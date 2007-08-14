package org.bibsonomy.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.common.enums.GroupID;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ValidationUtilsTest {

	@Test
	public void present() {
		// String
		assertFalse(ValidationUtils.present(""));
		assertFalse(ValidationUtils.present(" "));
		assertTrue(ValidationUtils.present("hurz"));

		// Collection
		assertFalse(ValidationUtils.present(Collections.EMPTY_LIST));
		final Collection<String> c = new ArrayList<String>();
		c.add("hurz");
		assertTrue(ValidationUtils.present(c));

		// Object
		assertTrue(ValidationUtils.present(new Object()));

		// GroupID
		assertFalse(ValidationUtils.present(GroupID.INVALID));
		for (final GroupID gid : GroupID.values()) {
			if (gid == GroupID.INVALID) continue;
			assertTrue(ValidationUtils.present(gid));
			assertTrue(ValidationUtils.presentValidGroupId(gid.getId()));
		}
	}

	@Test
	public void nullOrEqual() {
		assertFalse(ValidationUtils.nullOrEqual("", "hurz"));
		assertFalse(ValidationUtils.nullOrEqual("hurz", ""));
		assertTrue(ValidationUtils.nullOrEqual(null, null));
		assertTrue(ValidationUtils.nullOrEqual(null, "hurz"));
		assertTrue(ValidationUtils.nullOrEqual("hurz", "hurz"));
	}
}