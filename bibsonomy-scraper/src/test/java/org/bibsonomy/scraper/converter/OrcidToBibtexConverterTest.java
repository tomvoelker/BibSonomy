package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.bibsonomy.testutil.TestUtils;
import org.junit.Before;
import org.junit.Test;

public class OrcidToBibtexConverterTest {

    private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/orcid/";

    private OrcidToBibtexConverter converter;

    @Before
    public void setUp() throws Exception {
        this.converter = new OrcidToBibtexConverter();
    }

    @Test
    public void testDetailsToBibtex() throws IOException {
        final String citation = TestUtils.readEntryFromFile(PATH_TO_FILES + "orcidDetails.json");

        // convert
        final String bibTeX = this.converter.toBibtex(citation);

        // test the conversion
        final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "csltobibtextest1.bib").trim();
        assertEquals(expectedBibTeX, bibTeX);
    }

    @Test
    public void testDetailsWithSourceToBibtex() throws IOException {
        final String citation = TestUtils.readEntryFromFile(PATH_TO_FILES + "orcidDetailsWithBibtex.json");

        // convert
        final String bibTeX = this.converter.toBibtex(citation);

        // test the conversion
        final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "csltobibtextest1.bib").trim();
        assertEquals(expectedBibTeX, bibTeX);
    }

}
