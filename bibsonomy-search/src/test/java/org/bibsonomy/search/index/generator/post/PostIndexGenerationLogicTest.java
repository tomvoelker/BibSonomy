package org.bibsonomy.search.index.generator.post;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for {@link PostIndexGenerationLogic}
 *
 * @author dzo
 */
public class PostIndexGenerationLogicTest extends AbstractDatabaseManagerTest {

	private static final PostIndexGenerationLogic<BibTex> INDEX_GENERATION_LOGIC = (PostIndexGenerationLogic<BibTex>) SearchSpringContextWrapper.getBeanFactory().getBean("publicationGenerationDBLogic");

	/**
	 * tests {@link PostIndexGenerationLogic#getEntities(int, int)}
	 * @throws Exception
	 */
	@Test
	public void testGetPostEntries() throws Exception {
		final List<Post<BibTex>> posts = INDEX_GENERATION_LOGIC.getEntities(0, 100);
		assertEquals(22, posts.size());
		// check for documents
		for (final Post<BibTex> searchPost : posts) {
			final BibTex publication = searchPost.getResource();
			if ("b77ddd8087ad8856d77c740c8dc2864a".equals(publication.getIntraHash()) && "testuser1".equals(searchPost.getUser().getName())) {
				assertEquals(2, publication.getDocuments().size());
			}
		}
	}
}