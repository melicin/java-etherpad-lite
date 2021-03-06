/**
 * Copyright 2011 Jordan Hollinger
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.etherpad_lite_client;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.json.*;

/**
 * A client for talking to Etherpad Lite's HTTP JSON API.<br />
 * <br />
 * Example:<br />
 * <br />
 * <code>
 * EPLiteClient api = new EPLiteClient("http://etherpad.mysite.com", "FJ7jksalksdfj83jsdflkj");<br />
 * HashMap pad = api.getText("my_pad");<br />
 * String pad = pad.get("text").toString();
 * </code>
 */
public class EPLiteClient {
    /**
     * The Etherpad Lite API version this client targets
     */
    public static final int API_VERSION = 1;

    public static final int CODE_OK = 0;
    public static final int CODE_INVALID_PARAMETERS = 1;
    public static final int CODE_INTERNAL_ERROR = 2;
    public static final int CODE_INVALID_METHOD = 3;
    public static final int CODE_INVALID_API_KEY = 4;

    /**
     * The url of the API
     */
    public URI uri;

    /**
     * The API key
     */
    public String apiKey;

    /**
     * Initializes a new org.etherpad_lite_client.EPLiteClient object.
     *
     * @param url an absolute url, including protocol, to the EPL api
     * @param apiKey the API Key
     */
    public EPLiteClient(String url, String apiKey) {
        this.uri = URI.create(url);
        this.apiKey = apiKey;
    }

    // Groups
    // Pads may belong to a group. These pads are not considered "public", and won't be available through the Web UI without a session.

    /**
     * Creates a new Group. The group id is returned in "groupID" in the HashMap.
     * 
     * @return HashMap
     */
    public HashMap createGroup() {
        return this.post("createGroup");
    }

    /**
     * Creates a new Group for groupMapper if one doesn't already exist. Helps you map your application's groups to Etherpad Lite's groups.
     * The group id is returned in "groupID" in the HashMap.
     * 
     * @param groupMapper your group mapper string
     * @return HashMap
     */
    public HashMap createGroupIfNotExistsFor(String groupMapper) {
        HashMap args = new HashMap();
        args.put("groupMapper", groupMapper);
        return this.post("createGroupIfNotExistsFor", args);
    }

    /**
     * Delete group.
     *
     * @param groupID string
     */
    public void deleteGroup(String groupID) {
        HashMap args = new HashMap();
        args.put("groupID", groupID);
        this.post("deleteGroup", args);
    }

    /**
     * List all the padIDs in a group. They will be in an array inside "padIDs".
     * 
     * @param groupID string
     * @return HashMap
     */
    public HashMap listPads(String groupID) {
        HashMap args = new HashMap();
        args.put("groupID", groupID);
        return this.get("listPads", args);
    }

    /**
     * Create a pad in this group.
     * 
     * @param groupID string
     * @param padName string
     */
    public void createGroupPad(String groupID, String padName) {
        HashMap args = new HashMap();
        args.put("groupID", groupID);
        args.put("padName", padName);
        this.post("createGroupPad", args);
    }

    /**
     * Create a pad in this group.
     * 
     * @param groupID string
     * @param padName string
     * @param text string
     */
    public void createGroupPad(String groupID, String padName, String text) {
        HashMap args = new HashMap();
        args.put("groupID", groupID);
        args.put("padName", padName);
        args.put("text", text);
        this.post("createGroupPad", args);
    }

    // Authors
    // These authors are bound to the attributes the users choose (color and name). The author id is returned in "authorID".

    /**
     * Create a new author.
     * 
     * @return HashMap
     */
    public HashMap createAuthor() {
        return this.post("createAuthor");
    }

    /**
     * Create a new author with the given name. The author id is returned in "authorID".
     * 
     * @param name string
     * @return HashMap
     */
    public HashMap createAuthor(String name) {
        HashMap args = new HashMap();
        args.put("name", name);
        return this.post("createAuthor", args);
    }

    /**
     * Creates a new Author for authorMapper if one doesn't already exist. Helps you map your application's authors to Etherpad Lite's authors.
     * The author id is returned in "authorID".
     * 
     * @param authorMapper string
     * @return HashMap
     */
    public HashMap createAuthorIfNotExistsFor(String authorMapper) {
        HashMap args = new HashMap();
        args.put("authorMapper", authorMapper);
        return this.post("createAuthorIfNotExistsFor", args);
    }

    /**
     * Creates a new Author for authorMapper if one doesn't already exist. Helps you map your application's authors to Etherpad Lite's authors.
     * The author id is returned in "authorID".
     * 
     * @param authorMapper string
     * @param name string
     * @return HashMap
     */
    public HashMap createAuthorIfNotExistsFor(String authorMapper, String name) {
        HashMap args = new HashMap();
        args.put("authorMapper", authorMapper);
        args.put("name", name);
        return this.post("createAuthorIfNotExistsFor", args);
    }

