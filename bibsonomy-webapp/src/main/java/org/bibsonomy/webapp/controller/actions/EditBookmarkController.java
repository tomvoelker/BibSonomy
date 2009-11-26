package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.EditBookmarkValidator;
import org.bibsonomy.webapp.validation.EditPostValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author fba
 * @version $Id$
 */
public class EditBookmarkController extends EditPostController<Bookmark> {
	
	@Override
	protected View getPostView() {
		return Views.EDIT_BOOKMARK; // TODO: this could be configured using Spring!
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
	protected EditPostValidator<Bookmark> getValidator() {
		return new EditBookmarkValidator();
	}

	@Override
	protected EditPostCommand<Bookmark> instantiateEditPostCommand() {
		return new EditBookmarkCommand();
	}

	@Override
	protected void setDuplicateErrorMessage(Post<Bookmark> post, Errors errors) {
		errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
	}

	@Override
	protected void workOnCommand(EditPostCommand<Bookmark> command, User loginUser) {
		// noop
	}

}
