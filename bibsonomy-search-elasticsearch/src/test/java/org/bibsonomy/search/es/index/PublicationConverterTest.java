package org.bibsonomy.search.es.index;

import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.index.utils.SimpleFileContentExtractorService;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author dzo
 */
public class PublicationConverterTest {
	private static final PublicationConverter CONVERTER = new PublicationConverter(TestUtils.createURI("https://www.bibsonomy.org"), new SimpleFileContentExtractorService());

	@Test
	public void testConvert() {
		final Post<BibTex> post = new Post<>();
		post.setUser(new User("testuser"));
		final BibTex publication = new BibTex();
		publication.setMisc("editors = { Wade Wilson and Vanessa Geraldine Carlysle }");
		post.setResource(publication);
		final Map<String, Object> convertedPost = CONVERTER.convert(post);
		assertFalse("converted post contains misc field", convertedPost.containsKey(ESConstants.Fields.Publication.EDITORS));
	}
}