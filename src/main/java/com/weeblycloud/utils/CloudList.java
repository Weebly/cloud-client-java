package com.weeblycloud.utils;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import com.google.gson.*;

/**
* List of CloudResources that hides pagination.
*/
public class CloudList<T extends CloudResource> implements Iterable<T> {

    /**
    * Interface to allow passing in a function that is used
    * to convert a JSON response into an ArrayList of
    * CloudResources.
    */
    public interface ConvertFunction <A,B,C> {
        public C apply (A a, B b) throws CloudException;
    }

    /**
     * A CloudResponse for getting the next page if needed. Replaced
     * each time nextPage() is called.
     */
    public CloudResponse res;

    /**
     * The array of CloudResources, which is appended to
     * each time nextPage() is called.
     */
    public ArrayList<T> list;

    /**
     * The index in list of the current
     * element.
     */
    private int index;

    /**
     * The size of the list. May be greater than list.size()
     * if the endpoint is paginated and not all pages have been loaded.
     */
    private int size;

    /**
     * The ids required to construct the CloudResources
     * in list.
     */
    private String[] ids;

    /**
     * Whether or not the CloudList is paginated.
     *
     * @var boolean is_paginated
     */
    private boolean isPaginated;

    /**
    * Function for converting a JSON response into
    * an ArrayList of CloudResources. Each class that supports
    * getting a list implements the static method arrayFromJson,
    * which is then passed to the CloudList constructor.
    */
    private ConvertFunction<String[], JsonElement, ArrayList<T>> arrayFromJson;

    /**
     * Creates a new CloudList object.
     *
     * @param res A CloudResponse returned by a GET call.
     * @param ids The ids required to construct the CloudResources in list
     *              (e.g. user_id for Site objects).
     * @param arrayFromJson Function for converting a JSON response into
     *          an ArrayList of CloudResources.
     */
    public CloudList(CloudResponse res, String[] ids,
            ConvertFunction<String[],JsonElement, ArrayList<T>> arrayFromJson)
            throws CloudException {
        this.res = res;
        this.ids = ids;
        this.arrayFromJson = arrayFromJson;
        this.list = arrayFromJson.apply(this.ids, this.res.body);
        this.index = -1;
        this.isPaginated = (this.res.isPaginated);
        this.size = this.isPaginated ? res.total : list.size();
    }

    /**
     * Fetches and appends the next page of responses to this.list. Returns true if
     * there is a next page, false otherwise.
     */
    public boolean nextPage() throws CloudException {
        res = res.nextPage();

        if (res != null) {
            list.addAll(arrayFromJson.apply(ids, res.body));
            return true;
        } else {
            return false;
        }
    }

    /**
     * The size of the list.
     */
    public int size() {
        return size;
    }

    /**
     * Whether or not the CloudList is paginated.
     */
    public boolean isPaginated() {
        return isPaginated;
    }

    /**
    * A string representation of the list's items.
    */
    public String toString() {
        return list.toString();
    }

    public Iterator<T> iterator() {
        Iterator<T> it = new Iterator<T>() {
            private int index = 0;

            public boolean hasNext() {
                try {
                    return ((index < (list.size())) || (isPaginated() && nextPage()));
                } catch (CloudException e) {
                    return false;
                }
            }

            public T next() {
                if (!hasNext()) {
                    throw new IndexOutOfBoundsException();
                }
                return list.get(index++);
            }
        };

        return it;
    }

}