    /**
     * Returns an array of all pads this author contributed to.
     * 
     * @param authorID string
     * @return HashMap
     */
    public HashMap listPadsOfAuthor(String authorID) {
        HashMap args = new HashMap();
        args.put("authorID", authorID);
        return this.get("listPadsOfAuthor", args);
    }

    /**
     * Returns an array of authors who contributed to this pad.
     * 
     * @param authorID string
     * @return HashMap
     */
    public HashMap listAuthorsOfPad(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        return this.get("listAuthorsOfPad", args);
    }

    // Sessions
    // Sessions can be created between a group and an author. This allows an author to access more than one group. The sessionID will be set as a
    // cookie to the client and is valid until a certain date. Only users with a valid session for this group, can access group pads. You can create a
    // session after you authenticated the user at your web application, to give them access to the pads. You should save the sessionID of this session
    // and delete it after the user logged out.

    /**
     * Create a new session for the given author in the given group, valid until the given UNIX time.
     * The session id will be returned in "sessionID".<br />
     * <br />
     * Example:<br />
     * <br />
     * <code>
     * import java.util.Date;<br />
     * ...<br />
     * Date now = new Date();<br />
     * long in1Hour = (now.getTime() + (60L * 60L * 1000L) / 1000L);<br />
     * String sessID1 = api.createSession(groupID, authorID, in1Hour).get("sessionID").toString();
     * </code>
     * 
     * @param groupID string
     * @param authorID string
     * @param validUntil long UNIX timestamp <strong>in seconds</strong>
     * @return HashMap
     */
    public HashMap createSession(String groupID, String authorID, long validUntil) {
        HashMap args = new HashMap();
        args.put("groupID", groupID);
        args.put("authorID", authorID);
        args.put("validUntil", validUntil);
        return this.post("createSession", args);
    }

    /**
     * Create a new session for the given author in the given group valid for the given number of hours.
     * The session id will be returned in "sessionID".<br />
     * <br />
     * Example:<br />
     * <br />
     * <code>
     * // in 2 hours<br />
     * String sessID1 = api.createSession(groupID, authorID, 2).get("sessionID").toString();
     * </code>
     * 
     * @param groupID string
     * @param authorID string
     * @param validUntil int length of session in hours
     * @return HashMap
     */
    public HashMap createSession(String groupID, String authorID, int length) {
        long inNHours = ((new Date()).getTime() + ((long)length * 60L * 60L * 1000L)) / 1000L;
        return this.createSession(groupID, authorID, inNHours);
    }

    /**
     * Create a new session for the given author in the given group, valid until the given datetime.
     * The session id will be returned in "sessionID".<br />
     * <br />
     * Example:<br />
     * <br />
     * <code>
     * import java.util.Date;<br />
     * import java.text.DateFormat;<br />
     * import java.text.SimpleDateFormat;<br />
     * import java.util.TimeZone;<br />
     * ...<br />
     * DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");<br />
     * dfm.setTimeZone(TimeZone.getTimeZone("GMT-5"));<br />
     * Date longTime = dfm.parse("2056-01-15 20:15:00");<br />
     * String sessID = api.createSession(groupID, authorID, longTime).get("sessionID").toString();
     * </code>
     * 
     * @param groupID string
     * @param authorID string
     * @param validUntil Date
     * @return HashMap
     */
    public HashMap createSession(String groupID, String authorID, Date validUntil) {
        long seconds = validUntil.getTime() / 1000L;
        return this.createSession(groupID, authorID, seconds);
    }

    /**
     * Delete a session.
     * 
     * @param sessionID string
     */
    public void deleteSession(String sessionID) {
        HashMap args = new HashMap();
        args.put("sessionID", sessionID);
        this.post("deleteSession", args);
    }

    /**
     * Returns information about a session: authorID, groupID and validUntil.
     * 
     * @param sessionID string
     * @return HashMap
     */
    public HashMap getSessionInfo(String sessionID) {
        HashMap args = new HashMap();
        args.put("sessionID", sessionID);
        return this.get("getSessionInfo", args);
    }

    /**
     * List all the sessions IDs in a group. Returned as a HashMap of sessionIDs keys, with values of HashMaps containing
     * groupID, authorID, and validUntil.
     * 
     * @param groupID string
     * @return HashMap
     */
    public HashMap listSessionsOfGroup(String groupID) {
        HashMap args = new HashMap();
        args.put("groupID", groupID);
        return this.get("listSessionsOfGroup", args);
    }

