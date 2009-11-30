/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.rest.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.UnsupportedHttpMethodException;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class HttpMethodTest {

	@Test
	public void testGetHttpMethod() {
		assertEquals(HttpMethod.GET, HttpMethod.getHttpMethod("get"));
		assertEquals(HttpMethod.POST, HttpMethod.getHttpMethod("post"));
		assertEquals(HttpMethod.PUT, HttpMethod.getHttpMethod("put"));
		assertEquals(HttpMethod.DELETE, HttpMethod.getHttpMethod("delete"));

		assertEquals(HttpMethod.GET, HttpMethod.getHttpMethod("GET"));
		assertEquals(HttpMethod.POST, HttpMethod.getHttpMethod("pOSt"));
		assertEquals(HttpMethod.PUT, HttpMethod.getHttpMethod("pUt"));
		assertEquals(HttpMethod.DELETE, HttpMethod.getHttpMethod("dElEtE"));

		assertEquals(HttpMethod.GET, HttpMethod.getHttpMethod(" GeT "));

		try {
			HttpMethod.getHttpMethod("hurz");
			fail("Should throw exception");
		} catch (final UnsupportedHttpMethodException ex) {
		}

		try {
			HttpMethod.getHttpMethod("");
			fail("Should throw exception");
		} catch (final UnsupportedHttpMethodException ex) {
		}

		try {
			HttpMethod.getHttpMethod(null);
			fail("Should throw exception");
		} catch (final InternServerException ex) {
		}
	}

	/*
	 * We want to make sure that this is the case, because we are relying on it
	 * in our testcases.
	 */
	@Test
	public void testToString() {
		assertEquals("GET", HttpMethod.GET.toString());
		assertEquals("POST", HttpMethod.POST.toString());
		assertEquals("PUT", HttpMethod.PUT.toString());
		assertEquals("DELETE", HttpMethod.DELETE.toString());
	}
}