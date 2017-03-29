/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.extra.BibTexExtra;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
// FIXME adapt to new test db
public class BibTexExtraDatabaseManagerTest extends AbstractDatabaseManagerTest {

    private final String BIB_TEST_HASH = "b77ddd8087ad8856d77c740c8dc2864a"; // INTRA-hash
    private final String TEST_USER = "testuser1";
    private final String TEST_URL = "http://www.example.com/";
    private final String TEST_TXT = "This is a test...";

    private static BibTexExtraDatabaseManager bibTexExtraDb;

    /**
     * sets up the used managers
     */
    @BeforeClass
    public static void setupDatabaseManager() {
        bibTexExtraDb = BibTexExtraDatabaseManager.getInstance();
    }

    @Ignore
    @Test
    public void getURL() {
        final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
        assertEquals(2, extras.size());

        assertEquals("http://localhost/mywiki/literature/BG98.pdf", extras.get(0).getUrl().toString());
        assertEquals("Local", extras.get(0).getText());

        assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(1).getUrl().toString());
        assertEquals("Online Version", extras.get(1).getText());
    }

    @Ignore
    @Test
    public void createURL() {
        bibTexExtraDb.createURL(this.BIB_TEST_HASH, this.TEST_USER, this.TEST_URL, this.TEST_TXT, this.dbSession);
        final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
        assertEquals(3, extras.size());
        assertEquals(this.TEST_URL, extras.get(0).getUrl().toString());
        assertEquals(this.TEST_TXT, extras.get(0).getText());
    }

    @Ignore
    @Test
    public void deleteURL() throws MalformedURLException {
        bibTexExtraDb.deleteURL(this.BIB_TEST_HASH, this.TEST_USER, new URL("http://localhost/mywiki/literature/BG98.pdf"), this.dbSession);
        final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
        assertEquals(1, extras.size());
        assertEquals("http://members.pingnet.ch/gamma/junit.htm", extras.get(0).getUrl().toString());
    }

    @Ignore
    @Test
    public void deleteAllURLs() {
        // 925724 is the contentId for the hash b6c9a44d411bf8101abdf809d5df1431
        bibTexExtraDb.deleteAllURLs(925724, this.dbSession);
        final List<BibTexExtra> extras = bibTexExtraDb.getURL(this.BIB_TEST_HASH, this.TEST_USER, this.dbSession);
        assertEquals(0, extras.size());
    }

    @Ignore
    @Test
    public void updateURL() {
        bibTexExtraDb.updateURL(925724, 12345678, this.dbSession);
    }

    @Ignore
    @Test
    public void updateDocument() {
        bibTexExtraDb.updateDocument(813954, 12345678, this.dbSession);
    }

    @Test
    public void insertAndDeleteExtendedFieldData() {
        Map<String, List<String>> extendedFieldList = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);
        assertEquals(1, extendedFieldList.size());

        final List<String> keys = extendedFieldList.get(extendedFieldList.keySet().iterator().next());

        assertEquals(3, keys.size());

        bibTexExtraDb.createExtendedField("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", "ACM", "TEST", this.dbSession);

        extendedFieldList = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);
        assertEquals(2, extendedFieldList.size());

        Map<String, List<String>> extendedFields = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);

        assertEquals(2, extendedFields.size());
        extendedFields = bibTexExtraDb.getExtendedFieldsByKey("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", "ACM", this.dbSession);

        assertEquals(1, extendedFields.size());

        bibTexExtraDb.deleteExtendedFieldByKeyValue("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", "ACM", "TEST", this.dbSession);
        extendedFields = bibTexExtraDb.getExtendedFields("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", this.dbSession);

        assertEquals(1, extendedFields.size());

        extendedFields = bibTexExtraDb.getExtendedFieldsByKey("testuser1", "b77ddd8087ad8856d77c740c8dc2864a", "ACM", this.dbSession);

        assertEquals(0, extendedFields.size());

    }

    @Test
    public void getExtendedFieldByKey() {
        final Map<String, List<String>> exFields = bibTexExtraDb.getExtendedFieldsByKey("testuser2", "1b298f199d487bc527a62326573892b8", "JEL", this.dbSession);

        final List<String> keys = exFields.get(exFields.keySet().iterator().next());

        assertEquals(3, keys.size());

    }


    @Ignore
    @Test
    public void updateExtendedFieldsData() {
        bibTexExtraDb.updateExtendedFieldsData(783786, 12345678, this.dbSession);
    }
}