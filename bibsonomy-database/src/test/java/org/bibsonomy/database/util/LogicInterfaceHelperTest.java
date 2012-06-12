package org.bibsonomy.database.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.AbstractDatabaseTest;
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
public class LogicInterfaceHelperTest extends AbstractDatabaseTest {

	/**
	 * tests buildParam
	 */
	@Test
	public void buildParam() {
		for (final Class<? extends GenericParam> paramClass : new Class[] { BookmarkParam.class, BibTexParam.class, TagParam.class, TagRelationParam.class, UserParam.class, GroupParam.class }) {
			GenericParam param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, "hash", null, 0, 10, null, null, "search-string", null, new User());
			assertEquals(paramClass, param.getClass());
			assertEquals(" +search-string", param.getSearch());
			assertEquals("hash", param.getHash());

			// TODO: do we want the LIMIT to be 0?
			param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, "", null, 12, 10, null, null, null, null, new User());
			assertEquals(LogicInterfaceHelper.DEFAULT_LIST_LIMIT, param.getLimit());

			// hash
			final String testHash = "11111111111111111111111111111111";
			for (final int hashId : HashID.getHashRange()) {
				param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, hashId + testHash, null, 12, 10, null, null, null, null, new User());
				if (paramClass == BibTexParam.class) {
					assertEquals(HashID.getSimHash(hashId).getId(), ((BibTexParam) param).getSimHash());
				} else if (paramClass == TagParam.class) {
					assertEquals(HashID.getSimHash(hashId).getId(), ((TagParam) param).getSimHash());
				}
			}
			for (final Object hashId : new Object[] { "a" /* , 4, 5, 6 */}) {
				try {
					param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, hashId + testHash, null, 12, 10, null, null, null, null, new User());
					fail("Expected exception");
				} catch (final RuntimeException ignore) {
				}
			}
		}
	}
	
	@Test
	public void testBuilding() {
		final GenericParam param = LogicInterfaceHelper.buildParam(BibTexParam.class, GroupingEntity.USER, "testuser1", Arrays.asList("test", "->test", "<->test2", "test3->", "-->test4", "test5-->"), "thisisastrangehash", null, 0, 10, null, null, "", null, new User());
		assertEquals(1, param.getNumTransitiveConcepts());
		assertEquals(1, param.getNumSimpleConcepts());
		assertEquals(1, param.getNumSimpleTags());
		assertEquals(2, param.getNumSimpleConceptsWithParent());
		assertEquals(1, param.getNumCorrelatedConcepts());
	}
}