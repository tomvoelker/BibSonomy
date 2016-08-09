/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for {@link SearchInfoDBLogic}
 *
 * @author dzo
 */
public class SearchInfoDBLogicTest {
	
	private static SearchInfoDBLogic LOGIC;
	
	/**
	 * retrieves the logic from the config
	 */
	@BeforeClass
	public static final void setLogic() {
		LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean(SearchInfoDBLogic.class);
	}
	
	/**
	 * tests {@link SearchInfoDBLogic#getUserNamesThatShareDocumentsWithUser(String)}
	 */
	@Test
	public void testGetUserNamesThatShareDocumentsWithUser() {
		final Set<String> users = LOGIC.getUserNamesThatShareDocumentsWithUser("testuser1");
		assertEquals(1, users.size());
	}
}
