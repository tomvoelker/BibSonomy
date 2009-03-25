/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.util;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class WebUtilsTest {

	
	
	@Test
	public void testExtractCharset1() {
		assertEquals("UTF-8", WebUtils.extractCharset("text/html; charset=utf-8; qs=1"));
	}
	

	@Test
	public void testExtractCharset2() {
		assertEquals("ISO-8859-1", WebUtils.extractCharset("text/html; charset=ISO-8859-1"));
	}

	@Test
	public void testExtractCharset3() {
		assertEquals("LATIN1", WebUtils.extractCharset("text/html; charset=latin1; qs=1"));
	}

	@Test
	public void testRedirectUrl() throws MalformedURLException {
		assertEquals("http://www.bibsonomy.org/groups", WebUtils.getRedirectUrl(new URL("http://www.bibsonomy.org/group")).toString());
	}
	
	
}
