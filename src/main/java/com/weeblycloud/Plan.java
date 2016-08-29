package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

/**
* Represents a plan that can be applied to a Weebly Cloud site.
*/
public class Plan extends CloudResource {
    private String planId;

    /**
     * Creates a Plan object.
     *
     * @param planId ID of an existing plan.
     * @param initialize Whether or not to retrieve this plan's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the Plan's properties if initialize
     *          is false.
     */
    public Plan(String planId, boolean initialize, JsonObject existing)
            throws CloudException {
        super("plan/"+planId, initialize, existing);
        this.planId = planId;
    }

    /**
     * Extracts properties from Plan's unique JSON response format.
     *
     * @param json JSON from an API response.
     */
    public JsonObject propertiesFromJson(JsonObject json) {
        JsonObject properties = null;
        JsonObject plans = json.get("plans").getAsJsonObject();
        for (Map.Entry plan : plans.entrySet()) {
            properties = (JsonObject)plan.getValue();
        }
        return properties;
    }

    /**
     * Creates a Plan object.
     *
     * @param planId ID of an existing plan.
     * @param initialize Whether or not to retrieve this plan's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public Plan(String planId, boolean initialize) throws CloudException {
        this(planId, initialize, null);
    }

    /**
     * Creates a Plan object.
     *
     * @param planId ID of an existing plan.
     */
    public Plan(String planId) throws CloudException {
        this(planId, true, null);
    }

    /**
     * Converts a JSON response into an array of
     * Plan objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the Plans
     *              (none).
     * @param json JSON representation of a list of plans.
     */
    public static ArrayList<Plan> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<Plan> planList = new ArrayList<Plan>();
        JsonObject plans = json.getAsJsonObject().get("plans").getAsJsonObject();
        
        for (Map.Entry plan : plans.entrySet()) {
            planList.add(
                new Plan((String)plan.getKey(), false, (JsonObject)plan.getValue())
            );
        }
        return planList;
    }
}
