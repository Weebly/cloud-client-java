package com.weeblycloud;

import com.weeblycloud.utils.*;

/**
* A resource that can be deleted.
*/
public interface Deletable extends Accessible{
    /**
    * Deletes the resource from the database.
    */
    default boolean delete() throws CloudException {
        CloudClient.getClient().delete(getUrl());
        return true;
    }
}