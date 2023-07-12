package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DisseminController extends AjaxController {

    public class DisseminConfig {
        public static final String API_URL = "https://dissem.in/api/query/";
        public static final String POLICY = "policy";
        public static final String POLICY_PREPRINT = "preprint";
        public static final String POLICY_POSTPRINT = "postprint";
        public static final String POLICY_PUBLISHED = "published";
        public static final String POLICY_CAN = "can";
        public static final String POLICY_CANNOT = "cannot";
        public static final String POLICY_RESTRICTED = "restricted";
        public static final String POLICY_UNKNOWN = "unknown";
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
            String postDocument = this.convertPost(post);
            JSONObject resultJson = this.postRequest(postDocument);
            if (present(resultJson)) {
            }
        }

        return policy;
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

    private JSONObject postRequest(String postDoc) {
        try {
            final HttpPost httpPost = new HttpPost(DisseminConfig.API_URL);

            final StringEntity entity = new StringEntity(postDoc);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            final HttpResponse response;
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                LOG.info("Check dissemin status for publication:" + postDoc);
                response = client.execute(httpPost);
            }

            if (present(response)) {
                final int statusCode = response.getStatusLine().getStatusCode();
                final String stringResponse = EntityUtils.toString(response.getEntity());
                JSONParser parser = new JSONParser();
                JSONObject resultObj = (JSONObject) parser.parse(stringResponse);
                return resultObj;
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return new JSONObject();
    }
}
