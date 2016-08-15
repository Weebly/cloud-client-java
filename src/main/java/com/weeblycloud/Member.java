package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.*;

/**
* Represents a member of a Weebly Cloud site.
*/
public class Member extends CloudResource implements Deletable, Mutable{
    private String userId;
    private String siteId;
    private String memberId;

    /**
     * Creates a Member object.
     *
     * @param userId ID of the user this member belongs to.
     * @param siteId ID of the site this member belongs to.
     * @param memberId ID of an existing member.
     * @param initialize Whether or not to retrieve this member's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the Member's properties if initialize
     *          is false.
     */
    public Member(String userId, String siteId, String memberId, boolean initialize, 
            JsonObject existing) throws CloudException {
        super("user/"+userId+"/site/"+siteId+"/member/"+memberId, initialize, existing);
        this.userId = userId;
        this.siteId = siteId;
        this.memberId = memberId;
    }

    /**
     * Creates a Member object.
     *
     * @param userId ID of the user this member belongs to.
     * @param siteId ID of the site this member belongs to.
     * @param memberId ID of an existing member.
     * @param initialize Whether or not to retrieve this member's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public Member(String userId, String siteId, String memberId, boolean initialize) 
            throws CloudException {
        this(userId, siteId, memberId, initialize, null);
    }

    /**
     * Creates a Member object.
     *
     * @param userId ID of the user this member belongs to.
     * @param siteId ID of the site this member belongs to.
     * @param memberId ID of an existing member.
     */
    public Member(String userId, String siteId, String memberId) throws CloudException {
        this(userId, siteId, memberId, true, null);
    }

    /**
     * Converts a JSON response into an array of
     * Member objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the Members
     *              (userId and siteId).
     * @param json JSON representation of a list of members.
     */
    public static ArrayList<Member> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<Member> memberList = new ArrayList<Member>();
        JsonArray members = json.getAsJsonArray();

        for (JsonElement member : members) {
            String id = member.getAsJsonObject().get("member_id").getAsString();
            memberList.add(
                new Member(ids[0], ids[1], id, false, member.getAsJsonObject())
            );
        }

        return memberList;
    }
}