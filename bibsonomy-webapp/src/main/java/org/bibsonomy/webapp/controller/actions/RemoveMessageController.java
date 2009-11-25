package org.bibsonomy.webapp.controller.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.RemoveMessageCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * With this Controller we delete Messages from a user's inbox
 * TODO: implement it ;-)
 * @author sdo
 * @version $Id$
 */
public class RemoveMessageController implements MinimalisticController<RemoveMessageCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(DeletePostController.class);
	
	private RequestLogic requestLogic;
	private LogicInterface logic;
	private Errors errors;

	
	public RemoveMessageCommand instantiateCommand() {
		return new RemoveMessageCommand();
	}
	
	public View workOn(RemoveMessageCommand command){
		RequestWrapperContext context = command.getContext();
		/*
		 * user has to be logged in to delete
		 */
		if (!context.isUserLoggedIn()){
			errors.reject("error.general.login");
		}
		/*
		 * check the ckey
		 */
		if (context.isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, ckey is valid");
			// delete the message
			final List<Post<? extends Resource>> posts = createObjects(command);
			logic.deleteInboxMessages(posts, false);
		} else {
			errors.reject("error.field.valid.ckey");
		}
		/*
		 * if there are errors, show them
		 */
		if (errors.hasErrors()){
			return Views.ERROR;
		}
		// go back where you've come from
		return new ExtendedRedirectView(requestLogic.getReferer());
	}
	
	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors=errors;

	}
	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
	

	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}	
	
	private List<Post<? extends Resource>> createObjects(RemoveMessageCommand command){
		// create new list and necessary variables
		final List<Post<? extends Resource>> posts = new ArrayList<Post<? extends Resource>>();
		
		// get the has string
		final String hash = command.getHash();
		/*
		 * add one post - "pick one"
		 */
		final Post<BibTex> post = new Post<BibTex>();
		final User user = new User();
		//we could any Resource but since we don't need this information in the inbox...
		final BibTex bib = new BibTex();
		bib.setIntraHash(hash);
		post.setResource(bib);
		user.setName(command.getUser());
		post.setUser(user);
		posts.add(post);
		return posts;
	}
}
