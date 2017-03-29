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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author dzo
 */
public class ListExtensionCheckerTest {
	
	private static final String EXTENSION_1 = "pdf";
	private static final String EXTENSION_2 = "abc";
	private static final String EXTENSION_3 = "app";
	private static final ListExtensionChecker EXTENSION_CHECKER = new ListExtensionChecker(Arrays.asList(EXTENSION_1, EXTENSION_2, EXTENSION_3));
	
	/**
	 * tests for {@link ListExtensionChecker#checkExtension(String)}
	 * @throws Exception
	 */
	@Test
	public void testCheckExtension() throws Exception {
		assertTrue(EXTENSION_CHECKER.checkExtension(EXTENSION_1));
		assertTrue(EXTENSION_CHECKER.checkExtension(EXTENSION_2));
		assertTrue(EXTENSION_CHECKER.checkExtension(EXTENSION_3));
		assertFalse(EXTENSION_CHECKER.checkExtension("thisextension"));
	}

}
