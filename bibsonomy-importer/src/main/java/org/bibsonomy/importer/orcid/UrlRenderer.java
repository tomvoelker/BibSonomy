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

import java.util.List;

import org.bibsonomy.util.UrlBuilder;

public class UrlRenderer {

    public static String BASE_URL = "https://pub.orcid.org/v3.0/";
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
