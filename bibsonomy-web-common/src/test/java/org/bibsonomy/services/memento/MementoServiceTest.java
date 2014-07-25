/**
 *
 *  BibSonomy-Web-Common - Common things for web
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
