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

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author fba
 */
public class EditBookmarkController extends EditPostController<Bookmark, EditBookmarkCommand> {

	@Override
	protected View getPostView() {
		return Views.EDIT_BOOKMARK; // TODO: this could be configured using
									// Spring!
	}

	@Override
	protected Bookmark instantiateResource() {
		return new Bookmark();
	}

	@Override
	protected PostValidator<Bookmark> getValidator() {
		return new PostValidator<Bookmark>();
	}

	@Override
	protected EditBookmarkCommand instantiateEditPostCommand() {
		return new EditBookmarkCommand();
	}

	@Override
	protected void setDuplicateErrorMessage(final Post<Bookmark> post, final Errors errors) {
		errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
	}

	@Override
	protected void workOnCommand(final EditBookmarkCommand command, final User loginUser) {
		// noop
	}

	@Override
	public View workOn(final EditBookmarkCommand command) {
		/*
		 * if URL of resource null show POST_BOOKMARK view and
		 * initialize didYouKnowMessageCommand
		 */
		if (!present(command.getPost().getResource().getUrl()) && !present(command.getIntraHashToUpdate()) && !present(command.getHash())) {
			this.initializeDidYouKnowMessageCommand(command);
			command.getPost().getResource().setUrl("http://");
			return Views.POST_BOOKMARK;
		}

		/*
		 * otherwise use editPost workflow
		 */
		return super.workOn(command);
	}

	@Override
	protected void replaceResourceSpecificPostFields(final Post<Bookmark> post, final String key, final Post<Bookmark> newPost) {
		try {
			super.replacePostFields(post, key, newPost);
			return;
		} catch (final ValidationException e) {
			// ignore
		}
		switch (key) {
		case "title":
			post.getResource().setTitle(newPost.getResource().getTitle());
			break;
		case "url":
			post.getResource().setUrl(newPost.getResource().getUrl());
			break;
		case "description":
			post.setDescription(newPost.getDescription());
			break;
		default:
			throw new ValidationException("Couldn't find " + key + " among Bookmark fields!");
		}
	}

}
