/**
 * BibSonomy-Importer - Various importers for bookmarks and publications.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.importer.orcid;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.util.WebUtils;

/**
 * This class is a RESTClient for ORCID
 * and used to send API Request to ORCID and receive responses as JSON.
 *
 * @author kchoong
 */
public class OrcidRestClient {

    public static String CONTENT_HEADER = "application/orcid+json";

    private final OrcidUrlGenerator orcidUrlGenerator;

    public OrcidRestClient() {
        this.orcidUrlGenerator = new OrcidUrlGenerator();
    }

    public String getWorks(String orcidId) {
        String url = this.orcidUrlGenerator.getWorksUrl(orcidId);
        return this.execute(url);
    }

    public String getWorkDetails(String orcidId, String workId) {
        String url = this.orcidUrlGenerator.getWorkDetailsUrl(orcidId, workId);
        return this.execute(url);
    }

    public String getWorkDetailsBulk(String orcidId, List<String> workIds) {
        String url = this.orcidUrlGenerator.getWorkDetailsBulkUrl(orcidId, workIds);
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

