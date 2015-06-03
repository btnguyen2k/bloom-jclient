package com.github.btnguyen2k.bloom.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link ThriftBloomClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftBloomClientFactory {
    private static LoadingCache<String, ThriftBloomClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, ThriftBloomClient>() {
                @Override
                public void onRemoval(RemovalNotification<String, ThriftBloomClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, ThriftBloomClient>() {
                @Override
                public ThriftBloomClient load(String hostsAndPorts) throws Exception {
                    ThriftBloomClient bloomClient = new ThriftBloomClient(hostsAndPorts);
                    bloomClient.init();
                    return bloomClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link ThriftBloomClient} instance.
     * 
     * @param hostsAndPorts
     *            format {@code host1:port1,host2:port2,host3:port3...}
     * @return
     */
    public static ThriftBloomClient newBloomClient(String hostAndPort) {
        try {
            return cache.get(hostAndPort);
        } catch (ExecutionException e) {
            return null;
        }
    }

}
