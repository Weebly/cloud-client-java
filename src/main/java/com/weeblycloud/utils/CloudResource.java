package com.weeblycloud.utils;

import com.weeblycloud.Accessible;
import java.util.HashMap;
import com.google.gson.*;

/**
* Represents a Weebly Cloud resource.
*/
public abstract class CloudResource  implements Accessible{
    /**
     * Unique URL of the resource.
     */
    protected String url;

    /**
     * Properties of the resource.
     */
    protected JsonObject properties;

    /**
     * Properties that have been changed by setProperty.
     */
    protected HashMap<String, Object> changed;

    /**
     * False if get() has not yet been called.
     */
    protected boolean got;

    public CloudResource(String url, boolean initialize, JsonObject existing)
            throws CloudException {
        this.changed = new HashMap<String, Object>();
        this.properties = new JsonObject();
        this.url = url;
        if (initialize) {
            this.get();
        } else if (existing != null){
            this.properties = existing;
        }
    }

    /**
     * Fetches the object's properties from the database.
     */
    public void get() throws CloudException {
        got = true;
        CloudResponse res = CloudClient.getClient().get(url);
        properties = propertiesFromJson(res.body.getAsJsonObject());
    }

    /**
     * Returns a property of the resource.
     *
     * @param property The property name.
     * @return The value of the property as a JsonElement. For how to convert 
     * this to other types, see the Google GSON documentation.
     */
    public JsonElement getProperty(String property) throws CloudException {
        if (!got && !properties.has(property)) {
            get();
        }

        return properties.get(property);
    }

    /**
     * Extracts properties from normal JSON response format;
     * this is overriden in classes with special formats.
     *
     * @param json The JSON object returned by the API call.
     */
    protected JsonObject propertiesFromJson(JsonObject json) {
        return json;
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, Object> getChanged() {
        return changed;
    }

    /**
    * @return JSON-encoded string of this resource's properties.
    */
    public String toString() {
        return properties.toString();
    }
}