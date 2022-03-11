package org.bibsonomy.importer.orcid;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OrcidUtils {

    public static Set<String> getListOfWorkIds(JSONObject worksObj) {
        Set<String> workIds = new HashSet<>();

        JSONArray worksArray = (JSONArray) worksObj.get("group");
        worksArray.forEach(item -> {
            JSONObject obj = (JSONObject) item;
            JSONArray workSummary = (JSONArray) obj.get("work-summary");
            JSONObject workSummaryObj = (JSONObject) workSummary.get(0);
            workIds.add(workSummaryObj.get("put-code").toString());
        });

        return workIds;
    }

}
