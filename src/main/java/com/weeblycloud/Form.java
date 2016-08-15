package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.*;

/**
* Represents a form on a Weebly Cloud site.
*/
public class Form extends CloudResource {
    private String userId;
    private String siteId;
    private String formId;

    /**
     * Creates a Form object.
     *
     * @param userId ID of the user this form belongs to.
     * @param siteId ID of the site this form belongs to.
     * @param formId ID of an existing form.
     * @param initialize Whether or not to retrieve this form's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the Form's properties if initialize
     *          is false.
     */
    public Form(String userId, String siteId, String formId,
        boolean initialize, JsonObject existing) throws CloudException {
        super("user/"+userId+"/site/"+siteId+"/form/"+formId, initialize, existing);
        this.userId = userId;
        this.siteId = siteId;
        this.formId = formId;
    }

    /**
     * Creates a Form object.
     *
     * @param userId ID of the user this form belongs to.
     * @param siteId ID of the site this form belongs to.
     * @param formId ID of an existing form.
     * @param initialize Whether or not to retrieve this form's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public Form(String userId, String siteId, String formId, boolean initialize) 
            throws CloudException {
        this(userId, siteId, formId, initialize, null);
    }

    /**
     * Creates a Form object.
     *
     * @param userId ID of the user this form belongs to.
     * @param siteId ID of the site this form belongs to.
     * @param formId ID of an existing form.
     */
    public Form(String userId, String siteId, String formId) throws CloudException {
        this(userId, siteId, formId, true, null);
    }

    /**
     * Returns a CloudList of FormEntries on this Form.
     *
     * @param searchParams Search query parameters. See the API documentation
     *              for valid parameters.
     */
    public CloudList<FormEntry> listFormEntries(HashMap<String, Object> searchParams)
            throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/entry");
        return new CloudList<FormEntry>(
            res,
            new String[]{this.userId, this.siteId, this.formId},
            FormEntry::arrayFromJson
        );
    }

    /**
     * Returns a CloudList of FormEntries on this Form.
     */
    public CloudList<FormEntry> listFormEntries() throws CloudException {
        return listFormEntries(new HashMap<String, Object>());
    }

    /**
    * Returns the FormEntry with the given ID. Must belong to
    * this form.
    *
    * @param entryId ID of the form entry to return.
    */
    public FormEntry getFormEntry(String entryId) throws CloudException {
        return new FormEntry(this.userId, this.siteId, this.formId, entryId);
    }

    /**
     * Converts a JSON response into an array of
     * Form objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the Forms
     *              (userId and siteId).
     * @param json JSON representation of a list of forms.
     */
    public static ArrayList<Form> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<Form> formList = new ArrayList<Form>();
        JsonArray forms = json.getAsJsonArray();

        for (JsonElement form : forms) {
            String id = form.getAsJsonObject().get("form_id").getAsString();
            formList.add(new Form(ids[0], ids[1], id, false, form.getAsJsonObject()));
        }

        return formList;
    }
}