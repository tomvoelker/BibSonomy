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
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
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
			return new ExtendedRedirectView("/login");
		}
		
		// check if ckey is valid
		if (!command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		
		// if clear all is set, clear all
		if ("clearAll".equals(command.getAction())) {
			logic.deleteBasketItems(null, true);
		
			return new ExtendedRedirectView(requestLogic.getReferer());
		}
		
		// create list of posts by hash data and given username
		final List<Post<? extends Resource>> posts = createObjects(command);
		
		/*
		 * new basket size
		 */
		int basketSize = 0;
		/*
		 * decide which method will be called
		 */
		if (command.getAction().startsWith("pick")){
			basketSize = logic.createBasketItems(posts);
		} else if (command.getAction().startsWith("unpick")){
			basketSize = logic.deleteBasketItems(posts, false);
		}
		/*
		 * set new basket size
		 */
		command.setResponseString(Integer.toString(basketSize));
		
		return Views.AJAX_TEXT;
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
		final String hash = command.getHash();
		
		// if its bigger than 33 chars split it else easy handling
		if (hash.length() > 33){
			/*
			 * add several posts - "pick all"
			 */
			for (final String s : hash.split(" ")){
				/*
				 * split string i.e. 1717560e1867fcb75197fe8689e1cc0d/daill
				 */
				final String[] hashAndOwner = s.split("/");
				posts.add(createPost(hashAndOwner[0].substring(1, hashAndOwner[0].length()), hashAndOwner[1]));
			}
		} else {
			posts.add(createPost(hash, command.getUser()));
		}
				
		return posts;
	}

	/**
	 * Creates a new (empty) post with the given username and intrahash.
	 * 
	 * @param intraHash
	 * @param userName
	 * @return
	 */
	private Post<BibTex> createPost(final String intraHash, final String userName) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex publication = new BibTex();
		
		publication.setIntraHash(intraHash);
		post.setResource(publication);
		post.setUser(new User(userName));
		return post;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
}
