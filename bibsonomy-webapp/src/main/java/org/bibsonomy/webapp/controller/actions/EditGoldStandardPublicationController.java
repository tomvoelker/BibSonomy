package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.ObjectUtils;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
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
		 * get goldstandard post
		 */
		final Post<BibTex> post = super.getPostDetails(intraHash, "");
		if (present(post)) {
			return post;
		}
		
		/*
		 * maybe someone will create a new goldstandard from an normal user post
		 */
		if (present(userName)) {
			final Post<BibTex> postDetails = super.getPostDetails(intraHash, userName);
			return this.convertToGoldStandard(postDetails);
		}
		
		return null;
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
