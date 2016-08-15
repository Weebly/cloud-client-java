package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import com.google.gson.*;

/**
* Represents a Weebly Cloud admin account.
*/
public class Account extends CloudResource implements Mutable {
    /**
     * Creates an Account object.
     *
     * @param initialize Whether or not to retrieve this account's properties
     *      from the server upon instantiation. The properties can later
     *      be retrieved by calling get().
     * @throws CloudException
     */
    public Account(boolean initialize) throws CloudException {
        super("account", initialize, null);
    }

    /**
     * Creates a new Account object.
     * @throws CloudException
     */
    public Account() throws CloudException {
        this(true);
    }

    /**
     * Extracts properties from Account's unique JSON response format.
     *
     * @param json A JsonObject from the body of an API response.
     * @return A JsonObject with the account's properties.
     */
    public JsonObject propertiesFromJson(JsonObject json) {
        return json.getAsJsonObject("account");
    }

    /**
     * Creates a new user with the given email and properties.
     *
     * @param email The user's email. Must be unique.
     * @param data Hash map of user properties.
     * @return User The newly created user.
     * @throws CloudException
     */
    public User createUser(String email, HashMap<String, Object> data)
            throws CloudException {
        CloudClient client = CloudClient.getClient();

        data.put("email",email);
        
        CloudResponse res = client.post("user", data);
        JsonObject userData = res.body.getAsJsonObject().getAsJsonObject("user");

        return new User(userData.get("user_id").getAsString(), false, userData);
    }

    /**
     * Creates a new user with the given email.
     *
     * @param email The user's email. Must be unique.
     * @return User The newly created user.
     * @throws CloudException
     */
    public User createUser(String email) throws CloudException {
        return this.createUser(email, new HashMap<String, Object>());
    }

    /**
     * Returns a list of available plans.
     * @throws CloudException
     */
    public CloudList<Plan> listPlans() throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get("plan");
        
        return new CloudList<Plan>(res, new String[]{}, Plan::arrayFromJson);
    }

    /**
     * Gets a single Plan by ID.
     *
     * @param planId ID of the plan to return.
     * @return The Plan with the specified ID.
     * @throws CloudException
     */
    public Plan getPlan(String planId) throws CloudException {
        return new Plan(planId);
    }
}
