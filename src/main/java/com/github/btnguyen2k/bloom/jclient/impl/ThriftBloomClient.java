package com.github.btnguyen2k.bloom.jclient.impl;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.btnguyen2k.bloom.jclient.BloomResponse;
import com.github.btnguyen2k.bloomserver.thrift.TBloomResponse;
import com.github.btnguyen2k.bloomserver.thrift.TBloomService;
import com.github.ddth.thriftpool.AbstractTProtocolFactory;
import com.github.ddth.thriftpool.ITProtocolFactory;
import com.github.ddth.thriftpool.PoolConfig;
import com.github.ddth.thriftpool.ThriftClientPool;

/**
 * Thrift-implementation of {@link IBloomClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftBloomClient extends AbstractBloomClient {

    private Logger LOGGER = LoggerFactory.getLogger(ThriftBloomClient.class);

    private String bloomServerHostsAndPorts = "localhost:9090";
    private ThriftClientPool<TBloomService.Client, TBloomService.Iface> thriftClientPool;

    /**
     * Constructs a new {@link ThriftBloomClient} object.
     */
    public ThriftBloomClient() {
    }

    /**
     * Constructs a new {@link ThriftBloomClient} object.
     * 
     * @param bloomServerHostsAndPorts
     *            format {@code host1:port1,host2:port2,host3:port3...}
     */
    public ThriftBloomClient(String bloomServerHostsAndPorts) {
        setBloomServerHostsAndPorts(bloomServerHostsAndPorts);
    }

    public String getBloomServerHostsAndPorts() {
        return bloomServerHostsAndPorts;
    }

    public ThriftBloomClient setBloomServerHostsAndPorts(String bloomServerHostsAndPorts) {
        this.bloomServerHostsAndPorts = bloomServerHostsAndPorts;
        return this;
    }

    /**
     * Helper method to create a new {@link ITProtocolFactory} for bloom-server
     * Thrift client.
     * 
     * @param host
     * @param port
     * @return
     */
    public static ITProtocolFactory protocolFactory(final String hostsAndPorts, final int soTimeout) {
        ITProtocolFactory protocolFactory = new AbstractTProtocolFactory(hostsAndPorts) {
            @Override
            protected TProtocol create(HostAndPort hostAndPort) throws Exception {
                TSocket socket = new TSocket(hostAndPort.host, hostAndPort.port);
                socket.setTimeout(soTimeout);
                TTransport transport = new TFramedTransport(socket);
                try {
                    transport.open();
                } catch (TTransportException e) {
                    transport.close();
                    throw e;
                }
                TProtocol protocol = new TCompactProtocol(transport);
                return protocol;
            }
        };
        return protocolFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThriftBloomClient init() {
        super.init();

        final int timeout = 10000;
        thriftClientPool = new ThriftClientPool<TBloomService.Client, TBloomService.Iface>();
        thriftClientPool.setClientClass(TBloomService.Client.class).setClientInterface(
                TBloomService.Iface.class);
        thriftClientPool.setTProtocolFactory(protocolFactory(bloomServerHostsAndPorts, timeout));
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
