package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/**
* Represents a Weebly Cloud user.
*/
public class User extends CloudResource implements Mutable {
    private String userId;

    /**
     * Creates a User object.
     *
     * @param userId ID of an existing user.
     * @param initialize Whether or not to retrieve this user's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the User's properties if initialize
     *          is false.
     */
    public User(String userId, boolean initialize, JsonObject existing)
            throws CloudException {
        super("user/"+userId, initialize, existing);
        this.userId = userId;
    }

    /**
     * Creates a User object.
     *
     * @param userId ID of an existing user.
     * @param initialize Whether or not to retrieve this user's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public User(String userId, boolean initialize) throws CloudException {
        this(userId, initialize, null);
        this.userId = userId;
    }

    /**
     * Creates a User object.
     *
     * @param userId ID of an existing user.
     */
    public User(String userId) throws CloudException {
        this(userId, true, null);
    }

    /**
     * Extracts properties from User's unique JSON response format.
     *
     * @param json JSON from an API response.
     */
    public JsonObject propertiesFromJson(JsonObject json) {
        return json.getAsJsonObject("user");
    }

    /**
     * Enables a user account after an account has been
     * disabled. Enabling a user account will allow that user
     * to log in and edit their sites. When a user is created,
     * their account is automatically enabled.
     */
    public void enable() throws CloudException {
        CloudClient client = CloudClient.getClient();
        client.post(this.url + "/enable");
    }

    /**
     * Disables a user account, preventing them from
     * logging in or editing their sites.
     */
    public void disable() throws CloudException {
        CloudClient client = CloudClient.getClient();
        client.post(this.url + "/disable");
    }

    /**
     * Generates a one-time login link. Will return an error
     * if the user has been disabled.
     */
    public String loginLink() throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.post(this.url + "/loginLink");
        return res.body.getAsJsonObject().get("link").getAsString();
    }

    /**
     * Returns an array of themes available to this
     * user. The themes in the array are NOT resource
     * objects, but are constructed directly from the
     * response JSON.
     *
     * @param searchParams Search parameters. For valid
     * parameters, see the API documentation.
     */
    public ArrayList<JsonObject> getAvailableThemes(HashMap<String, Object> searchParams)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/theme", searchParams);
        ArrayList<JsonObject> themeList = new ArrayList<JsonObject>();
        JsonArray themes = res.body.getAsJsonObject().getAsJsonArray("data");

        for (JsonElement theme : themes) {
            themeList.add(theme.getAsJsonObject());
        }

        return themeList;
    }

    /**
     * Returns an array of themes available to this
     * user. The themes in the array are NOT resource
     * objects, but are constructed directly from the
     * response JSON.
     */
    public ArrayList<JsonObject> getAvailableThemes() throws CloudException {
        return getAvailableThemes(new HashMap<String, Object>());
    }

    /**
     * Adds a custom theme to a user.
     *
     * @param name The name of the theme.
     * @param zipUrl The URL of the .zip file containing the theme.
     *              Must be publicly accessible.
     */
    public void createCustomTheme(String name, String zipUrl) throws CloudException {
        CloudClient client = CloudClient.getClient();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", name);
        data.put("zip_url", zipUrl);
        client.post(this.url + "/theme", data);
    }

    /**
     * Creates a new Site belonging to this user in the database.
     *
     * @param domain The domain of the new site.
     * @param data Hash map of site properties. For the allowed
     *          properties, see the API documentation.
     */
    public Site createSite(String domain, HashMap<String, Object> data)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        data.put("domain", domain);
        CloudResponse res = client.post(this.url+"/site",data);
        JsonObject siteData = res.body.getAsJsonObject().getAsJsonObject("site");
        return new Site(this.userId, siteData.get("site_id").getAsString(), false, siteData);

    }

    /**
     * Creates a new Site belonging to this user in the database.
     *
     * @param domain The domain of the new site.
     */
    public Site createSite(String domain) throws CloudException {
        return createSite(domain, new HashMap<String, Object>());
    }

    /**
     * Returns a CloudList of Sites belonging to this user.
     *
     * @param searchParams Search query parameters. See the API documentation
     *              for valid parameters.
     */
    public CloudList<Site> listSites(HashMap<String, Object> searchParams)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/site", searchParams);
        return new CloudList<Site>(res, new String[]{this.userId}, Site::arrayFromJson);
    }

    /**
     * Returns a CloudList of Sites belonging to this user.
     */
    public CloudList<Site> listSites() throws CloudException {
        return listSites(new HashMap<String, Object>());
    }

    /**
    * Get the site with the specified ID. The site must belong to this user.
    *
    * @param siteId The ID of the site to retrieve.
    */
    public Site getSite(String siteId) throws CloudException {
        return new Site(this.userId, siteId);
    }
}
