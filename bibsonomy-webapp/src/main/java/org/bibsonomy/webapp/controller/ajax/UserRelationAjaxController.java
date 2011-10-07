package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ajax.UserRelationAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
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
	private static final String ADD_FOLLOWER = "addFollower";

	private static final String REMOVE_RELATION = "removeRelation";

	private static final String ADD_RELATION = "addRelation";

	private static final String REMOVE_FRIEND = "removeFriend";

	private static final String ADD_FRIEND = "addFriend";

	private static final String REMOVE_FOLLOWER = "removeFollower";

	private static final Log log = LogFactory.getLog(UserRelationAjaxController.class);
	
	private Errors errors;

	@Override
	public UserRelationAjaxCommand instantiateCommand() {
		return new UserRelationAjaxCommand();
	}

	@Override
	public View workOn(UserRelationAjaxCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		if (!command.getContext().getUserLoggedIn()){
			log.debug("someone tried to access this ajax controller manually and isn't logged in");
			return new ExtendedRedirectView("/");
		}
		
		log.debug("AJAX controller; userName: " + command.getRequestedUserName() + ", action: " + command.getAction() + ", ckey: " + command.getContext().getCkey() + ", forward: " + command.getForward());
		
		// check if ckey is valid
		if (!command.getContext().isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		//
		// switch between add or remove and call the right method
		//
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
		
		// forward to a certain page, if requested 
		if (present(command.getForward())) {
			// TODO: remove?!?
//			if (EnumUtils.searchEnumByName(Views.values(), command.getForward()) == null) {
//				errors.reject("error.invalid_forward_page");
//				return Views.ERROR;
//			}
			return new ExtendedRedirectView("/" + command.getForward());
		}
		
		return Views.AJAX_TEXT;
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
			throw new IllegalArgumentException("Invalid number of relation names given ("+command.getRelationTags().size()+")");
		}
		User user = new User(command.getRequestedUserName());
		String requestedRelation = command.getRelationTags().get(0);
		
		logic.createUserRelationship(command.getContext().getLoginUser().getName(),user.getName(), UserRelation.OF_FRIEND, requestedRelation);
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
