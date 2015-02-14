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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ajax.ClipboardManagerCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.ajax.ClipboardValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author Christian Kramer
 */
public class ClipboardController extends AjaxController implements ValidationAwareController<ClipboardManagerCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(ClipboardController.class);
	
	private Errors errors;

	@Override
	public ClipboardManagerCommand instantiateCommand() {
		return new ClipboardManagerCommand();
	}

	@Override
	public View workOn(ClipboardManagerCommand command) {
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
		}
		final String action = command.getAction();
		
		if (errors.hasErrors()) {
			return Views.ERROR;
		}
		
		// if clear all is set, clear all
		if ("clearAll".equals(action)) {
			logic.deleteBasketItems(null, true);
			return new ExtendedRedirectView(requestLogic.getReferer());
		}
		
		// create list of posts by hash data and given username
		final List<Post<? extends Resource>> posts = createObjects(command);
		
		/*
		 * new clipboard size
		 */
		int clipboardSize = 0;
		/*
		 * decide which method will be called
		 */
		if (action.startsWith("pick")){
			clipboardSize = logic.createBasketItems(posts);
		} else if (action.startsWith("unpick")){
			clipboardSize = logic.deleteBasketItems(posts, false);
		}
		/*
		 * set new clipboard size
		 */
		command.setResponseString(Integer.toString(clipboardSize));
		
		return Views.AJAX_TEXT;
	}

	/**
	 * private method to extract hashes and user from one string
	 * 
	 * @param command
	 * @return List<Post<BibTex>>
	 */
	private static List<Post<? extends Resource>> createObjects(final ClipboardManagerCommand command){
		// create new list and necessary variables
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		
		// get the has string
		final String hash = command.getHash();
		
		// if its bigger than 33 chars split it else easy handling
		if (present(hash) && hash.length() > 33){
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
	private static Post<BibTex> createPost(final String intraHash, final String userName) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex publication = new BibTex();
		
		publication.setIntraHash(intraHash);
		post.setResource(publication);
		post.setUser(new User(userName));
		return post;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#getValidator()
	 */
	@Override
	public Validator<ClipboardManagerCommand> getValidator() {
		return new ClipboardValidator();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#isValidationRequired(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public boolean isValidationRequired(ClipboardManagerCommand command) {
		return true;
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
