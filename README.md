# Weebly Cloud API Library: Java

## Installation

###Maven




### Others

Download and include the following JARs:

- [Apache Commons HttpComponents](https://hc.apache.org/downloads.cgi)
- [Google Gson](https://github.com/google/gson)
- The latest release of Weebly Cloud Library


## Setup and Authentication
API calls are authenticated through a public key and a hash of your request and secret key. You can create and manage your API keys in the settings section of the Weebly Cloud Admin.

	import com.weeblycloud.utils.CloudClient;
	CloudClient.setKeys(YOUR_API_KEY, YOUR_API_SECRET);

You must set your public and secret key **before** making any calls to the API.

## Examples

#### Typical use case: create a user and site, get a login link

```java
import com.weeblycloud.*;
import com.weeblycloud.utils.*;

CloudClient.setKeys(YOUR_API_KEY, YOUR_API_SECRET);

Account account = new Account();
User user = account.createUser("test@domain.com");
HashMap<String, Object> siteProperties = new HashMap<String, Object>();
siteProperties.put("site_title","My Website");
Site site = user.createSite("domain.com", siteProperties);
System.out.println(site.loginLink());
```

#### Printing the name of all pages in a site matching the query "help"

```java
Site site = new Site(userId, siteId);

HashMap<String, Object> params = new HashMap<String, Object>();
params.put("query", "help");
CloudList<Page> pages = site.listPages(params);
for (Page page : pages) {
	System.out.println(page.getProperty("title").getAsString());
}
```

## Errors
If a request fails, the CloudClient throws a CloudException. A list of error codes can be found in the [API documentation](https://cloud-developer.weebly.com/about-the-rest-apis.html). The exception can be caught as folllows:

```java
import com.weeblycloud.*;
import com.weeblycloud.utils.*;

CloudClient.setKeys(YOUR_API_KEY, YOUR_API_SECRET);

CloudClient client = CloudClient.getClient();

try {
	Account account = new Account();
} catch (CloudException e) {
	System.out.println(e);
}
```

## Resources
The library provides classes that represent API resources. These resources can be **mutable**, meaning their properties can be changed, and **deletable**. These classes are inside the com.weeblycloud package.

Common methods:

- The method **`resource.getProperty(property)`** will return a given property of the resource. If the property does not exist, it will return **null**.
- The method **`resource.setProperty(property, value)`** will set the value of a given property of the resource. Changes will not be saved in the database until **`resource.save()`** is called. If the resource is not mutable, calling this method will throw an exception. Not every property of a mutable resource can be changed; for more information, reference the [Cloud API Documentation](https://cloud-developer.weebly.com/about-the-rest-apis.html) for the resource in question's `PUT` method.
- The method **`resource.save()`** saves the properties changed by setProperty() to the database. If the resource is not mutable, calling this method will throw an exception.
- The method **`resource.delete()`** deletes the resource from the database. If the resource is not deletable, calling this method will throw an exception.

### Instantiating Resources
For example, to create an object representing a site with id `siteId` and owned by `userId`:

	Site site = new com.weeblycloud.Site(userId, siteId);

All resource constructors have two optional parameters, `initialize` (true by default) and `existing`. If `initialize` is set to false, the properties of the object are not retrieved from the database when the object is instantiated. Instead, `existing` is used to set the object's properties. This can be used to reduce unecessary API calls when chaining calls.

For example, to retrieve all the sites of a user without getting that user's information:

	CloudList<Site> sites = (new User(userId, false)).listSites();

### Iterable Results
Methods beginning with `list` return a `CloudList`. Use the `next` function to iterate through the list. For instance:

```java
CloudList<Site> sites = (new User(userId)).listSites();
for (Site site : sites) {
	System.out.println(site.getProperty("site_title"));
}
```
This would list the titles of all sites belonging to a given user.

##Resource Types

In addition to this readme, each resource class has javadoc documentation for public methods.

### Account
[API Documentation](https://cloud-developer.weebly.com/account.html)

A **mutable** representation of your Cloud Admin account, specified by your API keys. To construct:

	Account account = new com.weeblycloud.Account();

- **`createUser(String email)`** Creates a new user with the given email.
- **`createUser(String email, HashMap<String, Object> data)`**
Creates a new user with the given email and properties.
- **`getPlan(String planId)`** Gets a single Plan by ID.
- **`listPlans()`** Returns a list of available plans.

### Blog
[API Documentation](https://cloud-developer.weebly.com/blog.html)

A respresentation of a blog. To construct:

	Blog blog = new com.weeblycloud.Blog(userId, siteId, blogId);

- **`createBlogPost(String postBody)`** Creates a new BlogPost on the blog with the specified post body.
- **`createBlogPost(String postBody, HashMap<String,Object> data)`** Creates a new BlogPost on the blog with the specified post body and other properties specified in data.
- **`getBlogPost(String postId)`** Get the post with the specified ID.
- **`listBlogPosts()`** Returns a CloudList of BlogPosts on this Blog.

### BlogPost
[API Documentation](https://cloud-developer.weebly.com/blog-post.html)

A **mutable** and **deletable** respresentation of a blog post. To construct:

	BlogPost blogPost = new com.weeblycloud.BlogPost(userId, siteId, blogId, postId);

> There are no `BlogPost` specific methods.

### Form
[API Documentation](https://cloud-developer.weebly.com/form.html)

A respresentation of a form. To construct:

	Form form = new com.weeblycloud.Form(userId, siteId, formId);

- **`arrayFromJson(String[] ids, JsonElement json)`** Converts a JSON response into an array of Form objects.
- **`getFormEntry(String entryId)`** Returns the FormEntry with the given ID.
- **`listFormEntries()`** Returns a CloudList of FormEntries on this Form.
- **`listFormEntries(HashMap<String,Object> searchParams)`** Returns a CloudList of FormEntries on this Form.


### FormEntry
[API Documentation](https://cloud-developer.weebly.com/form-entry.html)

A respresentation of a form entry. To construct:

	Form formEntry = new com.weeblycloud.FormEntry(userId, siteId, formId, entryId);

> There are no `FormEntry` specific methods.

### Group
[API Documentation](https://cloud-developer.weebly.com/group.html)

A **mutable** and **deletable** respresentation of a group. To construct:

	Group group = new com.weeblycloud.Group(userId, siteId, groupId);

### Page
[API Documentation](https://cloud-developer.weebly.com/page.html)

A **mutable** respresentation of a page. To construct:

	Page page = new com.weeblycloud.Page(userId, siteId, pageId);

> There are no `Page` specific methods.

### Plan
[API Documentation](https://cloud-developer.weebly.com/plan.html)

A respresentation of a plan. To construct:

	Plan plan = new com.weeblycloud.Plan(planId);

> There are no `Plan` specific methods.

### Member
[API Documentation](https://cloud-developer.weebly.com/member.html)

A **mutable** and **deletable** respresentation of a member. To construct:

	Member member = new com.weeblycloud.Member(userId, siteId, memberId);

### Site
[API Documentation](https://cloud-developer.weebly.com/site.html)

A **mutable** and **deletable** respresentation of a site. To construct:

To construct:

	Site site = new com.weeblycloud.Site(userId, siteId);

- **`createGroup(String name)`**
Creates a new Group on the site with the specified name.
- **`createMember(HashMap<String,Object> data)`**
Creates a new Member on the site with the specified data.
- **`disable()`**
Disables a site, preventing the user from accessing it through the editor.
- **`enable()`**
Enables a site, allowing it to be edited.
- **`getBlog(String blogId)`**
Returns the Blog with the given ID.
- **`getForm(String formId)`**
Returns the Form with the given ID.
- **`getGroup(String groupId)`**
Returns the Group with the given ID.
- **`getMember(String memberId)`**
Returns the Member with the given ID.
- **`getPage(String pageId)`**
Returns the Page with the given ID.
- **`getPlan()`**
Gets the Plan assigned to the site.
- **`listBlogs()`**
Reurns a CloudList of Blogs belonging to this site.
- **`listForms()`**
Returns a CloudList of Forms on this Site.
- **`listForms(HashMap<String,Object> searchParams)`**
Returns a CloudList of Forms on this Site.
- **`listGroups()`**
Returns a CloudList of Groups on this Site.
- **`listGroups(HashMap<String,Object> searchParams)`**
Returns a CloudList of Groups on this Site.
- **`listMembers()`**
Returns a CloudList of Members on this Site.
- **`listMembers(HashMap<String,Object> searchParams)`**
Returns a CloudList of Members on this Site.
- **`listPages()`**
Returns a CloudList of Pages on this Site.
- **`listPages(HashMap<String,Object> searchParams)`**
Returns a CloudList of Pages on this Site.
- **`loginLink()`**
Generates a one-time login link for the user that automatically redirects them to the site editor for this site.
- **`publish()`**
Publishes a site.
- **`restore(String domain)`**
Restores a deleted site to the exact state it was in when deleted.
- **`setPlan(String planId)`**
Assigns a plan to a site with default term 1.
- **`setPlan(String planId, int term)`**
Assigns a plan to a site with the given term.
- **`setPublishCredentials(String host, String username, String password, String path)`**
 Sets publish credentials for a given site.
- **`setTheme(String themeId, boolean isCustom)`**
Assigns a theme to a site by ID.
- **`unpublish()`**
Unpublishes a site.

### User
[API Documentation](https://cloud-developer.weebly.com/user.html)

A **mutable** respresentation of a WeeblyCloud user. To construct:

	User user = new com.weeblycloud.User(userId);

- **`createCustomTheme(String name, String zipUrl)`**
Adds a custom theme to a user.
- **`createSite(String domain)`**
Creates a new Site belonging to this user in the database.
- **`createSite(String domain, HashMap<String,Object> data)`**
Creates a new Site belonging to this user in the database.
- **`disable()`**
Disables a user account, preventing them from logging in or editing their sites.
- **`enable()`**
Enables a user account after an account has been disabled.
- **`getAvailableThemes()`**
Returns an array of themes available to this user.
- **`getAvailableThemes(HashMap<String,Object> searchParams)`**
Returns an array of themes available to this user.
- **`getSite(String siteId)`**
Get the site with the specified ID.
- **`listSites()`**
Returns a CloudList of Sites belonging to this user.
- **`listSites(HashMap<String,Object> searchParams)`**
Returns a CloudList of Sites belonging to this user.
- **`loginLink()`**
Generates a one-time login link.

## Making Raw API Calls
Not every resource has a cooresponding resource class. It is possible to make a raw API call using a `CloudClient` object.

```java
CloudClient client = com.weeblycloud.utils.CloudClient.getClient();
```
Using that client, call `get`, `post`, `put`, `patch`, or `delete`. All client request methods take a url as their first parameter. `post`, `patch`, and `put` take an optional hash map of data that will be sent in the request body. `get` takes an optional hash map whose values will be used in the query string of the request.

The url **must not** include a leading slash.

#### Request examples

##### Get cloud admin account account
```java
// Get client
CloudClient client = CloudClient.getClient();

// Request the /account endpoint
CloudResponse res = client.get("account");
```

##### Update a page title
```java
// Get client
CloudClient client = CloudClient.getClient();

// Build endpoint with IDs
String endpoint = "user/"+userId+"/site/"+siteId+"/page/"+pageId;

// Prepare the data to be sent
HashMap<String, Object> data = new HashMap<String, Object>();
data.put("title", "Other New Title");

// Make the request
client.patch(endpoint, data);
```

##### Get all sites for a user (with search parameters)
```java
// Get client
CloudClient client = CloudClient.getClient();

// Build endpoint with IDs
String endpoint = "user/"+userId+"/site";

//Prepare the query string parameters
HashMap<String, Object> params = new HashMap<String, Object>();
params.put("role", "owner");

// Make the request
```

### Handling Responses
All requests return a `CloudResponse` object or throw an Exception (see error handling). The JSON returned by the request can be accessed through the response's `body` property.

```java
// Make a request
CloudResponse response = client.get("account");

// Print JSON body of response
print(response.body);
```


###Pagination example
If the endpoint supports pagination, the next and previous pages of results can be retrieved with the `getPreviousPage()` and `getNextPage()` methods. If there is no next or previous page, those methods return null.

```java
// Create client
CloudClient client = CloudClient.getClient();

//Prepare query string parameters
HashMap<String, Object> params = new HashMap<String, Object>();
params.put("limit", 10); //Limit the results to ten per page

CloudResponse response = client.get("user/"+userId+"/site",params);

while(response != null){
    System.out.println(response.body + "\n");
    response =  response.nextPage();
}
```
Get all of a user's sites, 10 sites per page.

##Questions?

If you have any questions or feature requests pertaining to this library, please open up a new issue. For general API questions, please contact us at dev-support@weebly.com, and we'll be happy to lend a hand!