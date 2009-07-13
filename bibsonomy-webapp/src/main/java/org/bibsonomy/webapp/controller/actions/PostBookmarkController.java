package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostBookmarkValidator;
import org.bibsonomy.webapp.validation.PostPostValidator;
import org.bibsonomy.webapp.view.Views;

/**
 * @author fba
 * @version $Id$
 */
public class PostBookmarkController extends PostPostController<Bookmark> {
	
	@Override
	protected View getPostView() {
		return Views.POST_BOOKMARK; // TODO: this could be configured using Spring!
	}

	@Override
	protected String getRedirectUrl(Post<Bookmark> post) {
		return  post.getResource().getUrl();
	}

	@Override
	protected Bookmark instantiateResource() {
		final Bookmark bookmark = new Bookmark();
		/*
		 * set default values.
		 */
		bookmark.setUrl("http://");
		return bookmark;
	}

	@Override
	protected PostPostValidator<Bookmark> getValidator() {
		return new PostBookmarkValidator();
	}

	@Override
	protected EditPostCommand<Bookmark> instantiateEditPostCommand() {
		return new EditBookmarkCommand();
	}

}
