package org.bibsonomy.lucene.util;

import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * class for converting BibTex post objects to lucene documents and vice versa
 * @author fei
 *
 */
public class LuceneBibTexConverter extends LuceneResourceConverter<BibTex> {

	@Override
	protected Post<BibTex> createEmptyPost() {
		BibTex bibTex = new BibTex();
		User user = new User();
		Post<BibTex> post = new LucenePost<BibTex>();
		post.setResource(bibTex);
		post.setUser(user);
		return post;
	}
}
