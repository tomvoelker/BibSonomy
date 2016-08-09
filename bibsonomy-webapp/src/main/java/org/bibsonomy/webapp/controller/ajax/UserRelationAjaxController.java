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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ajax.UserRelationAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;


/**
 * This controller catches ajax requests on the /ajax/handleUserRelation url
 * and handles the add and remove actions of general user relations
 * 
 * @author Christian Kramer, Folke Mitzlaff
 */
public class UserRelationAjaxController extends AjaxController implements MinimalisticController<UserRelationAjaxCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(UserRelationAjaxController.class);
	
	private static final String ADD_FOLLOWER = "addFollower";
	private static final String REMOVE_RELATION = "removeRelation";
	private static final String ADD_RELATION = "addRelation";	
	private static final String ADD_FRIEND = "addFriend";
	private static final String REMOVE_FRIEND = "removeFriend";
	private static final String REMOVE_FOLLOWER = "removeFollower";

	
	private static final int SPHERENAME_MAX_LENGTH = 64;
	
	/** We allow only a..z A..Z 0..9 - . _  */
	private static final Pattern SPHERENAME_DISALLOWED_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9:\\.\\-_]");
	
	
	private Errors errors;
	
	@Override
	public UserRelationAjaxCommand instantiateCommand() {
		return new UserRelationAjaxCommand();
	}

	@Override
	public View workOn(final UserRelationAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}

		// check if ckey is valid
		if (!command.getContext().isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
			return this.getErrorView();
		}
		
		if (!present(command.getRequestedUserName())) {
			this.errors.rejectValue("requestedUserName", "error.field.required");
			return this.getErrorView();
		}
		
		//
		// switch between add or remove and call the right method
		//
		try {
			final String action = command.getAction();
			if (ADD_FOLLOWER.equals(action)) {
				this.addFollower(command);
			} else if (REMOVE_FOLLOWER.equals(action)) {
				this.removeFollower(command);
			} else if (ADD_FRIEND.equals(action)) {
				this.addFriend(command);
			} else if (REMOVE_FRIEND.equals(action)) {
				this.removeFriend(command);
			} else if (ADD_RELATION.equals(action)) {
				this.addRelation(command);
			} else if (REMOVE_RELATION.equals(action)) {
				this.removeRelation(command);
			}
		} catch (final ValidationException e) {
			log.info("Error establishing a connection for '" + context.getLoginUser().getName() + "' to user '" + command.getRequestedUserName()+"': " + e.getMessage());
			this.errors.reject(e.getMessage());
		}
		
		// return error messages in case of errors
		if (this.errors.hasErrors()) {
			return this.getErrorView();
		}
		
		// forward to a certain page, if requested 
		final String forward = command.getForward();
		if (present(forward)) {
			return new ExtendedRedirectView("/" + forward);
		}
		
		// all done
		return Views.AJAX_JSON;
	}
	

	/**
	 * use this method to add a user to the followers table
	 * 
	 * @param command
	 */
	private void addFollower(final UserRelationAjaxCommand command){
		final User user = new User(command.getRequestedUserName());
		this.logic.createUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.FOLLOWER_OF, null);
	}
	
	/**
	 * use this method to remove someone from the followers table
	 * 
	 * @param command
	 */
	private void removeFollower(final UserRelationAjaxCommand command){
		final User user = new User(command.getRequestedUserName());
		this.logic.deleteUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.FOLLOWER_OF, null);
	}

	/**
	 * use this method to add a given user relation
	 * 
	 * @param command
	 */
	private void addRelation(final UserRelationAjaxCommand command) {
		final String requestedRelation;
		final List<String> relationTags = command.getRelationTags();
		// TODO: create a validator for sphere name validation
		if (UserRelation.OF_FRIEND.equals(command.getUserRelation())) {
			if (!present(relationTags) || (relationTags.size() > 1)) {
				this.errors.reject("error.field.valid.sphere.name");
				return;
			}
			requestedRelation = relationTags.get(0);
			if (!present(requestedRelation) || (requestedRelation.length() > SPHERENAME_MAX_LENGTH) || SPHERENAME_DISALLOWED_CHARACTERS_PATTERN.matcher(requestedRelation).find()) {
				this.errors.rejectValue("relationTags","error.field.valid.sphere.name");
				return;
			}
		} else {
			requestedRelation = null;
		}
		
		final User user = new User(command.getRequestedUserName());
		this.logic.createUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), command.getUserRelation(), requestedRelation);
	}
	
	/**
	 * use this method to remove a given user relation
	 * 
	 * @param command
	 */
	private void removeRelation(final UserRelationAjaxCommand command) {
		if (!present(command.getRelationTags()) || (command.getRelationTags().size() > 1)) {
			throw new IllegalArgumentException("Invalid number of relation names given ("+command.getRelationTags().size()+")");
		}
		final User user = new User(command.getRequestedUserName());
		final String requestedRelation = command.getRelationTags().get(0);
		
		this.logic.deleteUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, requestedRelation);
	}

	/**
	 * use this method to add a given user to the login user's friend list 
	 * 
	 * @param command
	 */
	private void addFriend(final UserRelationAjaxCommand command) {
		final User user = new User(command.getRequestedUserName());
		this.logic.createUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, null);
	}

	/**
	 * use this method to remove a given user from the login user's friend list 
	 * 
	 * @param command
	 */
	private void removeFriend(final UserRelationAjaxCommand command) {
		final User user = new User(command.getRequestedUserName());
		this.logic.deleteUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, null);
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
