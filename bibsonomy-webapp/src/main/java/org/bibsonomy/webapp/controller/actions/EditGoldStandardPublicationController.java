package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ObjectUtils;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.GoldStandardPostValidator;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 * @version $Id$
 */
public class EditGoldStandardPublicationController extends AbstractEditPublicationController<PostPublicationCommand> {
	
	@Override
	protected View getPostView() {
		return Views.EDIT_GOLD_STANDARD_PUBLICATION;
	}
	
	@Override
	protected Post<BibTex> getPostDetails(String intraHash, String userName) {
		/*
		 * get goldstandard post; username must be empty!
		 */
		return super.getPostDetails(intraHash, "");
	}
	
	@Override
	protected Post<BibTex> getCopyPost(User loginUser, String hash, String user) {
		@SuppressWarnings("unchecked")
		Post<BibTex> post = null;
		try {
			post = (Post<BibTex>) this.logic.getPostDetails(hash, user);
		} catch (ResourceNotFoundException ex) {
			// ignore
		} catch (ResourceMovedException ex) {
			// ignore		
		}
		return this.convertToGoldStandard(post);
	}
	
	@Override
	protected boolean canEditPost(RequestWrapperContext context) {
		return super.canEditPost(context) && Role.ADMIN.equals(context.getLoginUser().getRole());
	}
	
	@Override
	protected View getAccessDeniedView(final PostPublicationCommand command) {
		throw new AccessDeniedException("You are not allowed to edit Goldstandards!!!");
	}
	
	private Post<BibTex> convertToGoldStandard(Post<BibTex> post) {
		if (!present(post)) {
			return null;
		}
		
		final Post<BibTex> gold = new Post<BibTex>();
		
		final GoldStandardPublication goldP = new GoldStandardPublication();
		ObjectUtils.copyPropertyValues(post.getResource(), goldP);
		gold.setResource(goldP);
		
		return gold;
	}
	
	@Override
	protected PostPublicationCommand instantiateEditPostCommand() {
		return new PostPublicationCommand();
	}
	
	@Override
	protected BibTex instantiateResource() {
		return new GoldStandardPublication();
	}
	
	@Override
	protected PostValidator<BibTex> getValidator() {
		return new GoldStandardPostValidator<BibTex>();
	}
	
	@Override
	protected void setRecommendationFeedback(final Post<BibTex> post, final int postID) {
		// noop gold standards have no tags
	}

}
