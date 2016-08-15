package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.*;

/**
* Represents an entry on a form in a Weebly Cloud site.
*/
public class FormEntry extends CloudResource {
    private String userId;
    private String siteId;
    private String formId;
    private String formEntryId;

    /**
     * Creates a FormEntry object.
     *
     * @param userId ID of the user this form entry belongs to.
     * @param siteId ID of the site this form entry belongs to.
     * @param formId ID of the form this form entry belongs to.
     * @param formEntryId ID of an existing form entry.
     * @param initialize Whether or not to retrieve this form entry's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the FormEntry's properties if initialize
     *          is false.
     */
    public FormEntry(String userId, String siteId, String formId, String formEntryId,
            boolean initialize, JsonObject existing) throws CloudException {
        super(
            "user/"+userId+"/site/"+siteId+"/form/"+formId+"/entry/"+formEntryId,
            initialize,
            existing
        );
        this.userId = userId;
        this.siteId = siteId;
        this.formId = formId;
        this.formEntryId = formEntryId;
    }

    /**
     * Creates a FormEntry object.
     *
     * @param userId ID of the user this form entry belongs to.
     * @param siteId ID of the site this form entry belongs to.
     * @param formId ID of the form this form entry belongs to.
     * @param formEntryId ID of an existing form entry.
     * @param initialize Whether or not to retrieve this form entry's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public FormEntry(String userId, String siteId, String formId, String formEntryId, 
            boolean initialize) throws CloudException {
        this(userId, siteId, formId, formEntryId, initialize, null);
    }

    /**
     * Creates a FormEntry object.
     *
     * @param userId ID of the user this form entry belongs to.
     * @param siteId ID of the site this form entry belongs to.
     * @param formId ID of the form this form entry belongs to.
     * @param formEntryId ID of an existing form entry.
     */
    public FormEntry(String userId, String siteId, String formId, String formEntryId) 
            throws CloudException {
        this(userId, siteId, formId, formEntryId, true, null);
    }

    /**
     * Converts a JSON response into an array of
     * FormEntry objects. Because the formatting of responses
     * and the IDs needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the FormEntries
     *              (userId, siteId, and formId).
     * @param json JSON representation of a list of form entries.
     */
    public static ArrayList<FormEntry> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<FormEntry> formEntryList = new ArrayList<FormEntry>();
        JsonArray formEntries = json.getAsJsonArray();

        for (JsonElement formEntry : formEntries) {
            String id = formEntry.getAsJsonObject().get("form_entry_id").getAsString();
            formEntryList.add(
                new FormEntry(ids[0], ids[1], ids[2], id, false, formEntry.getAsJsonObject())
            );
        }

        return formEntryList;
    }
}
