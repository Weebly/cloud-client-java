package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.*;

/**
* Represents a page on a Weebly Cloud site.
*/
public class Page extends CloudResource implements Mutable{
    private String userId;
    private String siteId;
    private String pageId;

    /**
     * Creates a Page object.
     *
     * @param userId ID of the user this page belongs to.
     * @param siteId ID of the site this page belongs to.
     * @param pageId ID of an existing page.
     * @param initialize Whether or not to retrieve this page's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the Page's properties if initialize
     *          is false.
     */
    public Page(String userId, String siteId, String pageId, boolean initialize, 
            JsonObject existing) throws CloudException {
        super("user/"+userId+"/site/"+siteId+"/page/"+pageId, initialize, existing);
        this.userId = userId;
        this.siteId = siteId;
        this.pageId = pageId;
    }

    /**
     * Creates a Page object.
     *
     * @param userId ID of the user this page belongs to.
     * @param siteId ID of the site this page belongs to.
     * @param pageId ID of an existing page.
     * @param initialize Whether or not to retrieve this page's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public Page(String userId, String siteId, String pageId, boolean initialize) 
            throws CloudException {
        this(userId, siteId, pageId, initialize, null);
    }

    /**
     * Creates a Page object.
     *
     * @param userId ID of the user this page belongs to.
     * @param siteId ID of the site this page belongs to.
     * @param pageId ID of an existing page.
     */
    public Page(String userId, String siteId, String pageId) throws CloudException {
        this(userId, siteId, pageId, true, null);
    }

    /**
     * Converts a JSON response into an array of
     * Page objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the Pages
     *              (userId and siteId).
     * @param json JSON representation of a list of pages.
     */
    public static ArrayList<Page> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<Page> pageList = new ArrayList<Page>();
        JsonArray pages = json.getAsJsonArray();

        for (JsonElement page : pages) {
            String id = page.getAsJsonObject().get("page_id").getAsString();
            pageList.add(new Page(ids[0], ids[1], id, false, page.getAsJsonObject()));
        }

        return pageList;
    }
}