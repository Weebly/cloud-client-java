package com.weeblycloud.utils;

import com.google.gson.*;
import java.util.*;
import java.net.*;
import java.io.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

import java.math.BigInteger;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.config.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.*;

/**
 * CloudClient for accessing the Weebly Cloud API.
 */
public class CloudClient {
    /**
     * API domain
     */
    public static String BASE_URL = "api.weeblycloud.com/";

    /**
     * Admin API key
     */
    public String apiKey;

    /**
     * API secret key
     */
    public String apiSecret;

    /**
     * Instance of CloudClient
     */
    private static CloudClient instance;

    /**
     * Gets the instance of the CloudClient
     */
    public static CloudClient getClient() {
        if(instance==null) {
            return null;
        }

        return instance;
    }

    /**
     * Sets the instance of CloudClient using the API keys. Must
     * be called before making a request.
     *
     * @param apiKey
     * @param apiSecret
     */
    public static void setKeys(String apiKey, String apiSecret) {
        instance = new CloudClient(apiKey, apiSecret);
    }

    /**
     * Creates a new CloudClient object.
     *
     * @param apiKey
     * @param apiSecret
     */
    private CloudClient(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    /**
     * Makes a request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     * @param method The HTTP method for the request.
     * @param data The data to send in the request. Gets sent in the query
     *              string if the method is GET or DELETE, or in the request
     *              body if the method is POST, PUT, or PATCH.
     */
    public CloudResponse makeRequest(
        String url,
        String method,
        HashMap<String,Object> data
    ) throws CloudException {
        //Set the timeout for requests to 3 seconds
        CloseableHttpClient httpClient = buildHttpClientWithTimeout(3 * 1000);

        HttpRequestBase request = buildHttpRequest(url, method, data);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (java.net.SocketTimeoutException e) {
            throw new CloudException("Response timed out", 999);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CloudResponse cloudResponse = new CloudResponse(response, url, data);

        request.releaseConnection();

        try {
            httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cloudResponse;
    }

    /**
     * Sets the headers and body for an HTTP request.
     *
     * @param url The endpoint url, not including domain or query string.
     * @param method
     * @param data
     */
    private HttpRequestBase buildHttpRequest(String url, String method,
            HashMap<String,Object> data) {
        URIBuilder uriBuilder = (new URIBuilder())
            .setScheme("https")
            .setHost(BASE_URL)
            .setPath(url);

        //Send the data through either the request body
        //or the query parameters
        boolean dataInBody = Arrays
            .asList(new String[]{"POST","PUT","PATCH"})
            .contains(method);
        String content = "";
        if (dataInBody) {
            content = (new GsonBuilder()).create().toJson(data);
        } else {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        URI uri = null;
        try {
            uri = uriBuilder.build();
        } catch (java.net.URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //Select the HTTP method, defaulting to GET
        HttpRequestBase request;
        if (method.equals("DELETE")) {
            request = new HttpDelete(uri);
        } else if (method.equals("POST")) {
            request = new HttpPost(uri);
        } else if (method.equals("PATCH")) {
            request = new HttpPatch(uri);
        } else if (method.equals("PUT")) {
            request = new HttpPut(uri);
        } else {
            request = new HttpGet(uri);
        }
        
        if(dataInBody) {
            try {
                ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(content));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        request.addHeader("X-Public-Key", apiKey);
        request.addHeader(
            "X-Signed-Request-Hash",
            makeHash(method + "\n" + url + "\n" + content)
        );
        request.addHeader("Content-type", "application/json");
        request.addHeader("X-Client-Type", "java");
        request.addHeader("X-Client-Version", "1.0.0");

        return request;
    }

    private CloseableHttpClient buildHttpClientWithTimeout (int timeout) {
        RequestConfig requestConfig = RequestConfig
            .custom()
            .setConnectTimeout(timeout)
            .build();

        return HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    /**
     * Makes a GET request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     */
    public CloudResponse get(String url) throws CloudException {
        return get(url, new HashMap<String, Object>());
    }

    /**
     * Makes a GET request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     * @param parameters
     */
    public CloudResponse get(String url, HashMap<String, Object> parameters)
            throws CloudException {
        return makeRequest(url, "GET", parameters);
    }

    /**
     * Makes a DELETE request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     */
    public CloudResponse delete(String url) throws CloudException {
        return makeRequest(url, "DELETE", new HashMap<String, Object>());
    }

    /**
     * Makes a POST request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     */
    public CloudResponse post(String url) throws CloudException {
        return post(url, new HashMap<String, Object>());
    }

    /**
     * Makes a POST request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     * @param data
     */
    public CloudResponse post(String url, HashMap<String, Object> data)
            throws CloudException {
        return makeRequest(url, "POST", data);
    }

    /**
     * Makes a PATCH request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     * @param data
     */
    public CloudResponse patch(String url, HashMap<String, Object> data)
            throws CloudException {
        return makeRequest(url, "PATCH", data);
    }

    /**
     * Makes a PUT request to the Weebly Cloud API.
     *
     * @param url The endpoint url, not including domain or query string.
     * @param data
     */
    public CloudResponse put(String url, HashMap<String, Object> data)
            throws CloudException {
        return makeRequest(url, "PUT", data);
    }

    /**
     * Returns a hash of the private key and the message to
     * authenticate.
     *
     * @param message The message to hash with the secret key.
     */
    private String makeHash(String message) {
        String hashString = "";

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(apiSecret.getBytes("utf-8"), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(message.getBytes("utf-8"));

            String hexString = String.format("%064x", new BigInteger(1,hash));
            hashString = Base64.getEncoder().encodeToString(hexString.getBytes("utf-8"));
        } catch (java.security.GeneralSecurityException e){
            throw new RuntimeException(e);
        } catch (java.io.UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }

        return hashString;
    }

}
