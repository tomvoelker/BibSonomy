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
package org.bibsonomy.search.index.generator.post;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * @author dzo
 */
public class CommunityPostIndexGenerationLogicBookmarkTest extends CommunityPostIndexGenerationLogicTest<GoldStandardBookmark> {

	@Override
	protected CommunityPostIndexGenerationLogic<GoldStandardBookmark> getLogic() {
		return (CommunityPostIndexGenerationLogic<GoldStandardBookmark>) SearchSpringContextWrapper.getBeanFactory().getBean("communityBookmarkGenerationDBLogic");
	}

	@Override
	protected int getNumberOfEntites() {
		return 1;
	}

	@Override
	protected void testEntities(List<Post<GoldStandardBookmark>> entities) {
		final List<User> usersOfPost = entities.get(0).getUsers();
		assertThat(usersOfPost.size(), is(1));
		assertThat(usersOfPost, contains(equalTo(new User("testuser2"))));
	}

}
