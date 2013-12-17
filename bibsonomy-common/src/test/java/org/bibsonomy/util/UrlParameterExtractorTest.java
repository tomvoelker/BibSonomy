/**
 *
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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

package org.bibsonomy.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jensi
  */
public class UrlParameterExtractorTest {

	/**
	 * Tests parseParameterValueFromUrl
	 */
	@Test
	public void testParseParameterValueFromUrl() {
		UrlParameterExtractor serviceObj = new UrlParameterExtractor("hurz");
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org?hurz=a+b"));
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org?hahaha=hihihi&hurz=a+b"));
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org?hahaha=hihihi&hurz=a+b&hohoho=lalala"));
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org/hurz=bla?hurz=a+b&hohoho=lalala"));
	}

}
