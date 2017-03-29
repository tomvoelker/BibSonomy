/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.services.filesystem.extension;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * @author dzo
 */
public class WildcardExtensionCheckerTest {
	
	private static final WildcardExtensionChecker EXTENSION_CHECKER = new WildcardExtensionChecker();

	/**
	 * tests {@link WildcardExtensionChecker#checkExtension(String)}
	 */
	@Test
	public void testCheckExtension() {
		assertTrue(EXTENSION_CHECKER.checkExtension("pdf"));
		assertTrue(EXTENSION_CHECKER.checkExtension("app"));
		assertTrue(EXTENSION_CHECKER.checkExtension("apk"));
		assertTrue(EXTENSION_CHECKER.checkExtension(null));
		assertTrue(EXTENSION_CHECKER.checkExtension("pptx"));
		assertTrue(EXTENSION_CHECKER.checkExtension("abcdefghijklmnopqrstuvwxyz"));
	}
}
