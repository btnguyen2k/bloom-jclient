package com.github.btnguyen2k.bloom.jclient.impl;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.btnguyen2k.bloom.jclient.BloomResponse;
import com.github.btnguyen2k.bloom.jclient.IBloomClient;
import com.github.btnguyen2k.bloomserver.thrift.TBloomResponse;
import com.github.btnguyen2k.bloomserver.thrift.TBloomService;
import com.github.ddth.thriftpool.AbstractTProtocolFactory;
import com.github.ddth.thriftpool.ITProtocolFactory;
import com.github.ddth.thriftpool.PoolConfig;
import com.github.ddth.thriftpool.ThriftClientPool;

/**
 * Thrift-over-http implementation of {@link IBloomClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftHttpBloomClient extends AbstractBloomClient {

    private Logger LOGGER = LoggerFactory.getLogger(ThriftHttpBloomClient.class);

    private String bloomServerUrls = "http://localhost:9000/thrift";
    private ThriftClientPool<TBloomService.Client, TBloomService.Iface> thriftClientPool;

    /**
     * Constructs a new {@link ThriftHttpBloomClient} object.
     */
    public ThriftHttpBloomClient() {
    }

    /**
     * Constructs a new {@link ThriftHttpBloomClient} object.
     * 
     * @param bloomServerUrls
     *            format
     *            {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     */
    public ThriftHttpBloomClient(String bloomServerUrls) {
        setBloomServerUrls(bloomServerUrls);
    }

    /**
     * Format
     * {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     * 
     * @return
     */
    public String getBloomServerUrls() {
        return bloomServerUrls;
    }

    /**
     * format
     * {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     * 
     * @param bloomServerUrls
     * @return
     */
    public ThriftHttpBloomClient setBloomServerUrls(String bloomServerUrls) {
        this.bloomServerUrls = bloomServerUrls;
        return this;
    }

    private static class MyTProtocolFactory extends AbstractTProtocolFactory {
        public MyTProtocolFactory(String bloomServerUrls) {
            super(bloomServerUrls);
        }

        /**
         * {@inheritDoc}
         */
        protected void parseHostAndPortList() {
            String[] urlTokens = getHostsAndPorts().split("[,\\s]+");

            clearHostAndPortList();
            for (String url : urlTokens) {
                HostAndPort hap = new HostAndPort(url);
                addHostAndPort(hap);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected TProtocol create(HostAndPort hostAndPort) throws Exception {
            SocketConfig config = SocketConfig.custom().setSoTimeout(10000).build();
            CloseableHttpClient httpClient = HttpClients.custom().disableAuthCaching()
                    .disableCookieManagement().setDefaultSocketConfig(config).build();
            // .createMinimal();
            TTransport transport = new THttpClient(hostAndPort.host, httpClient);
            try {
                transport.open();
            } catch (TTransportException e) {
                transport.close();
                throw e;
            }
            TProtocol protocol = new TCompactProtocol(transport);
            return protocol;
        }
    }

    /**
     * Helper method to create a new {@link ITProtocolFactory} for bloom-server
     * Thrift-over-http client.
     * 
     * @param bloomServerUrls
     * @return
     */
    public static ITProtocolFactory protocolFactory(final String bloomServerUrls) {
        ITProtocolFactory protocolFactory = new MyTProtocolFactory(bloomServerUrls);
        return protocolFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThriftHttpBloomClient init() {
        super.init();

        final int timeout = 10000;
        thriftClientPool = new ThriftClientPool<TBloomService.Client, TBloomService.Iface>();
        thriftClientPool.setClientClass(TBloomService.Client.class).setClientInterface(
                TBloomService.Iface.class);
        thriftClientPool.setTProtocolFactory(protocolFactory(bloomServerUrls));
        thriftClientPool.setPoolConfig(new PoolConfig().setMaxActive(32).setMaxWaitTime(timeout));
        thriftClientPool.init();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (thriftClientPool != null) {
            try {
                thriftClientPool.destroy();
            } catch (Exception e) {
            }
        }

        super.destroy();
    }

    private static BloomResponse doResponse(TBloomResponse serverResponse) {
        if (serverResponse == null) {
            return doResponse(500, false, "Empty server response / Server-side exception.");
        }
        BloomResponse response = new BloomResponse(serverResponse.status, serverResponse.value,
                serverResponse.message);
        return response;
    }

    private static BloomResponse doResponse(int status, boolean value, String message) {
        BloomResponse response = new BloomResponse(status, value, message);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BloomResponse put(String item) {
        return put(item, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BloomResponse put(String item, String bloomName) {
        try {
            TBloomResponse serverResponse = null;
            TBloomService.Iface bloomClient = thriftClientPool.borrowObject();
            if (bloomClient != null) {
                try {
                    serverResponse = bloomClient.put(bloomName, item);
                } finally {
                    thriftClientPool.returnObject(bloomClient);
                }
            }
            return doResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return doResponse(500, false, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BloomResponse mightContain(String item) {
        return mightContain(item, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BloomResponse mightContain(String item, String bloomName) {
        try {
            TBloomResponse serverResponse = null;
            TBloomService.Iface bloomClient = thriftClientPool.borrowObject();
            if (bloomClient != null) {
                try {
                    serverResponse = bloomClient.mightContain(bloomName, item);
                } finally {
                    thriftClientPool.returnObject(bloomClient);
                }
            }
            return doResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return doResponse(500, false, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BloomResponse initBloom(String secret, String bloomName, long expectedNumItems,
            double expectedFpp, boolean force, boolean counting, boolean scaling) {
        try {
            TBloomResponse serverResponse = null;
            TBloomService.Iface bloomClient = thriftClientPool.borrowObject();
            if (bloomClient != null) {
                try {
                    serverResponse = bloomClient.initBloom(secret, bloomName, expectedNumItems,
                            expectedFpp, force, counting, scaling);
                } finally {
                    thriftClientPool.returnObject(bloomClient);
                }
            }
            return doResponse(serverResponse);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return doResponse(500, false, e.getMessage());
        }
    }

}
