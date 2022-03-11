package org.bibsonomy.importer.orcid;

import java.util.List;

import org.bibsonomy.util.UrlBuilder;

public class UrlRenderer {

    public static String BASE_URL = "https://pub.orcid.org/v2.1/";
    public static String WORK_PARAM = "work";
    public static String WORKS_PARAM = "works";

    public String getWorksUrl(String orcidId) {
        UrlBuilder urlBuilder = new UrlBuilder(BASE_URL);
        urlBuilder.addPathElement(orcidId);
        urlBuilder.addPathElement(WORKS_PARAM);

        return urlBuilder.toString();
    }

    public String getWorkDetailsUrl(String orcidId, String workId) {
        UrlBuilder urlBuilder = new UrlBuilder(BASE_URL);
        urlBuilder.addPathElement(orcidId);
        urlBuilder.addPathElement(WORK_PARAM);
        urlBuilder.addPathElement(workId);

        return urlBuilder.toString();
    }

    public String getWorkDetailsBulkUrl(String orcidId, List<String> workIds) {
        UrlBuilder urlBuilder = new UrlBuilder(BASE_URL);
        urlBuilder.addPathElement(orcidId);
        urlBuilder.addPathElement(WORKS_PARAM);
        urlBuilder.addPathElement(String.join(",", workIds));

        return urlBuilder.toString();
    }
}
