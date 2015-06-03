package com.github.btnguyen2k.bloom.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link ThriftHttpBloomClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftHttpBloomClientFactory {
    private static LoadingCache<String, ThriftHttpBloomClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, ThriftHttpBloomClient>() {
                @Override
                public void onRemoval(
                        RemovalNotification<String, ThriftHttpBloomClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, ThriftHttpBloomClient>() {
                @Override
                public ThriftHttpBloomClient load(String urls) throws Exception {
                    ThriftHttpBloomClient bloomClient = new ThriftHttpBloomClient(urls);
                    bloomClient.init();
                    return bloomClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link ThriftHttpBloomClient} instance.
     * 
     * @param urls
     *            format
     *            {@code http://host1:port1/uri1,https://host2:port2/uri2,http://host3:port3/uri3...}
     * @return
     */
    public static ThriftHttpBloomClient newBloomClient(String urls) {
        try {
            return cache.get(urls);
        } catch (ExecutionException e) {
            return null;
        }
    }

}
