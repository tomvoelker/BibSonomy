/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
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

package org.bibsonomy.rest.utils;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.SortedMap;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class HeaderUtilsTest {

	@Test
	public void testGetPreferredTypes() {
		/*
		 * was throwing an exception (broken header?!)
		 */
		final String header = "text/html,application/xhtml+xml,application/xml;image/png,image/jpeg,image/*;q=0.9,*/*;q=0.8";
		final SortedMap<Double, List<String>> preferredTypes = HeaderUtils.getPreferredTypes(header);
		assertEquals("{1.0=[text/html, application/xhtml+xml, application/xml, image/jpeg], 0.9=[image/*], 0.8=[*/*]}", preferredTypes.toString());
	
	}

}
