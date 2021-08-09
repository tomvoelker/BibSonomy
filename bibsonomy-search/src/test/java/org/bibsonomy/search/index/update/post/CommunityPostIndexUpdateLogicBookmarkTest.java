/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.search.index.update.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for "normal" bookmarks for the community post index
 * @author dzo
 */
public class CommunityPostIndexUpdateLogicBookmarkTest extends CommunityPostIndexUpdateLogicTest<Bookmark> {

	private static final CommunityPostIndexUpdateLogic<Bookmark> UPDATE_LOGIC = (CommunityPostIndexUpdateLogic<Bookmark>) SearchSpringContextWrapper.getBeanFactory().getBean("communityIndexUpdateLogicBookmark");

	@Override
	protected CommunityPostIndexUpdateLogic<Bookmark> getUpdateLogic() {
		return UPDATE_LOGIC;
	}

	@Override
	protected void testNewEntities(List<Post<Bookmark>> newCommunityPosts) {
		assertThat(newCommunityPosts.get(0).getResource().getUrl(), is("https://www.uni-kassel.de/eecs/"));
	}

	@Override
	protected long getLastEntityIdForNewEntities() {
		return 0;
	}
}
