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
package org.bibsonomy.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import org.bibsonomy.model.util.MiscFieldConflictResolutionStrategy;
import java.util.Map;
import org.bibsonomy.model.util.BibTexUtils;
import org.junit.Test;

/**
 * tests for {@link BibTex}
 *
 * @author dzo
 */
public class BibTexTest {

	/**
	 * tests {@link BibTex#syncMiscFields(MiscFieldConflictResolutionStrategy)}
	 * This test shall verify that the misc-string and the misc-field are kept in sync via syncMiscFields
	 */
	@Test
	public void testSyncMiscFields() {
		final BibTex publication = new BibTex();

		final String key = "key";
		final String key3 = "key3";
		final String value1 = "value1";
		final String value2 = "value2";
		final String misc1 = "  " + key + " = {" + value1 + "}";
		final String misc2 = "  " + key + " = {" + value2 + "}";
		final String misc3 = "  " + key3 + " = {" + value2 + "}";
		final Map<String,String> misc2fields = BibTexUtils.parseMiscFieldString(misc2);
		
		// Alter misc entry via setMisc
		publication.setMisc(misc2);  // Sync is implied now
		assertTrue(publication.isMiscFieldParsed()); // check for that
		assertEquals(misc2, publication.getMisc()); // Assert that miscField equals misc string

		
		// Alter misc entry via setMiscFields
		publication.setMiscFields(misc2fields); //Sync is implied
		assertTrue(publication.isMiscFieldParsed()); // check for that
		assertEquals(misc2, publication.getMisc());
		
		// Alter misc entry via addMiscFields
		publication.addMiscField(key, value1); // Sync is implied
		assertTrue(publication.isMiscFieldParsed()); // check for that	
		assertEquals(misc1, publication.getMisc());

		// Alter misc entry via removeMiscField
		publication.addMiscField(key3, value2); // Sync is implied
		assertTrue(publication.isMiscFieldParsed()); // check for that
		publication.removeMiscField(key); // Sync is implied
		assertTrue(publication.isMiscFieldParsed()); // check for that
		assertNull(publication.getMiscField(key));
		assertEquals(value2, publication.getMiscField(key3));
		assertEquals(misc3, publication.getMisc());
			
	}
}