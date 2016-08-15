package com.weeblycloud.utils;

import org.apache.http.*;
import java.util.HashMap;
import com.google.gson.*;

import java.net.*;
import java.io.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.config.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.*;

/**
 * A response from the Weebly Cloud API.
 */
public class CloudResponse{
    /**
     * The body of the response.
     */
    public JsonElement body;

    /**
     * The endpoint URL used to retrieve the response.
     */
    public String url;

    /**
     * The size of the result set, which is NOT
     * necessarily the size of the body, if the
     * result is paginated.
     */
    public int total;

    /**
     * The page of the result.
     */
    public int page;

    /**
     * The total number of pages.
     */
    public int pageCount;

    /**
     * The maximum number of results per page.
     */
    public int limit;

    /**
     * The query parameters used to retrieve the response.
     */
    public HashMap<String, Object> parameters;

    /**
     * Whether or not the response is paginated.
     */
    public boolean isPaginated;

    /**
     * Creates a CloudResponse.
     *
     * @param response The body of the response in JSON format.
     * @param url The endpoint URL used to retrieve the response
     * @param parameters The query parameters used to retrieve the response.
     */
    public CloudResponse(CloseableHttpResponse response, String url,
            HashMap<String,Object> parameters) throws CloudException {
        this.url = url;
        this.parameters = parameters;       
        int statusCode = response.getStatusLine().getStatusCode();
        
        //Get the JSON body of the response
        //unless the status code is 204
        HttpEntity myEntity = response.getEntity();
        String bodyJson = "";
        if(statusCode != 204) {
            try {
                bodyJson = EntityUtils.toString(myEntity);
                EntityUtils.consume(myEntity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JsonElement body = new JsonParser().parse(bodyJson);

        if ((body.isJsonObject() && body.getAsJsonObject().has("error"))) {
            JsonObject error = body.getAsJsonObject().getAsJsonObject("error");
            throw new CloudException(error);
        } else if (response.getStatusLine().getStatusCode() >= 400) {
            throw new CloudException("Error performing request",
                response.getStatusLine().getStatusCode());
        }

        this.body = body;

        //Process the headers that are returned from paginated endpoints
        Header totalHeader = response.getFirstHeader("X-Resultset-Total");
        Header limitHeader = response.getFirstHeader("X-Resultset-Limit");
        Header pageHeader = response.getFirstHeader("X-Resultset-Page");
        this.total = (totalHeader != null)? Integer.parseInt(totalHeader.getValue()) : -1;
        this.page = (pageHeader != null) ? Integer.parseInt(pageHeader.getValue()) : -1;
        this.limit = (limitHeader != null) ? Integer.parseInt(limitHeader.getValue()) : -1;
        this.isPaginated = (page > 0);
        this.pageCount = (int) Math.ceil(((float) total) / limit);
    }

    /**
     * Gets the next page, if it exists.
     *
     * @return The CloudResponse containing the next page
     * if there is a next page, or null if there is no next page.
     */
    public CloudResponse nextPage() throws CloudException {
        if (!isPaginated || (page >= pageCount)) {
            return null;
        }

        parameters.put("page",page+1);
        return CloudClient.getClient().get(url, parameters);
    }

    /**
     * Gets the previous page, if it exists.
     *
     * @return The CloudResponse containing the previous page
     * if there is a previous page, null if there is no previous page.
     */
    public CloudResponse previousPage() throws CloudException {
        if (!isPaginated || (page <= 1)) {
            return null;
        }

        parameters.put("page",page-1);
        return CloudClient.getClient().get(url, parameters);
    }

    /**
     * Returns the response body in JSON format.
     */
    public String toString() {
        return this.body.toString();
    }
}