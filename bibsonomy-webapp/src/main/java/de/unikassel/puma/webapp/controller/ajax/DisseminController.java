package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DisseminController extends AjaxController {

    public static class DisseminConfig {
        public static final String API_URL = "https://dissem.in/api/";
        public static final String QUERY_URL = API_URL + "query/";
        public static final String ERROR = "error";
        public static final String PAPER = "paper";
        public static final String POLICY = "policy";
        public static final String POLICY_PREPRINT = "preprint";
        public static final String POLICY_POSTPRINT = "postprint";
        public static final String POLICY_PUBLISHED = "published";
        public static final String POLICY_CAN = "can";
        public static final String POLICY_CANNOT = "cannot";
        public static final String POLICY_RESTRICTED = "restricted";
        public static final String POLICY_UNKNOWN = "unknown";
        public static final String RECORDS = "records";
        public static final String STATUS = "classification";
        public static final String STATUS_OA = "oa";
        public static final String STATUS_OK = "ok";
        public static final String STATUS_COULDBE = "couldbe";
        public static final String STATUS_UNK = "unk";
        public static final String STATUS_CLOSED = "closed";
    }

    protected static final Log LOG = LogFactory.getLog(DisseminController.class);

    public Map<String, String> getPolicyForPost(final Post<? extends BibTex> post) {
        // Initiate post policy as all unknown
        Map<String, String> policy = new HashMap<String, String>() {{
            put(DisseminConfig.STATUS, DisseminConfig.STATUS_UNK);
            put(DisseminConfig.POLICY_PREPRINT, DisseminConfig.POLICY_UNKNOWN);
            put(DisseminConfig.POLICY_POSTPRINT, DisseminConfig.POLICY_UNKNOWN);
            put(DisseminConfig.POLICY_PUBLISHED, DisseminConfig.POLICY_UNKNOWN);
        }};

        // Get policy, if post is present
        if (present(post)) {
            String doi = post.getResource().getMiscField("doi");
            if (present(doi)) {
                this.handleByDoi(doi, policy);
            } else {
                this.handleByMetadata(post, policy);
            }
        }

        return policy;
    }

    private void processResponse(final String response, final Map<String, String> policy) {
        if (present(response)) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject resultJson =  (JSONObject) parser.parse(response);
                if (present(resultJson) && !resultJson.containsKey(DisseminConfig.ERROR)) {
                    JSONObject paperJson = (JSONObject) resultJson.getOrDefault(DisseminConfig.PAPER, null);
                    if (present(paperJson)) {
                        policy.put(DisseminConfig.STATUS, ((String) paperJson.getOrDefault(DisseminConfig.STATUS, DisseminConfig.STATUS_UNK)).toLowerCase());

                        JSONArray recordsJson = (JSONArray) paperJson.getOrDefault(DisseminConfig.RECORDS, new JSONArray());
                        for (Object o : recordsJson) {
                            if (o instanceof JSONObject) {
                                JSONObject recordJson = (JSONObject) o;
                                if (recordJson.containsKey(DisseminConfig.POLICY)) {
                                    JSONObject policyJson = (JSONObject) recordJson.get(DisseminConfig.POLICY);
                                    policy.put(DisseminConfig.POLICY_PREPRINT, (String) policyJson.getOrDefault(DisseminConfig.POLICY_PREPRINT, DisseminConfig.POLICY_UNKNOWN));
                                    policy.put(DisseminConfig.POLICY_POSTPRINT, (String) policyJson.getOrDefault(DisseminConfig.POLICY_POSTPRINT, DisseminConfig.POLICY_UNKNOWN));
                                    policy.put(DisseminConfig.POLICY_PUBLISHED, (String) policyJson.getOrDefault(DisseminConfig.POLICY_PUBLISHED, DisseminConfig.POLICY_UNKNOWN));
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                LOG.error("Unable to parse the response");
                throw new RuntimeException(e);
            }
        }
    }

    private void handleByDoi(final String doi, final Map<String, String> policy) {
        try {
            final HttpGet httpGet = new HttpGet(DisseminConfig.API_URL + doi);
            httpGet.setHeader("Accept", "application/json");

            final String response = WebUtils.getContentAsString(WebUtils.getHttpClient(), httpGet);
            this.processResponse(response, policy);
        } catch (HttpException | IOException e) {
            LOG.error("Unable to retrieve the document with the DOI: " + doi);
        }
    }

    private void handleByMetadata(final Post<? extends BibTex> post, final Map<String, String> policy) {
        try {
            String postDocument = this.convertPost(post);
            final HttpPost httpPost = new HttpPost(DisseminConfig.QUERY_URL);
            final StringEntity entity = new StringEntity(postDocument, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            final String response = WebUtils.getContentAsString(WebUtils.getHttpClient(), httpPost);
            this.processResponse(response, policy);
        } catch (HttpException | IOException e) {
            LOG.error("Unable to retrieve the document with the metadata of this publication: " + post);
        }
    }

    private String convertPost(final Post<? extends BibTex> post) {
        JSONObject postJson = new JSONObject();
        postJson.put("title", post.getResource().getTitle());
        postJson.put("date", post.getResource().getYear());

        JSONArray authors = new JSONArray();
        for (PersonName author : post.getResource().getAuthor()) {
            JSONObject authorObj = new JSONObject();
            authorObj.put("first", author.getFirstName());
            authorObj.put("last", author.getLastName());
            authors.add(authorObj);
        }
        postJson.put("authors", authors);

        return postJson.toJSONString();
    }
}
