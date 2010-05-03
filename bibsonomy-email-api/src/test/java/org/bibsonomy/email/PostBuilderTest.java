package org.bibsonomy.email;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class PostBuilderTest {

	@Test
	public void testBuildPosts() {
		final PostBuilder postBuilder = new PostBuilder();
		postBuilder.setUrlProvider(new UrlProvider());
		
		final Collection<Email> emails = new EmailParserTest().getEmails().values();
		for (final Email email : emails) {
			final List<Post<? extends Resource>> posts = postBuilder.buildPosts(email, "johndoe", "public");
			for (final Post<? extends Resource> post : posts) {
				assertEquals("johndoe", post.getUser().getName());
				assertEquals(email.getTags(), post.getTags());
				assertEquals("http://", ((Bookmark) post.getResource()).getUrl().substring(0, 7));
			}
		}

	}

}
