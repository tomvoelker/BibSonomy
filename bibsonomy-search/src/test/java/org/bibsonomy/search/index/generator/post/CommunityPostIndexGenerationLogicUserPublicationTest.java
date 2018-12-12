package org.bibsonomy.search.index.generator.post;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;

import java.util.List;

/***
 * tests for "normal" publications
 *
 * @author dzo
 */
public class CommunityPostIndexGenerationLogicUserPublicationTest extends CommunityPostIndexGenerationLogicTest<BibTex>  {

	private static final CommunityPostIndexGenerationLogic<BibTex> GENERATION_LOGIC = (CommunityPostIndexGenerationLogic<BibTex>) SearchSpringContextWrapper.getBeanFactory().getBean("communityNormalPublicationGenerationDBLogic");

	@Override
	protected CommunityPostIndexGenerationLogic<BibTex> getLogic() {
		return GENERATION_LOGIC;
	}

	@Override
	protected int getNumberOfEntites() {
		return 8;
	}

	@Override
	protected void testEntities(List<Post<BibTex>> entities) {
		assertThat(entities.get(1).getResource().getTitle(), is("A case for abductive reasoning over ontologies"));
	}
}
