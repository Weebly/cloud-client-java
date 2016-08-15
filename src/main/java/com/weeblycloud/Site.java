package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.*;

/**
* Represents a Weebly Cloud site.
*/
public class Site extends CloudResource implements Deletable, Mutable {
    private String userId;
    private String siteId;

    /**
     * Creates a Site object.
     *
     * @param userId ID of the user this site belongs to.
     * @param siteId ID of an existing site.
     * @param initialize Whether or not to retrieve this site's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the Site's properties if initialize
     *          is false.
     */
    public Site(String userId, String siteId, boolean initialize, JsonObject existing)
            throws CloudException {
        super("user/"+userId+"/site/"+siteId, initialize, existing);
        this.userId = userId;
        this.siteId = siteId;
    }

    /**
     * Creates a Site object.
     *
     * @param userId ID of the user this site belongs to.
     * @param siteId ID of an existing site.
     * @param initialize Whether or not to retrieve this site's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public Site(String userId, String siteId, boolean initialize) throws CloudException {
        this(userId, siteId, initialize, null);
    }

    /**
     * Creates a Site object.
     *
     * @param userId ID of the user this site belongs to.
     * @param siteId ID of an existing site.
     */
    public Site(String userId, String siteId) throws CloudException {
        this(userId, siteId, true, null);
    }

    /**
     * Extracts properties from Site's unique JSON response format.
     *
     * @param json JSON from an API response.
     */
    public JsonObject propertiesFromJson(JsonObject json) {
        return json.getAsJsonObject("site");
    }

    /**
    * Publishes a site.
    */
    public void publish() throws CloudException {
        CloudClient client = CloudClient.getClient();
        client.post(this.url + "/publish");
    }

    /**
    * Unpublishes a site.
    */
    public void unpublish() throws CloudException {
        CloudClient client = CloudClient.getClient();
        client.post(this.url + "/unpublish");
    }

