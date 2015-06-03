package com.github.btnguyen2k.bloom.jclient;

/**
 * Client API to interact with bloom-server.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IBloomClient {
    /**
     * Puts an item to the bloom filter.
     * 
     * @param item
     * @return
     */
    public BloomResponse put(String item);

    /**
     * Puts an item to a specified bloom filter.
     * 
     * @param item
     * @param bloomName
     * @return
     */
    public BloomResponse put(String item, String bloomName);

    /**
     * Tests if an item has been put to the bloom filter.
     * 
     * @param item
     * @return
     */
    public BloomResponse mightContain(String item);

    /**
     * Tests if an item has been put to a specified bloom filter.
     * 
     * @param item
     * @param bloomName
     * @return
     */
    public BloomResponse mightContain(String item, String bloomName);

    /**
     * Creates and Initializes a new bloom filter.
     * 
     * @param secret
     * @param bloomName
     * @param numItems
     * @param expectedFpp
     * @param force
     *            set to {@code true} to force overriding existing bloom filter
     * @param counting
     *            (not supported yet)
     * @param scaling
     *            (not supported yet)
     */
    public BloomResponse initBloom(String secret, String bloomName, long expectedNumItems,
            double expectedFpp, boolean force, boolean counting, boolean scaling);
}
