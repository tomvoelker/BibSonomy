package org.bibsonomy.search.index.generator.post;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for {@link CommunityPostIndexGenerationLogic} for {@link GoldStandardPublication}
 *
 * @author dzo
 */
public class CommunityPostIndexGenerationLogicPublicationTest extends CommunityPostIndexGenerationLogicTest<GoldStandardPublication> {

	private static final CommunityPostIndexGenerationLogic<GoldStandardPublication> GENERATION_LOGIC = (CommunityPostIndexGenerationLogic<GoldStandardPublication>) SearchSpringContextWrapper.getBeanFactory().getBean("communityPublicationGenerationDBLogic");

	@Override
	protected CommunityPostIndexGenerationLogic<GoldStandardPublication> getLogic() {
		return GENERATION_LOGIC;
	}

	@Override
	protected int getNumberOfEntites() {
		return 11;
	}

	@Override
	protected void testEntities(List<Post<GoldStandardPublication>> entities) {
		final Post<GoldStandardPublication> testPost = getFirstPostByInterHash(entities, "ac6aa3ccb181e61801cefbc1401d409a");
		final List<User> usersOfPost = testPost.getUsers();
		assertThat(usersOfPost.size(), is(1));
		assertThat(usersOfPost, contains(equalTo(new User("testuser2"))));
	}
}