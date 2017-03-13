/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.csl;

import static org.bibsonomy.util.ValidationUtils.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for {@link CSLFilesManager}
 * @author dzo
 */
public class CSLFilesManagerTest {
	
	private static final CSLFilesManager MANAGER = new CSLFilesManager();
	
	/**
	 * init the {@link #MANAGER}
	 */
	@BeforeClass
	public static void init() {
		MANAGER.init();
	}
	
	/**
	 * tests {@link CSLFilesManager#getStyleByName(String)}
	 */
	@Test
	public void testGetStyleByName() {
		assertNull(MANAGER.getStyleByName("mycsl"));
		final String cslID = "colombian-journal-of-anesthesiology";
		final CSLStyle style = MANAGER.getStyleByName(cslID);
		assertEquals("Colombian Journal of Anesthesiology", style.getDisplayName());
		assertEquals(cslID + ".csl", style.getId());
	}
	
	/**
	 * tests {@link CSLFilesManager#getLocaleFile(String)}
	 */
	@Test
	public void testGetLocaleFile() {
		assertNotNull(MANAGER.getLocaleFile("de-DE"));
	}
}
