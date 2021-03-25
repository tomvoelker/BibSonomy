package org.bibsonomy.search.index.generator.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for "normal" bookmarks
 *
 * @author dzo
 */
public class CommunityPostIndexGenerationLogicUserBookmarkTest extends CommunityPostIndexGenerationLogicTest<Bookmark> {

	private static final CommunityPostIndexGenerationLogic<Bookmark> GENERATION_LOGIC = (CommunityPostIndexGenerationLogic<Bookmark>) SearchSpringContextWrapper.getBeanFactory().getBean("communityNormalBookmarkGenerationDBLogic");

	@Override
	protected CommunityPostIndexGenerationLogic<Bookmark> getLogic() {
		return GENERATION_LOGIC;
	}

	@Override
	protected int getNumberOfEntites() {
		return 9;
	}

	@Override
	protected void testEntities(List<Post<Bookmark>> entities) {
		assertThat(entities.get(3).getDescription(), is("KDE Page"));
	}
}
