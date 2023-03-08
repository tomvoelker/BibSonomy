package de.unikassel.puma.openaccess.dissemin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DisseminController {

    protected static final Log LOG = LogFactory.getLog(DisseminController.class);

    public static final String API_URL = "https://dissem.in/api/query/";

    public String getPolicyForPost(final Post<? extends BibTex> post) {
        String postDocument = this.convertPost(post);
        JSONObject result = this.postRequest(postDocument);
        if (present(result)) {

        }

        return "";
    }

    private String convertPost(final Post<? extends BibTex> post) {
        JSONObject document = new JSONObject();
        document.put("title", post.getResource().getTitle());
        document.put("date", post.getResource().getYear());

        JSONArray authors = new JSONArray();
        for (PersonName author : post.getResource().getAuthor()) {
            JSONObject authorObj = new JSONObject();
            authorObj.put("first", author.getFirstName());
            authorObj.put("last", author.getLastName());
            authors.add(authorObj);
        }
        document.put("authors", authors);

        return document.toJSONString();
    }

    private JSONObject postRequest(String postDoc) {
        try {
            final HttpPost httpPost = new HttpPost(API_URL);

            final StringEntity entity = new StringEntity(postDoc);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            final HttpResponse response;
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                response = client.execute(httpPost);
            }

            if (present(response)) {
                final int statusCode = response.getStatusLine().getStatusCode();
                final String stringResponse = EntityUtils.toString(response.getEntity());
                JSONParser parser = new JSONParser();
                JSONObject resultObj = (JSONObject) parser.parse(stringResponse);
                return resultObj;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
