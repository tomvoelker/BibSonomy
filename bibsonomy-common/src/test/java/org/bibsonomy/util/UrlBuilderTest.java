/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class UrlBuilderTest {
	
	@Test
	public void testBuilding() throws URISyntaxException {
		final String protocol = "http";
		final String host = "test.com";
		final UrlBuilder urlBuilder = new UrlBuilder(protocol + "://" + host);
		
		final String paramKey1 = "test";
		final String paramKey2 = "parameter";
		final String paramValue2 = "value value";
		urlBuilder.addParameter(paramKey1, paramKey1).addParameter(paramKey2, paramValue2);
		
		final URI url = new URI(urlBuilder.asString());
		
		assertEquals(protocol, url.getScheme());
		assertEquals(host, url.getHost());
		
		final Map<String, String> queryMap = getQueryMap(url.getQuery());
		assertEquals(paramKey1, UrlUtils.safeURIDecode(queryMap.get(paramKey1)));
		assertEquals(paramValue2, UrlUtils.safeURIDecode(queryMap.get(paramKey2)));
	}
	
	private static Map<String, String> getQueryMap(String query) {
		if (!present(query)) {
			return Collections.emptyMap();
		}
	    final String[] params = query.split("&");  
	    final Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params) {  
	        final String[] keyValue = param.split("=");
	        map.put(keyValue[0], keyValue[1]);  
	    }  
	    return map;  
	}  
}
