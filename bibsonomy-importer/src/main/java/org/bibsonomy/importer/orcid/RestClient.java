package org.bibsonomy.importer.orcid;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.util.WebUtils;

public class RestClient {

    public static String CONTENT_HEADER = "application/orcid+json";

    private final UrlRenderer urlRenderer;

    public RestClient() {
        this.urlRenderer = new UrlRenderer();
    }

    public String getWorks(String orcidId) {
        String url = this.urlRenderer.getWorksUrl(orcidId);
        return this.execute(url);
    }

    public String getWorkDetails(String orcidId, String workId) {
        String url = this.urlRenderer.getWorkDetailsUrl(orcidId, workId);
        return this.execute(url);
    }

    private String execute(String url) {
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", CONTENT_HEADER);

        String response = "";
        try {
            response = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);
        } catch (HttpException | IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}

