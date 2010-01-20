package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.FOAFCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for the FOAF-rdf output
 * For more information please visit the <a href="http://www.foaf-project.org/">FOAF project page</a>
 * 
 * @author dzo
 * @version $Id$
 */
public class FOAFController implements MinimalisticController<FOAFCommand> {
	private LogicInterface logic;
	
	@Override
	public FOAFCommand instantiateCommand() {
		return new FOAFCommand();
	}

	@Override
	public View workOn(final FOAFCommand command) {		
		if (!command.getContext().isUserLoggedIn()) {
			return new ExtendedRedirectView("/login");
		}
		
		final String requestedUser = command.getRequestedUser();
		
		if (!present(requestedUser)) {
			throw new MalformedURLSchemeException("error.foaf_output_without_username");
		}
		
		/*
		 * get informations from logic
		 */
		final User user = this.logic.getUserDetails(requestedUser);
		
		/*
		 * get friends
		 */
		try {
			final List<User> friends = this.logic.getFriendsOfUser(user);
			user.addFriends(friends);
		} catch (final ValidationException ex) {
			// ignore it
		}
		
		/*
		 *  prepare mail address / encode it using sha-1
		 */
		final String mail = user.getEmail();
		if (present(mail)) {
			final String toEncode = "mailto:" + mail;
			user.setEmail(StringUtils.getSHA1Hash(toEncode));
		}		
		
		command.setUser(user);
		
		return Views.FOAF;
	}

	/**
	 * @param logic the adminLogic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
