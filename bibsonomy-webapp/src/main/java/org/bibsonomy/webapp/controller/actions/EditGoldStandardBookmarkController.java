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

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ObjectUtils;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.GoldStandardPostValidator;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * @author dzo
 */
public class EditGoldStandardBookmarkController extends EditBookmarkController {

	@Override
	protected View getPostView() {
		return Views.EDIT_GOLD_STANDARD_BOOKMARK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.webapp.controller.actions.EditPostController#getPostDetails
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	protected Post<Bookmark> getPostDetails(final String intraHash, final String userName) {
		return super.getPostDetails(intraHash, "");
	}

	@Override
	protected void prepareResourceForDatabase(final Bookmark resource) {
		// noop
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Post<Bookmark> getCopyPost(final User loginUser, final String hash, final String user) {
		Post<Bookmark> post = null;
		try {
			post = (Post<Bookmark>) this.logic.getPostDetails(hash, user);
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
	protected View finalRedirect(final String userName, final Post<Bookmark> post, final String referer) {
		if ((referer == null) || referer.matches(".*/editGoldStandardBookmark.*")) {
			return new ExtendedRedirectView(this.urlGenerator.getBookmarkUrl(post.getResource(), null));
		}

		return super.finalRedirect(userName, post, referer);
	}

	private Post<Bookmark> convertToGoldStandard(final Post<Bookmark> post) {
		if (!present(post)) {
			return null;
		}

		final Post<Bookmark> gold = new Post<Bookmark>();

		final GoldStandardBookmark goldResource = new GoldStandardBookmark();
		ObjectUtils.copyPropertyValues(post.getResource(), goldResource);
		gold.setResource(goldResource);

		return gold;
	}

	@Override
	protected String getGrouping(final User requestedUser) {
		return null;
	}

	@Override
	protected EditBookmarkCommand instantiateEditPostCommand() {
		return new EditBookmarkCommand();
	}

	@Override
	protected Bookmark instantiateResource() {
		return new GoldStandardBookmark();
	}

	@Override
	protected PostValidator<Bookmark> getValidator() {
		return new GoldStandardPostValidator<Bookmark>();
	}

	@Override
	protected void setRecommendationFeedback(final TagRecommendationEntity post, final int postID) {
		// noop gold standards have no tags
	}
}
