package com.weeblycloud;

import com.weeblycloud.utils.*;
import com.google.gson.*;
import java.util.HashMap;

/**
* A resource whose properties can be accessed.
*/
public interface Accessible {
    /**
     * Gets the unique URL belonging to the resource.
     *
     * @return The unique URL belonging to the resource.
     */
    public String getUrl();

    /**
     * Gets the properties that have been changed.
     *
     * @return Properties that have been changed by setProperty.
     */
    public HashMap<String, Object> getChanged();
}