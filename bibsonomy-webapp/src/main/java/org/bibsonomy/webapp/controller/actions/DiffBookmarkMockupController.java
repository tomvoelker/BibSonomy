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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.DiffBookmarkCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author PlatinAge
 */
public class DiffBookmarkMockupController extends EditPostController<Bookmark, DiffBookmarkCommand> {
	private static final Log LOGGER = LogFactory.getLog(DiffBookmarkMockupController.class);

	@Override
	protected DiffBookmarkCommand instantiateEditPostCommand() {
		final DiffBookmarkCommand command = new DiffBookmarkCommand();
		Post<Bookmark> tmpTestPost = new Post<Bookmark>(); 
		command.setPostDiff(tmpTestPost);
		command.getPostDiff().setResource(this.instantiateTestResource());
		tmpTestPost.setDescription("testausgabe stelle DiffBookmarkController, initiateEditPostCommand()");
		//command.setPostID(RecommenderStatisticsManager.getUnknownPID());
		
		/*command.setPostDiff(new Post<Bookmark>());
		command.getPostDiff().setResource(this.instantiateResource());
		//command.setPostID(RecommenderStatisticsManager.getUnknownPID());
		*/
		return command;
	}

	//@Override
	protected Bookmark instantiateTestResource() {
		// TODO Auto-generated method stub
		Bookmark bookmark = new Bookmark();
		bookmark.setUrl("http://www.bibsonomy.de");
		//TODO: tmp bookmark variables for postDiff tests
		bookmark.setTitle("biblicious:test bookmark");
		return bookmark;
	}

	@Override
	protected void workOnCommand(DiffBookmarkCommand command, User loginUser) {
		// TODO Auto-generated method stub
	}

	@Override
	protected View getPostView() {
		return Views.DIFFBOOKMARKPAGE;
	}

	@Override
	protected void setDuplicateErrorMessage(Post<Bookmark> post, Errors errors) {
		errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
	}

	@Override
	protected PostValidator<Bookmark> getValidator() {
		return new PostValidator<Bookmark>();
	}

	@Override
	protected Bookmark instantiateResource() {
		// TODO Auto-generated method stub
		return new Bookmark();
	}
	
}