    /**
     * List all the sessions IDs belonging to an author. Returned as a HashMap of sessionIDs keys, with values of HashMaps containing
     * groupID, authorID, and validUntil.
     * 
     * @param authorID string
     * @return HashMap
     */
    public HashMap listSessionsOfAuthor(String authorID) {
        HashMap args = new HashMap();
        args.put("authorID", authorID);
        return this.get("listSessionsOfAuthor", args);
    }

    // Pad content

    /**
     * Returns a HashMap containing the latest revision of the pad's text.
     * The text is stored under "text".
     * 
     * @param padId the pad's id string
     * @return HashMap
     */
    public HashMap getText(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        return this.get("getText", args);
    }

    /**
     * Returns a HashMap containing the a specific revision of the pad's text.
     * The text is stored under "text".
     * 
     * @param padId the pad's id string
     * @param rev the revision number
     * @return HashMap
     */
    public HashMap getText(String padId, int rev) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        args.put("rev", new Integer(rev));
        return this.get("getText", args);
    }

    /**
     * Creates a new revision with the given text (or creates a new pad).
     * 
     * @param padId the pad's id string
     * @param text the pad's new text
     */
    public void setText(String padId, String text) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        args.put("text", text);
        this.post("setText", args);
    }

    /**
     * Returns a HashMap containing the current revision of the pad's text as HTML.
     * The html is stored under "html".
     * 
     * @param padId the pad's id string
     * @return HashMap
     */
    public HashMap getHTML(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        return this.get("getHTML", args);
    }

    /**
     * Returns a HashMap containing the a specific revision of the pad's text as HTML.
     * The html is stored under "html".
     * 
     * @param padId the pad's id string
     * @param rev the revision number
     * @return HashMap
     */
    public HashMap getHTML(String padId, int rev) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        args.put("rev", new Integer(rev));
        return this.get("getHTML", args);
    }

    /**
     * Creates a new revision with the given html (or creates a new pad).
     * 
     * @param padId the pad's id string
     * @param html the pad's new html text
     */
    public void setHTML(String padId, String html) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        args.put("html", html);
        this.post("setHTML", args);
    }

    // Pads
    // Group pads are normal pads, but with the name schema GROUPID$PADNAME. A security manager controls access of them and its 
    // forbidden for normal pads to include a $ in the name. 

    /**
     * Create a new pad.
     * 
     * @param padId the pad's id string
     */
    public void createPad(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        this.post("createPad", args);
    }

    /**
     * Create a new pad with the given initial text.
     * 
     * @param padId the pad's id string
     * @param text the initial text string
     */
    public void createPad(String padId, String text) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        args.put("text", text);
        this.post("createPad", args);
    }

    /**
     * Returns the number of revisions of this pad. The number is in "revisions".
     * 
     * @param padId the pad's id string
     * @return HashMap
     */
    public HashMap getRevisionsCount(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        return this.get("getRevisionsCount", args);
    }

    /**
     * Deletes a pad.
     * 
     * @param padId the pad's id string
     */
    public void deletePad(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        this.post("deletePad", args);
    }

    /**
     * Get the pad's read-only id. The id will be in "readOnlyID".
     * 
     * @param padId the pad's id string
     * @return HashMap
     */
    public HashMap getReadOnlyID(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        return this.get("getReadOnlyID", args);
    }

    /**
     * Sets the pad's public status.
     * This is only applicable to group pads.
     * 
     * @param padId the pad's id string
     * @param publicStatus boolean
     */
    public void setPublicStatus(String padId, Boolean publicStatus) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        args.put("publicStatus", publicStatus);
        this.post("setPublicStatus", args);
    }

    /**
     * Gets the pad's public status. The boolean is in "publicStatus".
     * This is only applicable to group pads.<br />
     * <br />
     * Example:<br />
     * <br />
     * <code>
     * Boolean is_public = (Boolean)api.getPublicStatus("g.kjsdfj7ask$foo").get("publicStatus");
     * </code>
     * 
     * @param padId the pad's id string
     * @return HashMap
     */
    public HashMap getPublicStatus(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        return this.get("getPublicStatus", args);
    }

    /**
     * Sets the pad's password. This is only applicable to group pads.
     * 
     * @param padId the pad's id string
     * @param password string
     */
    public void setPassword(String padId, String password) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        args.put("password", password);
        this.post("setPassword", args);
    }

    /**
     * Checks whether the pad is password-protected or not. The boolean is in "isPasswordProtected".
     * This is only applicable to group pads.<br />
     * <br />
     * Example:<br />
     * <br />
     * <code>
     * Boolean pass = (Boolean)api.isPasswordProtected("g.kjsdfj7ask$foo").get("isPasswordProtected");
     * </code>
     * 
     * @param padId the pad's id string
     * @return HashMap
     */
    public HashMap isPasswordProtected(String padId) {
        HashMap args = new HashMap();
        args.put("padID", padId);
        return this.get("isPasswordProtected", args);
    }

    /**
     * Returns true if the connection is using SSL/TLS, false if not.
     * 
     * @return Boolean
     */
    public Boolean isSecure() {
        if (this.uri.getPort() == 443) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * POSTs to the HTTP JSON API.
     * 
     * @param apiMethod the name of the API method to call
     * @return HashMap
     */
    private HashMap post(String apiMethod) {
      return this.call(apiMethod, new HashMap(), "POST");
    }

    /**
     * POSTs to the HTTP JSON API.
     * 
     * @param apiMethod the name of the API method to call
     * @param apiArgs a HashMap of url/form parameters. apikey will be set automatically
     * @return HashMap
     */
    private HashMap post(String apiMethod, HashMap apiArgs) {
      return this.call(apiMethod, apiArgs, "POST");
    }

    /**
     * GETs from the HTTP JSON API.
     * 
     * @param apiMethod the name of the API method to call
     * @return HashMap
     */
    private HashMap get(String apiMethod) {
      return this.call(apiMethod, new HashMap(), "GET");
    }

    /**
     * GETs from the HTTP JSON API.
     * 
     * @param apiMethod the name of the API method to call
     * @param apiArgs a HashMap of url/form parameters. apikey will be set automatically
     * @return HashMap
     */
    private HashMap get(String apiMethod, HashMap apiArgs) {
      return this.call(apiMethod, apiArgs, "GET");
    }

    /**
     * Calls the HTTP JSON API.
     * 
     * @param apiMethod the name of the API method to call
     * @param apiArgs a HashMap of url/form parameters. apikey will be set automatically
     * @param httpMethod the HTTP method to use ("GET" or "POST")
     * @return HashMap
     */
    private HashMap call(String apiMethod, HashMap apiArgs, String httpMethod) {
        // Build the url params
        String strArgs = "";
        apiArgs.put("apikey", this.apiKey);
        Iterator i = apiArgs.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            Object value = e.getValue();
            if (value != null) {
                strArgs += e.getKey() + "=" + value;
                if (i.hasNext()) {
                  strArgs += "&";
                }
            }
        }

        // Execute the API call
        String path = this.uri.getPath() + "/" + API_VERSION + "/" + apiMethod;
        URL url;
        Request request;
        try {
            // A read (get) request
            if (httpMethod == "GET") {
                url = new URL(new URI(this.uri.getScheme(), null, this.uri.getHost(), this.uri.getPort(), path, strArgs, null).toString());
                request = new GETRequest(url);
            }
            // A write (post) request
            else if (httpMethod == "POST") {
                url = new URL(new URI(this.uri.getScheme(), null, this.uri.getHost(), this.uri.getPort(), path, null, null).toString());
                request = new POSTRequest(url, strArgs);
            }
            else {
                throw new EPLiteException(httpMethod + " is not a valid HTTP method");
            }
            return this.handleResponse(request.send());
        }
        catch (EPLiteException e) {
            throw new EPLiteException(e.getMessage());
        }
        catch (Exception e) {
            throw new EPLiteException("Unable to connect to Etherpad Lite instance (" + e.getClass() + "): " + e.getMessage());
        }
    }

    /**
     * Converts the API resonse's JSON string into a HashMap.
     * 
     * @param jsonString a valid JSON string
     * @return HashMap
     */
    public HashMap handleResponse(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            // Act on the response code
            if (!json.get("code").equals(null)) {
                switch ((Integer)json.get("code")) {
                    // Valid code, parse the response
                    case CODE_OK:
                        if (!json.get("data").equals(null)) {
                            return new JSONResponse(json.get("data").toString()).toHashMap();
                        } else {
                            return new HashMap();
                        }
                    // Invalid code, throw an exception with the message
                    case CODE_INVALID_PARAMETERS:
                    case CODE_INVALID_API_KEY:
                    case CODE_INVALID_METHOD:
                        throw new EPLiteException((String)json.get("message"));
                    default:
                        throw new EPLiteException("An unknown error has occurred while handling the response: " + jsonString);
                }
            // No response code, something's really wrong
            } else {
                throw new EPLiteException("An unknown error has occurred while handling the response: " + jsonString);
            }
        } catch (JSONException e) {
            System.err.println("Unable to parse JSON response (" + jsonString + "): " + e.getMessage());
            return new HashMap();
        }
    }
}
