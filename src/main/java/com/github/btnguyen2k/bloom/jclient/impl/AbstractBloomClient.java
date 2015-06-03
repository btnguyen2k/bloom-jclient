package com.github.btnguyen2k.bloom.jclient.impl;

import com.github.btnguyen2k.bloom.jclient.IBloomClient;

/**
 * Abstract implementation of {@link IBloomClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractBloomClient implements IBloomClient {

    public AbstractBloomClient init() {
        return this;
    }

    public void destroy() {
    }

}
