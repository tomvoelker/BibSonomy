package org.bibsonomy.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.common.enums.GroupID;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ValidationUtilsTest {

	private ValidationUtils check;

	@Before
	public void setUp() {
		this.check = ValidationUtils.getInstance();
	}

	@Test
	public void present() {
		// String
		assertFalse(this.check.present(""));
		assertTrue(this.check.present("hurz"));

		// Collection
		assertFalse(this.check.present(Collections.EMPTY_LIST));
		final Collection<String> c = new ArrayList<String>();
		c.add("hurz");
		assertTrue(this.check.present(c));

		// Object
		assertTrue(this.check.present(new Object()));

		// GroupID
		assertFalse(this.check.present(GroupID.INVALID));
		for (final GroupID gid : GroupID.values()) {
			if (gid == GroupID.INVALID) continue;
			assertTrue(this.check.present(gid));
			assertTrue(this.check.presentValidGroupId(gid.getId()));
		}
	}

	@Test
	public void nullOrEqual() {
		assertFalse(this.check.nullOrEqual("", "hurz"));
		assertFalse(this.check.nullOrEqual("hurz", ""));
		assertTrue(this.check.nullOrEqual(null, null));
		assertTrue(this.check.nullOrEqual(null, "hurz"));
		assertTrue(this.check.nullOrEqual("hurz", "hurz"));
	}
}