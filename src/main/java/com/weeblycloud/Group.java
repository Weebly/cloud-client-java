package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

/**
* Represents a group of members of a Weebly Cloud site.
*/
public class Group extends CloudResource implements Deletable, Mutable{
    private String userId;
    private String siteId;
    private String groupId;

    /**
     * Creates a Group object.
     *
     * @param userId ID of the user this group belongs to.
     * @param siteId ID of the site this group belongs to.
     * @param groupId ID of an existing group.
     * @param initialize Whether or not to retrieve this group's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the Group's properties if initialize
     *          is false.
     */
    public Group(
        String userId,
        String siteId,
        String groupId,
        boolean initialize,
        JsonObject existing
    ) throws CloudException {
        super("user/"+userId+"/site/"+siteId+"/group/"+groupId, initialize, existing);
        this.userId = userId;
        this.siteId = siteId;
        this.groupId = groupId;
    }

    /**
     * Creates a Group object.
     *
     * @param userId ID of the user this group belongs to.
     * @param siteId ID of the site this group belongs to.
     * @param groupId ID of an existing group.
     * @param initialize Whether or not to retrieve this group's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public Group(
        String userId,
        String siteId,
        String groupId,
        boolean initialize
    ) throws CloudException {
        this(userId, siteId, groupId, initialize, null);
    }

    /**
     * Creates a Group object.
     *
     * @param userId ID of the user this group belongs to.
     * @param siteId ID of the site this group belongs to.
     * @param groupId ID of an existing group.
     */
    public Group(String userId, String siteId, String groupId) throws CloudException {
        this(userId, siteId, groupId, true, null);
    }

    /**
     * Converts a JSON response into an array of
     * Group objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the Groups
     *              (userId and siteId).
     * @param json JSON representation of a list of groups.
     */
    public static ArrayList<Group> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<Group> groupList = new ArrayList<Group>();
        JsonArray groups = json.getAsJsonArray();

        for (JsonElement group : groups) {
            String id = group.getAsJsonObject().get("group_id").getAsString();
            groupList.add(new Group(ids[0], ids[1], id, false, group.getAsJsonObject()));
        }

        return groupList;
    }
}