/**
 * 
 */
package org.bibsonomy.recommender.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.webapp.command.EditBookmarkCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class BookmarkController extends SimpleFormController {

	private static final Log log = LogFactory.getLog(BookmarkController.class);

	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		final EditBookmarkCommand cmd = new EditBookmarkCommand();
		
		final Post<Bookmark> post = new Post<Bookmark>();
		
		post.setResource(new Bookmark());
		cmd.setPost(post);
		
		return cmd;
	}
	
	@Override
	protected ModelAndView onSubmit(Object command, BindException errors) throws Exception {
		
		
		if (command instanceof EditBookmarkCommand) {
			final EditBookmarkCommand epCmd = (EditBookmarkCommand) command;
			
			final Post<Bookmark> post = epCmd.getPost();
			
			System.out.println(post);
		}
		
		System.out.println("got command " + command);
		System.out.println("got errors  " + errors);
		
		
		return super.onSubmit(command, errors);
	}
	
}

