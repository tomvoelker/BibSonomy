/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import java.util.ArrayList;
import java.util.List;

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
 * 
 * @author sdo
 */
public class RemoveMessageController implements MinimalisticController<RemoveMessageCommand>, ErrorAware {
	private RequestLogic requestLogic;
	private LogicInterface logic;
	private Errors errors;

	@Override
	public RemoveMessageCommand instantiateCommand() {
		return new RemoveMessageCommand();
	}
	
	@Override
	public View workOn(final RemoveMessageCommand command){
		final RequestWrapperContext context = command.getContext();
		/*
		 * user has to be logged in to delete
		 */
		if (!context.isUserLoggedIn()){
			errors.reject("error.general.login");
		}
		/*
		 * check the ckey
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		/*
		 * if there are errors, show them
		 */
		if (errors.hasErrors()){
			return Views.ERROR;
		}

		if (command.isClear()) {
			/*
			 * delete all messages
			 */
			logic.deleteInboxMessages(null, true);
		} else {
			logic.deleteInboxMessages(createObjects(command), false);
		}

		/*
		 * go back where you've come from
		 */
		return new ExtendedRedirectView(requestLogic.getReferer());
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
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

	private List<Post<? extends Resource>> createObjects(final RemoveMessageCommand command){
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
