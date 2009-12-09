package org.bibsonomy.lucene.util;

import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;

/**
 * class for converting Bookmark post objects to lucene documents and vice versa
 * @author fei
 *
 */
public class LuceneBookmarkConverter extends LuceneResourceConverter<Bookmark> {

	@Override
	protected Post<Bookmark> createEmptyPost() {
		Bookmark bookmark = new Bookmark();
		User user = new User();
		Post<Bookmark> post = new LucenePost<Bookmark>();
		post.setResource(bookmark);
		post.setUser(user);
		post.getResource().recalculateHashes();
		return post;
	}
}
