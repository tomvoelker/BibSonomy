package org.bibsonomy.webapp.command.actions;

import java.util.LinkedList;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.PostCommand;

/**
 * @author fba
 * @version $Id$
 */
public class EditBookmarkCommand extends PostCommand {
	private boolean userLoggedIn;
	private Post<Bookmark> postBookmark;
	
	public EditBookmarkCommand() {
		postBookmark = new Post<Bookmark>();
		postBookmark.setResource(new Bookmark());
//		postBookmark.getResource().getTitle()
//		postBookmark.setTags(new LinkedList<Tag>());
	}

	public Post<Bookmark> getPostBookmark() {
		return this.postBookmark;
	}

	public void setUserLoggedIn(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}

	public boolean isUserLoggedIn() {
		return userLoggedIn;
	}
}
