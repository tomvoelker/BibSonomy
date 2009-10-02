package org.bibsonomy.webapp.controller.ajax;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ajax.BasketManagerCommand;
import org.bibsonomy.webapp.controller.AjaxController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class BasketController extends AjaxController implements MinimalisticController<BasketManagerCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(BasketController.class);
	
	private Errors errors;
	
	private RequestLogic requestLogic;

	@Override
	public BasketManagerCommand instantiateCommand() {
		return new BasketManagerCommand();
	}

	@Override
	public View workOn(BasketManagerCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		// user has to be logged in
		if (!command.getContext().isUserLoggedIn()){
			/*
			 * TODO: send to login page with meaningful help message
			 */
			return new ExtendedRedirectView("/");
		}
		
		// check if ckey is valid
		if (!command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		// var to save basketsize
		int basketSize = 0;
		
		// if clear all is set, clear all
		if ("clearAll".equals(command.getAction())){
			logic.deleteBasketItems(null, true);
		
			return new ExtendedRedirectView(requestLogic.getReferer());
		}
		
		// create list of posts by hash data and given username
		final List<Post<? extends Resource>> posts = createObjects(command);

		// decide which method will be called
		if (command.getAction().startsWith("pick")){
			basketSize = logic.createBasketItems(posts);
		}
		if (command.getAction().startsWith("unpick")){
			basketSize = logic.deleteBasketItems(posts, false);
		}
		
		// set new basektsize
		command.setResponseString(Integer.toString(basketSize));
		
		return Views.AJAX;
	}

	/**
	 * private method to extract hashes and user from one string
	 * 
	 * @param command
	 * @return List<Post<BibTex>>
	 */
	private List<Post<? extends Resource>> createObjects(BasketManagerCommand command){
		// create new list and necessary variables
		final List<Post<? extends Resource>> posts = new ArrayList<Post<? extends Resource>>();
		
		// get the has string
		final String hash = command.getRequestedResourceHash();
		
		// if its bigger than 33 chars split it else easy handling
		if (hash.length() > 33){
			/*
			 * add several posts - "pick all"
			 */
			for (final String s:hash.split(" ")){
				final Post<BibTex> post = new Post<BibTex>();
				final User user = new User();
				final BibTex bib = new BibTex();
				
				// split string i.e. 1717560e1867fcb75197fe8689e1cc0d/daill
				final String[] hashAndOwner = s.split("/");

				user.setName(hashAndOwner[1]);
				
				bib.setIntraHash(hashAndOwner[0].substring(1, hashAndOwner[0].length()));
				post.setResource(bib);
				post.setUser(user);
				
				posts.add(post);
			}
		} else {
			/*
			 * add one post - "pick one"
			 */
			final Post<BibTex> post = new Post<BibTex>();
			final User user = new User();
			final BibTex bib = new BibTex();
			
			bib.setIntraHash(hash);
			post.setResource(bib);
			
			user.setName(command.getUser());
			post.setUser(user);
			
			posts.add(post);
		}
				
		return posts;
	}

	public Errors getErrors() {
		return this.errors;
	}

	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
	
	/**
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}	
}
