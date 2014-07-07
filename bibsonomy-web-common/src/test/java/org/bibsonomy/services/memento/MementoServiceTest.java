package org.bibsonomy.services.memento;


import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class MementoServiceTest {

	@Test
	public void testGetQueryUrl() {
		try {
			final MementoService m = new MementoService(new URL("http://mementoweb.org/timegate/"));
		
			assertEquals("http://mementoweb.org/timegate/http://www.l3s.de/", m.getQueryUrl("http://www.l3s.de/"));
			assertEquals("http://mementoweb.org/timegate/http://www.l3s.de/?param=what_happens_to_params", m.getQueryUrl("http://www.l3s.de/?param=what_happens_to_params"));
			assertEquals("http://mementoweb.org/timegate/http://www.l3s.de/?param=what_happens_to_params&param2=even_with_two", m.getQueryUrl("http://www.l3s.de/?param=what_happens_to_params&param2=even_with_two"));
			
		} catch (MalformedURLException e) {
			fail("Could not format URL");
		}
	}

	@Test
	public void testGetMementoUrl() {
		try {
			final MementoService m = new MementoService(new URL("http://mementoweb.org/timegate/"));

			final URL mementoUrl = m.getMementoUrl("http://www.l3s.de/", "Thu, 27 July 2006 12:00:00 GMT");
		
			assertNotNull(mementoUrl);
			assertTrue(mementoUrl.toString().contains("www.l3s.de"));
			
			// TODO: implement tests for URLs with query parameters
			
		} catch (MalformedURLException e) {
			fail("Could not format URL");
		}
	}

}
