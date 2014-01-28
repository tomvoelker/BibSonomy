package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author PlatinAge
 */
public class DiffBookmarkCommand extends EditBookmarkCommand{
	private Post<Bookmark> postDiff;

	@Override
	public Post<Bookmark> getPostDiff() {
		return postDiff;
	}

	@Override
	public void setPostDiff(Post<Bookmark> postDiff) {
		this.postDiff = postDiff;
	}
}
