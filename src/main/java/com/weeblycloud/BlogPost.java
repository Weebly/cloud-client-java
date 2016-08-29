package com.weeblycloud;

import com.weeblycloud.utils.*;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

/**
* Represents a blog post.
*/
public class BlogPost extends CloudResource implements Deletable, Mutable {
    private String userId;
    private String siteId;
    private String blogId;
    private String blogPostId;

    /**
     * Creates a BlogPost object.
     *
     * @param userId ID of the user who owns the blog this post belongs to.
     * @param siteId ID of the site this post belongs to.
     * @param blogId ID of the blog this post belongs to.
     * @param blogPostId ID of an existing post.
     * @param initialize Whether or not to retrieve this post's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     * @param existing JsonObject to use as the BlogPost's properties if initialize
     *          is false.
     */
    public BlogPost(
        String userId,
        String siteId,
        String blogId,
        String blogPostId,
        boolean initialize,
        JsonObject existing
    ) throws CloudException {
        super(
            "user/"+userId+"/site/"+siteId+"/blog/"+blogId+"/post/"+blogPostId,
            initialize,
            existing
        );
        this.userId = userId;
        this.siteId = siteId;
        this.blogId = blogId;
        this.blogPostId = blogPostId;
    }

    /**
     * Creates a BlogPost object.
     *
     * @param userId ID of the user who owns the blog this post belongs to.
     * @param siteId ID of the site this post belongs to.
     * @param blogId ID of the blog this post belongs to.
     * @param blogPostId ID of an existing post.
     * @param initialize Whether or not to retrieve this post's properties
     *          from the server upon instantiation. The properties can later
     *          be retrieved by calling get().
     */
    public BlogPost(
        String userId,
        String siteId,
        String blogId,
        String blogPostId,
        boolean initialize
    ) throws CloudException {
        this(userId, siteId, blogId, blogPostId, initialize, null);
    }

    /**
     * Creates a BlogPost object.
     *
     * @param userId ID of the user who owns the blog this post belongs to.
     * @param siteId ID of the site this post belongs to.
     * @param blogId ID of the blog this post belongs to.
     * @param blogPostId ID of an existing post.
     */
    public BlogPost(
        String userId,
        String siteId,
        String blogId,
        String blogPostId
    ) throws CloudException {
        this(userId, siteId, blogId, blogPostId, true, null);
    }

    /**
     * Converts a JSON response into an array of
     * BlogPost objects. Because the formatting of responses
     * and the IDS needed for instantiation are
     * inconsistent across endpoints, this is handled
     * on a class-by-class basis.
     *
     * @param ids The IDs necessary to construct the BlogPosts
     *              (user ID, site ID, and blog ID).
     * @param json JSON of a list of blog posts.
     */
    public static ArrayList<BlogPost> arrayFromJson(String[] ids, JsonElement json)
            throws CloudException {
        ArrayList<BlogPost> blogPostList = new ArrayList<BlogPost>();
        JsonArray blogPosts = json.getAsJsonArray();

        for (JsonElement blogPost : blogPosts) {
            String id = blogPost.getAsJsonObject().get("post_id").getAsString();
            blogPostList.add(
                new BlogPost(ids[0], ids[1], ids[2], id, false, blogPost.getAsJsonObject())
            );
        }

        return blogPostList;
    }
}
