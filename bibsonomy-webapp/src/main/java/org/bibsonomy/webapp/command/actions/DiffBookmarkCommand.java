package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author PlatinAge
 */
public class DiffBookmarkCommand extends EditBookmarkCommand{
	private Post<Bookmark> postDiff;

	@Override
	public Post<Bookmark> getComparePost() {
		return postDiff;
	}

	@Override
	public void setComparePost(Post<Bookmark> postDiff) {
		this.postDiff = postDiff;
	}
}
