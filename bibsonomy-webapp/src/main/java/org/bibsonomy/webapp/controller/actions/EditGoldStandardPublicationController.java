/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ObjectUtils;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.GoldStandardPostValidator;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * controller for the edit gold standard publication form
 * - editGoldStandardPublication
 * 
 * @author dzo
 */
public class EditGoldStandardPublicationController extends AbstractEditPublicationController<PostPublicationCommand> {
	private static final int ADMIN = 0;
	@Override
	protected View getPostView() {
		return Views.EDIT_GOLD_STANDARD_PUBLICATION;
	}

	@Override
	protected Post<BibTex> getPostDetails(final String intraHash, final String userName) {
		/*
		 * get goldstandard post; username must be empty!
		 */
		return super.getPostDetails(intraHash, "");
	}

	@Override
	protected void prepareResourceForDatabase(final BibTex resource) {
		// noop
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Post<BibTex> getCopyPost(final User loginUser, final String hash, final String user) {
		Post<BibTex> post = null;
		try {
			post = (Post<BibTex>) this.logic.getPostDetails(hash, user);
		} catch (final ObjectNotFoundException ex) {
			// ignore
		} catch (final ResourceMovedException ex) {
			// ignore
		}

		if (post == null) {
			return null;
		}

		return this.convertToGoldStandard(post);
	}

	@Override
	protected View finalRedirect(final String userName, final Post<BibTex> post, final String referer) {
		if ((referer == null) || referer.matches(".*/editGoldStandardPublication.*") || referer.contains("/history/")) {
			return new ExtendedRedirectView(this.urlGenerator.getPublicationUrl(post.getResource(), null));
		}

		return super.finalRedirect(userName, post, referer);
	}

	private Post<BibTex> convertToGoldStandard(final Post<BibTex> post) {
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
	protected String getGrouping(final User requestedUser) {
		return null;
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
	protected void setRecommendationFeedback(final TagRecommendationEntity post, final int postID) {
		// noop gold standards have no tags
	}
	
	@Override
	protected void preparePost(EditPostCommand<BibTex> command, Post<BibTex> post) {
		
		super.preparePost(command, post);
		User loginUser = command.getContext().getLoginUser();
		
		if(loginUser.getRole().getRole() == ADMIN){
			if (command.isApproved()){
				post.setApproved(1);
			}else{
				post.setApproved(0);
			}
		}
		
	}

}
