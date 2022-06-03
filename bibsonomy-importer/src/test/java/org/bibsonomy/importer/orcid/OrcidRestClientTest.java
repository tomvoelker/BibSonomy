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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bibsonomy.junit.RemoteTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RemoteTest.class)
public class OrcidRestClientTest {

    private static String PATH_TO_FILES = "src/test/resources/orcid/";
    private OrcidRestClient client;

    @Before
    public void setUp() throws Exception {
        this.client = new OrcidRestClient();
    }

    @Test
    public void testGetWorks() {
        String orcidId = "0000-0002-0570-7908";

        String result = this.client.getWorks(orcidId);

        File file = new File(PATH_TO_FILES + "works.json");
        String expected = "";
        try {
            expected = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
                    .lines().collect(Collectors.joining("\n"));
        } catch (FileNotFoundException e) {
            // noop
        }
        assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(result));
    }

    @Test
    public void testGetWorkDetails() {
        String orcidId = "0000-0002-0570-7908";
        String workId = "100126388";

        String result = this.client.getWorkDetails(orcidId, workId);

        File file = new File(PATH_TO_FILES + "workDetails.json");
        String expected = "";
        try {
            expected = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
                    .lines().collect(Collectors.joining("\n"));
        } catch (FileNotFoundException e) {
            // noop
        }
        assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(result));
    }

    @Test
    public void testGetWorkDetailsBulk() {
        String orcidId = "0000-0002-0570-7908";
        List<String> worksIds = new ArrayList<>();
        worksIds.add("100126388");
        worksIds.add("95856315");

        String result = this.client.getWorkDetailsBulk(orcidId, worksIds);

        File file = new File(PATH_TO_FILES + "workDetailsBulk.json");
        String expected = "";
        try {
            expected = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
                    .lines().collect(Collectors.joining("\n"));
        } catch (FileNotFoundException e) {
            // noop
        }
        assertEquals(removeWhiteSpaces(expected), removeWhiteSpaces(result));
    }

    private String removeWhiteSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }

}
