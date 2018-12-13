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
