package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostPostValidator;
import org.bibsonomy.webapp.validation.PostPublicationValidator;
import org.bibsonomy.webapp.view.Views;

/**
 * Posting/editing one (!) publication posts.
 * 
 * TODO:
 * - scraper id must be written into DB
 * - scraper metadata must be handled
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class PostPublicationController extends PostPostController<BibTex> {
	
	@Override
	protected View getPostView() {
		return Views.POST_PUBLICATION; // TODO: this could be configured using Spring!
	}

	@Override
	protected String getRedirectUrl(Post<BibTex> post) {
		return  post.getResource().getUrl();
	}

	@Override
	protected BibTex instantiateResource() {
		final BibTex publication = new BibTex();
		/*
		 * set default values.
		 */
//		publication.setUrl("http://");
		return publication;
	}

	@Override
	protected PostPostValidator<BibTex> getValidator() {
		return new PostPublicationValidator();
	}

	@Override
	protected EditPostCommand<BibTex> instantiateEditPostCommand() {
		return new EditPublicationCommand();
	}

}
