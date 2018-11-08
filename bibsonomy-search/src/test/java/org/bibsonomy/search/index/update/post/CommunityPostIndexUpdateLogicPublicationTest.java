package org.bibsonomy.search.index.update.post;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/**
 * tests for the community update logic for "normal" publication entries
 *
 * @author dzo
 */
public class CommunityPostIndexUpdateLogicPublicationTest extends CommunityPostIndexUpdateLogicTest<BibTex> {

	private static final CommunityPostIndexUpdateLogic<BibTex> UPDATE_LOGIC = (CommunityPostIndexUpdateLogic<BibTex>) SearchSpringContextWrapper.getBeanFactory().getBean("communityIndexUpdateLogicPublication");

	@Override
	protected CommunityPostIndexUpdateLogic<BibTex> getUpdateLogic() {
		return UPDATE_LOGIC;
	}

	@Override
	protected void testNewEntities(List<Post<BibTex>> newCommunityPosts) {

	}

	@Override
	protected long getLastEntityIdForNewEntities() {
		return 0;
	}
}
