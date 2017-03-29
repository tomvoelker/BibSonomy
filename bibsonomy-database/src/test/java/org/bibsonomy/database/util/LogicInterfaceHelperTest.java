/**
 * BibSonomy-Database - Database for BibSonomy.
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
 */
public class LogicInterfaceHelperTest extends AbstractDatabaseTest {

	/**
	 * tests buildParam
	 */
	@Test
	public void buildParam() {
		for (final Class<? extends GenericParam> paramClass : new Class[] { BookmarkParam.class, BibTexParam.class, TagParam.class, TagRelationParam.class, UserParam.class, GroupParam.class }) {
			final String searchString = "search-string";
			GenericParam param = LogicInterfaceHelper.buildParam(paramClass, null, "", null, "hash", null, 0, 10, null, null, searchString, null, new User());
			assertEquals(paramClass, param.getClass());
			assertEquals(searchString, param.getSearch());
			assertEquals("hash", param.getHash());

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