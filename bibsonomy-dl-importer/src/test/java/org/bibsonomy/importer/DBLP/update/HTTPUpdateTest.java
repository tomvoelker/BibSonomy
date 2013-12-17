package org.bibsonomy.importer.DBLP.update;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.MalformedURLException;

import org.bibsonomy.importer.DBLP.DBLPException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 */
public class HTTPUpdateTest {

	@Test
	@Ignore
	public void testOpenSession() throws MalformedURLException, IOException, DBLPException {
		
		final String cookie = "dummy";
		
		final HTTPBookmarkUpdate httpUpdate = new HTTPBookmarkUpdate("http://www.bibsonomy.org/", "dblp", "db_user=" + cookie + ";");
		
		assertNotNull(httpUpdate.cKey);
	}

}
