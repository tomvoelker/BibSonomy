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
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ObjectUtils;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.command.actions.EditGoldStandardBookmarkCommand;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.GoldStandardPostValidator;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * @author dzo
 */
public class EditGoldStandardBookmarkController extends EditPostController<GoldStandardBookmark, EditGoldStandardBookmarkCommand>{

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#instantiateEditPostCommand()
	 */
	@Override
	protected EditGoldStandardBookmarkCommand instantiateEditPostCommand() {
		return new EditGoldStandardBookmarkCommand();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#instantiateResource()
	 */
	@Override
	protected GoldStandardBookmark instantiateResource() {
		return new GoldStandardBookmark();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#workOnCommand(org.bibsonomy.webapp.command.actions.EditPostCommand, org.bibsonomy.model.User)
	 */
	@Override
	protected void workOnCommand(EditGoldStandardBookmarkCommand command,
			User loginUser) {
		// noop
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#getPostView()
	 */
	@Override
	protected View getPostView() {
		return Views.EDIT_GOLD_STANDARD_BOOKMARK;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#setDuplicateErrorMessage(org.bibsonomy.model.Post, org.springframework.validation.Errors)
	 */
	@Override
	protected void setDuplicateErrorMessage(Post<GoldStandardBookmark> post,
			Errors errors) {
		errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
		
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.actions.EditPostController#getValidator()
	 */
	@Override
	protected PostValidator<GoldStandardBookmark> getValidator() {
		return new GoldStandardPostValidator<GoldStandardBookmark>();
	}
	
	@Override
	public View workOn(final EditGoldStandardBookmarkCommand command) {
		/* 
		 * if URL of resource null show POST_BOOKMARK view and 
		 * initialize didYouKnowMessageCommand  
		 */
	/*	if (!present(command.getPost().getResource().getUrl()) && !present(command.getIntraHashToUpdate()) && !present(command.getHash())) {
			initializeDidYouKnowMessageCommand(command);
			command.getPost().getResource().setUrl("http://");
			return Views.POST_BOOKMARK;
		}
		*/
		/*
		 * otherwise use editPost workflow
		 */
		return super.workOn(command);
	}
	
	
}
