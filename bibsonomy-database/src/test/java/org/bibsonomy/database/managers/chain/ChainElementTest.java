package org.bibsonomy.database.managers.chain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ChainElementTest {

	@Test
	public void present() {
		final ChainElement chain = new ChainElement<Bookmark, BookmarkParam>() {
			@Override
			protected boolean canHandle(final BookmarkParam param) {
				return false;
			}

			@Override
			protected List<Bookmark> handle(final BookmarkParam param, final Transaction session) {
				return null;
			}
		};

		// String
		assertFalse(chain.present(""));
		assertTrue(chain.present("hurz"));

		// Collection
		assertFalse(chain.present(Collections.EMPTY_LIST));
		final Collection<String> c = new ArrayList<String>();
		c.add("hurz");
		assertTrue(chain.present(c));

		// Object
		assertTrue(chain.present(new Object()));

		// GroupID
		assertFalse(chain.present(GroupID.GROUP_INVALID));
		for (final GroupID gid : GroupID.values()) {
			if (gid == GroupID.GROUP_INVALID) continue;
			assertTrue(chain.present(gid));
			assertTrue(chain.presentValidGroupId(gid.getId()));
		}

		// null or equal
		assertFalse(chain.nullOrEqual("", "hurz"));
		assertFalse(chain.nullOrEqual("hurz", ""));
		assertTrue(chain.nullOrEqual(null, null));
		assertTrue(chain.nullOrEqual(null, "hurz"));
		assertTrue(chain.nullOrEqual("hurz", "hurz"));
	}
}