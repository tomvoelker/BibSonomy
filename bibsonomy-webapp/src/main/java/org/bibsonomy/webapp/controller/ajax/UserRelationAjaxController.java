package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
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
 * @version $Id$
 */
public class UserRelationAjaxController extends AjaxController implements MinimalisticController<UserRelationAjaxCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(UserRelationAjaxController.class);
	
	private static final String ADD_FOLLOWER = "addFollower";

	private static final String REMOVE_RELATION = "removeRelation";

	private static final String ADD_RELATION = "addRelation";

	private static final String REMOVE_FRIEND = "removeFriend";

	private static final String ADD_FRIEND = "addFriend";

	private static final String REMOVE_FOLLOWER = "removeFollower";

	private Errors errors;
	
	private static final int SPHERENAME_MAX_LENGTH = 64;
	
	/**
	 * We allow only a..z A..Z 0..9 - . _ 
	 */
	private static final Pattern SPHERENAME_DISALLOWED_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9:\\.\\-_]");
	
	@Override
	public UserRelationAjaxCommand instantiateCommand() {
		return new UserRelationAjaxCommand();
	}

	@Override
	public View workOn(UserRelationAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}

		// check if ckey is valid
		if (!command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			returnErrorView();
		}
		
		//
		// switch between add or remove and call the right method
		//
		try {
			if (ADD_FOLLOWER.equals(command.getAction()) && command.getRequestedUserName() != null) {
				this.addFollower(command);
			} else if (REMOVE_FOLLOWER.equals(command.getAction()) && command.getRequestedUserName() != null) {
				this.removeFollower(command);
			} else if (ADD_FRIEND.equals(command.getAction()) && command.getRequestedUserName() != null) {
				this.addFriend(command);
			} else if (REMOVE_FRIEND.equals(command.getAction()) && command.getRequestedUserName() != null) {
				this.removeFriend(command);
			} else if (ADD_RELATION.equals(command.getAction()) && command.getRequestedUserName() != null) {
				this.addRelation(command);
			} else if (REMOVE_RELATION.equals(command.getAction()) && command.getRequestedUserName() != null) {
				this.removeRelation(command);
			}
		} catch (org.bibsonomy.common.exceptions.ValidationException e) {
			log.error("Error establishing a connection for '"+command.getContext().getLoginUser().getName()+"' to user '"+command.getRequestedUserName()+"': " + e.getMessage());
			this.errors.reject("error.user.relation.update");
		}
		
		// return error messages in case of errors
		if (errors.hasErrors()) {
			return returnErrorView();
		}
		
		// forward to a certain page, if requested 
		if (present(command.getForward())) {
			return new ExtendedRedirectView("/" + command.getForward());
		}
		
		// all done
		return Views.AJAX_JSON;
	}
	

	/**
	 * use this method to add a user to the followers table
	 * 
	 * @param command
	 */
	private void addFollower(UserRelationAjaxCommand command){
		User user = new User(command.getRequestedUserName());
		logic.createUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.FOLLOWER_OF, null);
	}
	
	/**
	 * use this method to remove someone from the followers table
	 * 
	 * @param command
	 */
	private void removeFollower(UserRelationAjaxCommand command){
		User user = new User(command.getRequestedUserName());
		logic.deleteUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.FOLLOWER_OF, null);
	}

	/**
	 * use this method to add a given user relation
	 * 
	 * @param command
	 */
	private void addRelation(UserRelationAjaxCommand command) {
		if (!present(command.getRelationTags()) || command.getRelationTags().size()>1) {
			errors.reject("error.field.valid.sphere.name");
			return;
		}
		
		User user = new User(command.getRequestedUserName());
		String requestedRelation = command.getRelationTags().get(0);
		
		// TODO: create a validator for sphere name validation
		if ( !present(requestedRelation) ||
				requestedRelation.length() > SPHERENAME_MAX_LENGTH ||
				SPHERENAME_DISALLOWED_CHARACTERS_PATTERN.matcher(requestedRelation).find())
		{
			errors.rejectValue("relationTags","error.field.valid.sphere.name");
		} else {
			logic.createUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, requestedRelation);
		}
	}
	
	/**
	 * use this method to remove a given user relation
	 * 
	 * @param command
	 */
	private void removeRelation(UserRelationAjaxCommand command) {
		if (!present(command.getRelationTags()) || command.getRelationTags().size()>1) {
			throw new IllegalArgumentException("Invalid number of relation names given ("+command.getRelationTags().size()+")");
		}
		User user = new User(command.getRequestedUserName());
		String requestedRelation = command.getRelationTags().get(0);
		
		logic.deleteUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, requestedRelation);
	}

	/**
	 * use this method to add a given user to the login user's friend list 
	 * 
	 * @param command
	 */
	private void addFriend(UserRelationAjaxCommand command) {
		User user = new User(command.getRequestedUserName());
		logic.createUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, null);
	}

	/**
	 * use this method to remove a given user from the login user's friend list 
	 * 
	 * @param command
	 */
	private void removeFriend(UserRelationAjaxCommand command) {
		User user = new User(command.getRequestedUserName());
		logic.deleteUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, null);
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
