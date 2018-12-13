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
