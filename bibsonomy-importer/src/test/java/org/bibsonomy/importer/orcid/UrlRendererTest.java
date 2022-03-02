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
