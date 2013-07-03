package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author PlatinAge
 * @version $Id$
 */
public class DiffBookmarkCommand extends EditBookmarkCommand{
	private Post<Bookmark> postDiff;

	public Post<Bookmark> getPostDiff() {
		return postDiff;
	}

	public void setPostDiff(Post<Bookmark> postDiff) {
		this.postDiff = postDiff;
	}
}
