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

import org.bibsonomy.model.util.MiscFieldConflictResolutionStrategy;
import org.junit.Test;

/**
 * tests for {@link BibTex}
 *
 * @author dzo
 */
public class BibTexTest {

	/**
	 * tests {@link BibTex#syncMiscFields(MiscFieldConflictResolutionStrategy)}
	 */
	@Test
	public void testSyncMiscFields() {
		final BibTex publication = new BibTex();

		final String key = "key";
		final String value2 = "value2";
		final String value1 = "value1";
		final String misc2 = "  " + key + " = {" + value2 + "}";
		publication.setMisc(misc2);
		publication.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_WINS);
		assertEquals(misc2, publication.getMisc());

		publication.addMiscField(key, value1);
		publication.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_WINS);

		assertEquals(value2, publication.getMiscField(key));
		assertEquals(misc2, publication.getMisc());
		assertTrue(publication.isMiscFieldParsed());

		publication.addMiscField(key, value1);
		publication.syncMiscFields(MiscFieldConflictResolutionStrategy.MISC_FIELD_MAP_WINS);

		final String misc1 = "  " + key + " = {" + value1 + "}";
		assertEquals(value1, publication.getMiscField(key));
		assertEquals(misc1, publication.getMisc());
		assertTrue(publication.isMiscFieldParsed());

	}
}