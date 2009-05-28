/**
 * 
 */
package org.bibsonomy.recommender.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.webapp.command.EditPublicationCommand;
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
public class PublicationController extends SimpleFormController {

	private static final Log log = LogFactory.getLog(PublicationController.class);

	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		final EditPublicationCommand cmd = new EditPublicationCommand();
		
		final Post<BibTex> post = new Post<BibTex>();
		
		post.setResource(new BibTex());
		post.setUser(new User());
		cmd.setPost(post);
		
		return cmd;
	}
	
	
	@Override
	protected ModelAndView onSubmit(Object command, BindException errors) throws Exception {
		
		if (command instanceof EditPublicationCommand) {
			final EditPublicationCommand epCmd = (EditPublicationCommand) command;
			
			final Post<BibTex> post = epCmd.getPost();
			
			System.out.println(post);
			
			
		}
		
		System.out.println("got command " + command);
		System.out.println("got errors  " + errors);
		
		
		return super.onSubmit(command, errors);
	}
	
}