    /**
     * Generates a one-time login link for the user that
     * automatically redirects them to the site editor for
     * this site.
     */
    public String loginLink() throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/loginLink");
        return res.body.getAsJsonObject().get("link").getAsString();
    }

    /**
     * Sets publish credentials for a given site. If a user's site
     * will not be hosted by Weebly, publish credentials can be
     * provided. When these values are set, the site will be
     * published to the location specified.
     *
     * @param host The host server to publish the site to.
     * @param username The username for logging in to the host.
     * @param password The password for logging into the host.
     * @param path The file path to publish the site files to.
     */
    public void setPublishCredentials(String host, String username, String password,
        String path) throws CloudException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("publish_host", host);
        data.put("publish_username", username);
        data.put("publish_password", password);
        data.put("publish_path", path);

        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/setPublishCredentials", data);
    }

    /**
     * Restores a deleted site to the exact state it
     * was in when deleted.
     *
     * @param domain The domain of the site.
     */
    public void restore(String domain) throws CloudException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("domain", domain);

        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/restore", data);
    }

    /**
     * Enables a site, allowing it to be edited.
     */
    public void enable() throws CloudException {
        CloudClient client = CloudClient.getClient();
        client.post(this.url + "/enable");
    }


    /**
     * Disables a site, preventing the user from
     * accessing it through the editor.
     */
    public void disable() throws CloudException {
        CloudClient client = CloudClient.getClient();
        client.post(this.url + "/disable");
    }

    /**
    * Gets the Plan assigned to the site.
    */
    public Plan getPlan() throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/plan");
        return Plan.arrayFromJson(new String[]{}, res.body).get(0);
    }

    /**
    * Assigns a plan to a site.
    *
    * @param planId ID of the plan to assign to the site.
    * @param term The term length.
    */
    public void setPlan(String planId, int term) throws CloudException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("plan_id", planId);
        data.put("term", term);

        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/plan", data);
    }

    /**
    * Assigns a plan to a site. Defaults to a term length of
    * 1.
    *
    * @param planId ID of the plan to assign to the site.
    */
    public void setPlan(String planId) throws CloudException {
        setPlan(planId, 1);
    }

    /**
    * Assigns a theme to a site by ID.
    *
    * @param themeId ID of the theme to assign to the site.
    * @param isCustom Whether or not the theme is a custom theme.
    */
    public void setTheme(String themeId, boolean isCustom) throws CloudException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("theme_id", themeId);
        data.put("is_custom", isCustom);

        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/theme", data);
    }


    //Blog methods
    /**
    * Reurns a CloudList of Blogs belonging to this site.
    */
    public CloudList<Blog> listBlogs() throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/blog");
        return new CloudList<Blog>(res,
            new String[]{this.userId, this.siteId}, Blog::arrayFromJson);
    }

    /**
    * Returns the Blog with the given ID. Must belong to
    * this site.
    *
    * @param blogId ID of the blog to return.
    */
    public Blog getBlog(String blogId) throws CloudException {
        return new Blog(this.userId, this.siteId, blogId);
    }


    //Form methods
    /**
     * Returns a CloudList of Forms on this Site.
     *
     * @param searchParams Search query parameters. See the API documentation
     *              for valid parameters.
     */
    public CloudList<Form> listForms(HashMap<String, Object> searchParams)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/form", searchParams);
        return new CloudList<Form>(res,
            new String[]{this.userId, this.siteId}, Form::arrayFromJson);
    }

    /**
     * Returns a CloudList of Forms on this Site.
     */
    public CloudList<Form> listForms() throws CloudException {
        return listForms(new HashMap<String, Object>());
    }

    /**
    * Returns the Form with the given ID. Must belong to
    * this site.
    *
    * @param formId ID of the form to return.
    */
    public Form getForm(String formId) throws CloudException {
        return new Form(this.userId, this.siteId, formId);
    }


    //Page methods
    /**
     * Returns a CloudList of Pages on this Site.
     *
     * @param searchParams Search query parameters. See the API documentation
     *              for valid parameters.
     */
    public CloudList<Page> listPages(HashMap<String, Object> searchParams)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/page", searchParams);
        return new CloudList<Page>(res,
            new String[]{this.userId, this.siteId}, Page::arrayFromJson);
    }

    /**
     * Returns a CloudList of Pages on this Site.
     */
    public CloudList<Page> listPages() throws CloudException {
        return listPages(new HashMap<String, Object>());
    }

    /**
    * Returns the Page with the given ID. Must belong to
    * this site.
    *
    * @param pageId ID of the page to return.
    */
    public Page getPage(String pageId) throws CloudException {
        return new Page(this.userId, this.siteId, pageId);
    }


    //Group methods
    /**
     * Returns a CloudList of Groups on this Site.
     *
     * @param searchParams Search query parameters. See the API documentation
     *              for valid parameters.
     */
    public CloudList<Group> listGroups(HashMap<String, Object> searchParams)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/group", searchParams);
        return new CloudList<Group>(res,
            new String[]{this.userId, this.siteId}, Group::arrayFromJson);
    }

    /**
     * Returns a CloudList of Groups on this Site.
     */
    public CloudList<Group> listGroups() throws CloudException {
        return listGroups(new HashMap<String, Object>());
    }

    /**
    * Returns the Group with the given ID. Must belong to
    * this site.
    *
    * @param groupId ID of the group to return.
    */
    public Group getGroup(String groupId) throws CloudException {
        return new Group(this.userId, this.siteId, groupId);
    }

    /**
    * Creates a new Group on the site with the specified name.
    *
    * @param name The name of the new group.
    */
    public Group createGroup(String name) throws CloudException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", name);

        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/group", data);
        JsonObject group = res.body.getAsJsonObject();
        return new Group(this.userId, this.siteId, group.get("group_id").getAsString(),
            false, group);
    }


    //Member methods
    /**
     * Returns a CloudList of Members on this Site.
     *
     * @param searchParams Search query parameters. See the API documentation
     *              for valid parameters.
     */
    public CloudList<Member> listMembers(HashMap<String, Object> searchParams)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/member", searchParams);
        return new CloudList<Member>(res,
            new String[]{this.userId, this.siteId}, Member::arrayFromJson);
    }

    /**
     * Returns a CloudList of Members on this Site.
     */
    public CloudList<Member> listMembers() throws CloudException {
        return listMembers(new HashMap<String, Object>());
    }

    /**
    * Returns the Member with the given ID. Must belong to
    * this site.
    *
    * @param memberId ID of the member to return.
    */
    public Member getMember(String memberId) throws CloudException {
        return new Member(this.userId, this.siteId, memberId);
    }

    /**
    * Creates a new Member on the site with the specified data. For
    * required properties, see the API documentation.
    *
    * @param data The properties of the new group.
    */
    public Member createMember(HashMap<String, Object> data) throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/member", data);

        JsonObject member = res.body.getAsJsonObject();
        return new Member(
            this.userId,
            this.siteId,
            member.get("member_id").getAsString(),
            false,
            member
        );
    }


    /**
     * Converts a JSON response into an array of
     * Site objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the Sites
     *              (userId).
     * @param json JSON representation of a list of sites.
     */
    public static ArrayList<Site> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<Site> siteList = new ArrayList<Site>();
        JsonArray sites = json.getAsJsonObject().getAsJsonArray("sites");

        for (JsonElement site : sites) {
            String id = site.getAsJsonObject().get("site_id").getAsString();
            siteList.add(new Site(ids[0], id, false, site.getAsJsonObject()));
        }

        return siteList;
    }
}
