package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

/**
 * Represents a Weebly Cloud blog.
 */
public class Blog extends CloudResource {
    private String userId;
    private String siteId;
    private String blogId;

    /**
     * Creates a Blog object.
     *
     * @param userId ID of the user this blog belongs to.
     * @param siteId ID of the site this blog belongs to.
     * @param blogId ID of an existing blog.
     * @param initialize Whether or not to retrieve this blog's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the Blog's properties if initialize
     *          is false.
     */
    public Blog(
        String userId,
        String siteId,
        String blogId,
        boolean initialize,
        JsonObject existing
    ) throws CloudException {
        super("user/"+userId+"/site/"+siteId+"/blog/"+blogId, initialize, existing);
        this.userId = userId;
        this.siteId = siteId;
        this.blogId = blogId;
    }

    /**
     * Creates a Blog object.
     *
     * @param userId ID of the user this blog belongs to.
     * @param siteId ID of the site this blog belongs to.
     * @param blogId ID of an existing blog.
     * @param initialize Whether or not to retrieve this blog's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public Blog(
        String userId,
        String siteId,
        String blogId,
        boolean initialize
    ) throws CloudException {
        this(userId, siteId, blogId, initialize, null);
    }

    /**
     * Creates a Blog object.
     *
     * @param userId ID of the user this blog belongs to.
     * @param siteId ID of the site this blog belongs to.
     * @param blogId ID of an existing blog.
     */
    public Blog(String userId, String siteId, String blogId) throws CloudException {
        this(userId, siteId, blogId, true, null);
    }

    /**
     * Returns a CloudList of BlogPosts on this Blog.
     */
    public CloudList<BlogPost> listBlogPosts() throws CloudException {
        CloudClient client = CloudClient.getClient();
        CloudResponse res = client.get(this.url + "/post");
        return new CloudList<BlogPost>(res,
            new String[]{this.userId, this.siteId, this.blogId}, BlogPost::arrayFromJson);
    }

    /**
    * Creates a new BlogPost on the blog with the specified post body and other
    * properties specified in data.
    *
    * @param postBody The text body of the new post.
    * @param data Optional properties of the post.
    */
    public BlogPost createBlogPost(String postBody, HashMap<String, Object> data)
            throws CloudException {
        CloudClient client = CloudClient.getClient();

        data.put("post_body", postBody);

        CloudResponse res = client.post(this.url+"/post", data);
        JsonObject post = res.body.getAsJsonObject();

        return new BlogPost(this.userId, this.siteId, this.blogId, post.get("post_id").getAsString(), false, post);
    }

    /**
    * Creates a new BlogPost on the blog with the specified post body.
    *
    * @param postBody The text body of the new post.
    */
    public BlogPost createBlogPost(String postBody) throws CloudException {
        return createBlogPost(postBody, new HashMap<String, Object>());
    }

    /**
    * Get the post with the specified ID. The post must belong to this blog.
    *
    * @param postId The ID of the post to retrieve.
    */
    public BlogPost getBlogPost(String postId) throws CloudException {
        return new BlogPost(this.userId, this.siteId, this.blogId, postId);
    }

    /**
     * Converts a JSON response into an array of
     * Blog objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the Blogs
     *              (user ID and site ID).
     * @param json JSON representation of a list of blogs.
     */
    public static ArrayList<Blog> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<Blog> blogList = new ArrayList<Blog>();
        JsonArray blogs = json.getAsJsonArray();

        for (JsonElement blog : blogs) {
            String id = blog.getAsJsonObject().get("blog_id").getAsString();
            blogList.add(new Blog(ids[0], ids[1], id, false, blog.getAsJsonObject()));
        }

        return blogList;
    }
}