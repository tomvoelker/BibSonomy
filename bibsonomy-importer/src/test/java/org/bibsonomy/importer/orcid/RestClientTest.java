package org.bibsonomy.importer.orcid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class RestClientTest {

    private RestClient client;

    @Before
    public void setUp() throws Exception {
        this.client = new RestClient();
    }

    @Test
    public void testGetWorks() {
        String orcidId = "0000-0002-0570-7908";

        String result = this.client.getWorks(orcidId);
        assertEquals("", result);
    }

    @Test
    public void testGetWorkDetails() {
        String orcidId = "0000-0002-0570-7908";
        String workId = "100126388";

        String result = this.client.getWorkDetails(orcidId, workId);
        assertEquals("", result);
    }
}
