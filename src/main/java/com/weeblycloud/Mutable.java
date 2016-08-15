package com.weeblycloud;

import com.weeblycloud.utils.*;

/**
* A resource whose properties can be changed.
*/
public interface Mutable extends Accessible{
    /**
     * Saves the changed properties to the database.
     */
    default void save() throws CloudException {
        CloudClient.getClient().patch(getUrl(), getChanged());
        getChanged().clear();
    }

    /**
     * Sets a property of the resource. This change is NOT
     * saved in the database until save() is called.
     *
     * @param property The name of the property to change.
     * @param value The property's new value.
     */
    default boolean setProperty(String property, Object value) {
        getChanged().put(property, value);

        return true;
    }
}