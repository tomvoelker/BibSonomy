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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlRendererTest {

    private UrlRenderer urlRenderer;

    @Before
    public void setUp() throws Exception {
        this.urlRenderer = new UrlRenderer();
    }

    @Test
    public void testGetWorksUrl() {
        String orcidId = "0000-0002-0570-7908";
        String url = this.urlRenderer.getWorksUrl(orcidId);
        assertEquals(UrlRenderer.BASE_URL + orcidId + "/" + UrlRenderer.WORKS_PARAM , url);
    }

    @Test
    public void testGetWorkDetailsUrl() {
        String orcidId = "0000-0002-0570-7908";
        String workId = "100126388";
        String url = this.urlRenderer.getWorkDetailsUrl(orcidId, workId);
        assertEquals(UrlRenderer.BASE_URL + orcidId + "/" + UrlRenderer.WORK_PARAM + "/" + workId , url);
    }
}
