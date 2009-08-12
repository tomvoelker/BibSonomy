package org.bibsonomy.webapp.controller.ajax;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
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
public class BasketManagerController extends AjaxController implements MinimalisticController<BasketManagerCommand>, ErrorAware{
	private static final Log log = LogFactory.getLog(BasketManagerController.class);
	
	private Errors errors;
	
	private LogicInterface logic;
	private RequestLogic requestLogic;

	@Override
	public BasketManagerCommand instantiateCommand() {
		return new BasketManagerCommand();
	}

	@Override
	public View workOn(BasketManagerCommand command) {
		log.info(this.getClass().getSimpleName());
		
		// user has to be logged in
		if (!command.getContext().isUserLoggedIn()){
			return new ExtendedRedirectView("/");
		}
		
		//check if ckey is valid
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
		List<Post<BibTex>> posts = createObjects(command);

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
	private List<Post<BibTex>> createObjects(BasketManagerCommand command){
		// create new list and necessary variables
		List<Post<BibTex>> posts = new ArrayList<Post<BibTex>>();
		Post<BibTex> post;
		User user;
		BibTex bib;
		
		// get the has string
		String hash = command.getRequestedResourceHash();
		
		// if its bigger than 33 chars split it else easy handling
		if (hash.length() > 33){
			for (String s:hash.split(" ")){
				post = new Post<BibTex>();
				user = new User();
				bib = new BibTex();
				
				// split string i.e. 1717560e1867fcb75197fe8689e1cc0d/daill
				String[] hashAndOwner = s.split("/");

				user.setName(hashAndOwner[1]);
				
				bib.setIntraHash(hashAndOwner[0].substring(1, hashAndOwner[0].length()));
				post.setResource(bib);
				post.setUser(user);
				
				posts.add(post);
			}
		} else {
			post = new Post<BibTex>();
			user = new User();
			bib = new BibTex();
			
			bib.setIntraHash(hash);
			post.setResource(bib);
			
			user.setName(command.getUser());
			post.setUser(user);
			
			posts.add(post);
		}
				
		return posts;
	}
	
	/**
	 * set logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
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
