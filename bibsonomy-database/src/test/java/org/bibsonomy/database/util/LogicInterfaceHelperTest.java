package org.bibsonomy.database.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class LogicInterfaceHelperTest {

	/**
	 * tests buildParam
	 */
	@Test
	public void buildParam() {
		GenericParam param;

		for (final Class<? extends GenericParam> paramClass : new Class[] { BookmarkParam.class, BibTexParam.class, TagParam.class, TagRelationParam.class, UserParam.class, GroupParam.class }) {
			param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, "hash", null, 0, 10, "search-string", null, new User());
			assertEquals(paramClass, param.getClass());
			assertEquals(" +search-string", param.getSearch());
			assertEquals("hash", param.getHash());

			// TODO: do we want the LIMIT to be 0?
			param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, "", null, 12, 10, null, null, new User());
			assertEquals(0, param.getLimit());

			// hash
			final String testHash = "11111111111111111111111111111111";
			for (final int hashId : HashID.getHashRange()) {
				param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, hashId + testHash, null, 12, 10, null, null, new User());
				if (paramClass == BibTexParam.class) {
					assertEquals(HashID.getSimHash(hashId).getId(), ((BibTexParam) param).getSimHash());
				} else if (paramClass == TagParam.class) {
					assertEquals(HashID.getSimHash(hashId).getId(), ((TagParam) param).getSimHash());
				}
			}
			for (final Object hashId : new Object[] { "a" /* , 4, 5, 6 */}) {
				try {
					param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, hashId + testHash, null, 12, 10, null, null, new User());
					fail("Expected exception");
				} catch (RuntimeException ignore) {
				}
			}
		}

	}
}