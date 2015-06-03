package com.github.btnguyen2k.bloom.jclient.impl;

import java.util.HashMap;
import java.util.Map;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.btnguyen2k.bloom.jclient.BloomResponse;
import com.github.btnguyen2k.bloom.jclient.IBloomClient;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * REST-implementation of {@link IBloomClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RestBloomClient extends AbstractBloomClient {

    private Logger LOGGER = LoggerFactory.getLogger(RestBloomClient.class);

    private String bloomServerUrl;

    public RestBloomClient() {
    }

    public RestBloomClient(String bloomServerUrl) {
        setBloomServerUrl(bloomServerUrl);
    }

    public String getBloomServerUrl() {
        return bloomServerUrl;
    }

    public RestBloomClient setBloomServerUrl(String idServerUrl) {
        this.bloomServerUrl = idServerUrl;
        if (this.bloomServerUrl.endsWith("/")) {
            this.bloomServerUrl = this.bloomServerUrl
                    .substring(0, this.bloomServerUrl.length() - 1);
        }
        return this;
    }

    private Map<String, Object> callApi(String url) {
        return callApi(url, null, "GET");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callApi(String url, Object data, String method) {
        try {
            HttpRequest httpRequest = StringUtils.equalsIgnoreCase("POST", method) ? HttpRequest
                    .post(url) : HttpRequest.get(url);
            if (data != null) {
                httpRequest.body(SerializationUtils.toJsonString(data));
            }
            HttpResponse httpResponse = httpRequest.timeout(5000).send();
            try {
                if (httpResponse.statusCode() != 200) {
                    return null;
                }
                int contentLength = Integer.parseInt(httpResponse.contentLength());
                if (contentLength == 0 || contentLength > 1024) {
                    LOGGER.warn("Invalid response length: " + contentLength);
                    return null;
                }
                return SerializationUtils.fromJsonString(httpResponse.charset("UTF-8").bodyText(),
                        Map.class);
            } finally {
                httpResponse.close();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    private static BloomResponse doResponse(Map<String, Object> serverResponse) {
        if (serverResponse == null) {
            return doResponse(500, false, "Empty server response / Server-side exception.");
        }
        Integer status = DPathUtils.getValue(serverResponse, "s", Integer.class);
        Boolean value = DPathUtils.getValue(serverResponse, "v", Boolean.class);
        String message = DPathUtils.getValue(serverResponse, "m", String.class);

        BloomResponse response = new BloomResponse(status != null ? status.intValue() : 0,
                value != null ? value.booleanValue() : false, message);
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
        String apiUri = "/put/" + item;
        if (!StringUtils.isBlank(bloomName)) {
            apiUri += "/" + bloomName;
        }
        Map<String, Object> apiResult = callApi(bloomServerUrl + apiUri);
        return doResponse(apiResult);
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
        String apiUri = "/mightContain/" + item;
        if (!StringUtils.isBlank(bloomName)) {
            apiUri += "/" + bloomName;
        }
        Map<String, Object> apiResult = callApi(bloomServerUrl + apiUri);
        return doResponse(apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BloomResponse initBloom(String secret, String bloomName, long expectedNumItems,
            double expectedFpp, boolean force, boolean counting, boolean scaling) {
        String apiUri = "/initBloom";
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("secret", secret);
        data.put("bloom_name", bloomName);
        data.put("num_items", expectedNumItems);
        data.put("expected_fpp", expectedFpp);
        data.put("force", force);
        data.put("counting", counting);
        data.put("scaling", scaling);
        Map<String, Object> apiResult = callApi(bloomServerUrl + apiUri, data, "POST");
        return doResponse(apiResult);
    }

}
