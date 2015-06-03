package com.github.btnguyen2k.bloom.jclient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Response from bloom-server API call.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class BloomResponse {

    public int status;
    public boolean value;
    public String message;

    public BloomResponse() {
    }

    public BloomResponse(int status, boolean value, String message) {
        this.status = status;
        this.value = value;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(null);
        tsb.append("status", status);
        tsb.append("value", value);
        tsb.append("message", message);
        return tsb.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(status);
        hcb.append(value);
        hcb.append(message);
        return hcb.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BloomResponse) {
            BloomResponse other = (BloomResponse) obj;

            EqualsBuilder eq = new EqualsBuilder();
            eq.append(status, other.status);
            eq.append(value, other.value);
            eq.append(message, other.value);
        }
        return false;
    }
}
