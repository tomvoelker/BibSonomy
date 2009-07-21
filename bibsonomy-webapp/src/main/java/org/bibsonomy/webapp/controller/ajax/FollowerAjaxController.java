package org.bibsonomy.webapp.controller.ajax;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.FollowerAjaxCommand;
import org.bibsonomy.webapp.controller.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * This controller catches ajax requests on the /ajax/handleFollower url
 * and handles the add and remove actions of following a user.
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class FollowerAjaxController extends AjaxController implements MinimalisticController<FollowerAjaxCommand>{
	private static final Logger log = Logger.getLogger(FollowerAjaxController.class);
	private LogicInterface logic;

	@Override
	public FollowerAjaxCommand instantiateCommand() {
		return new FollowerAjaxCommand();
	}

	@Override
	public View workOn(FollowerAjaxCommand command) {
		log.debug(this.getClass().getSimpleName());
		
		if (!command.getContext().getUserLoggedIn()){
			log.debug("someone tried to access this ajax controller manually and isn't logged in");
			return new ExtendedRedirectView("/");
		}
		
		// switch between add or remove and call the right method
		if ("addFollower".equals(command.getAction()) && command.getRequestedUserName() != null){
			this.addFollower(command);
		}		
		if ("removeFollower".equals(command.getAction()) && command.getRequestedUserName() != null){
			this.removeFollower(command);
		}
		
		return Views.AJAX;
	}
	
	/**
	 * set the logic interface
	 */
	public void setLogic(LogicInterface logic){
		this.logic = logic;
	}
	
	/**
	 * use this method to add a user to the followers table
	 * 
	 * @param command
	 */
	private void addFollower(FollowerAjaxCommand command){
		User user = new User(command.getRequestedUserName());
		logic.createUserRelationship(command.getContext().getLoginUser(),user, UserRelation.FOLLOWER_OF);
	}
	
	/**
	 * use this method to remove someone from the followers table
	 * 
	 * @param command
	 */
	private void removeFollower(FollowerAjaxCommand command){
		User user = new User(command.getRequestedUserName());
		logic.deleteUserRelationship(command.getContext().getLoginUser(),user, UserRelation.FOLLOWER_OF);
	}

}
