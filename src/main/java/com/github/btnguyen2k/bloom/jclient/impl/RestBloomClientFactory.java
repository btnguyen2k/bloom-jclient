package com.github.btnguyen2k.bloom.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link RestBloomClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RestBloomClientFactory {
    private static LoadingCache<String, RestBloomClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, RestBloomClient>() {
                @Override
                public void onRemoval(RemovalNotification<String, RestBloomClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, RestBloomClient>() {
                @Override
                public RestBloomClient load(String bloomServerUrl) throws Exception {
                    RestBloomClient bloomClient = new RestBloomClient();
                    bloomClient.setBloomServerUrl(bloomServerUrl).init();
                    return bloomClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link RestBloomClient} instance.
     * 
     * @param bloomServerUrl
     * @return
     */
    public static RestBloomClient newBloomClient(String bloomServerUrl) {
        try {
            return cache.get(bloomServerUrl);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
