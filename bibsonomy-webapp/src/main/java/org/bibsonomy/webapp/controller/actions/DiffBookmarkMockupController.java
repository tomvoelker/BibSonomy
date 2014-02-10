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
		command.setComparePost(tmpTestPost);
		command.getComparePost().setResource(this.instantiateTestResource());
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
