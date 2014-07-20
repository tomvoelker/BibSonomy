package org.bibsonomy.services.memento;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

/**
 * 
 * tests for {@link MementoService}
 *
 * @author rja
 */
public class MementoServiceTest {

	@Test
	public void testGetQueryUrl() throws MalformedURLException {
		final MementoService m = new MementoService(new URL("http://mementoweb.org/timegate/"));
		
		assertEquals("http://mementoweb.org/timegate/http://www.l3s.de/", m.getQueryUrl("http://www.l3s.de/"));
		assertEquals("http://mementoweb.org/timegate/http://www.l3s.de/?param=what_happens_to_params", m.getQueryUrl("http://www.l3s.de/?param=what_happens_to_params"));
		assertEquals("http://mementoweb.org/timegate/http://www.l3s.de/?param=what_happens_to_params&param2=even_with_two", m.getQueryUrl("http://www.l3s.de/?param=what_happens_to_params&param2=even_with_two"));
	}

	@Test
	public void testGetMementoUrl() throws MalformedURLException {
		final MementoService m = new MementoService(new URL("http://mementoweb.org/timegate/"));

		final String url = "http://www.l3s.de/";
		final URL mementoUrl = m.getMementoUrl(url, MementoService.RFC1123_DATE_TIME_FORMATTER.parseDateTime("Thu, 27 July 2006 12:00:00 GMT").toDate());
		
		assertNotNull(mementoUrl);
		assertEquals("http://web.archive.org/web/20060718084715/http://www.l3s.de/", mementoUrl.toString());
		// TODO: implement tests for URLs with query parameters
	}

}
