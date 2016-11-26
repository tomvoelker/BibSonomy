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