package org.bibsonomy.webapp.controller.events;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.events.model.Event;
import org.bibsonomy.events.services.EventManager;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.events.EventRegistrationCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.events.EventRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * Allows users to register for a conference.
 * 
 * @author rja
 * @version $Id$
 */
public class EventRegistrationController implements ErrorAware, ValidationAwareController<EventRegistrationCommand> {

	private Errors errors;
	private LogicInterface logic;
	private EventManager eventManager;
	private static final Log log = LogFactory.getLog(EventRegistrationController.class);

	@Override
	public View workOn(final EventRegistrationCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * users must be logged in to register for an event
		 */
		if (!context.isUserLoggedIn()) {
			return new ExtendedRedirectView("/login?notice=login.notice.events&referer=/events/register/" + UrlUtils.safeURIEncode(command.getEvent().getId())); 
		}
		
		/*
		 * we have a logged in user now. :-) --> get current user
		 */
		final User loginUser = context.getLoginUser();

		final Event event = eventManager.getEvent(command.getEvent().getId());
		if (!present(event)) {
			throw new MalformedURLSchemeException("The event " + command.getEvent().getId() + " does not exist."); // FIXME: own message?
		}
		
		log.info("got event " + event);
		command.setEvent(event);

		
		if (!context.isValidCkey()) {
			/*
			 * heuristic: no ckey given -> user visits the page the first time -> 
			 * put him into the command and show him the page
			 */
			command.setUser(loginUser);
			return Views.EVENT_REGISTRATION;
		}
		
		/*
		 * FIXME: field length validation (like in UpdateUserController) missing!
		 */
		if (errors.hasErrors()) {
			return Views.EVENT_REGISTRATION;
		}
		
		/*
		 * register user for the event
		 */
		if (command.getRegistered()) {
			try {
			eventManager.registerUser(loginUser, event, command.getSubEvent(), command.getAddress());
			} catch (final Exception e) {
				// FIXME: handle case of already registered user!
				errors.reject("events.error.registration", e.getMessage());
			}
		}

		/*
		 * update user's profile / settings
		 */
		updateUserProfile(loginUser, command.getUser(), command.getProfilePrivlevel());
		
		/*
		 * FIXME: redirect to success page
		 */
		return Views.EVENT_REGISTRATION_SUCCESS;
	}

	/**
	 * updates the the profile settings of a user
	 * @param user
	 * @param command
	 */
	private void updateUserProfile(final User user, final User commandUser, final String profilePrivlevel) {
		user.setRealname(commandUser.getRealname());
		user.setGender(commandUser.getGender());
		user.setBirthday(commandUser.getBirthday());
		
		user.setEmail(commandUser.getEmail());
		user.setHomepage(commandUser.getHomepage());
		user.setProfession(commandUser.getProfession());
		user.setInstitution(commandUser.getInstitution());
		user.setInterests(commandUser.getInterests());
		user.setHobbies(commandUser.getHobbies());
		user.setPlace(commandUser.getPlace());
		
		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.getProfilePrivlevel(profilePrivlevel));
		
		logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		log.debug("updated profile for user " + user.getName());
	}
	
	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	@Override
	public Validator<EventRegistrationCommand> getValidator() {
		return new EventRegistrationValidator();
	}

	@Override
	public boolean isValidationRequired(EventRegistrationCommand command) {
		return true;
	}

	@Override
	public EventRegistrationCommand instantiateCommand() {
		final EventRegistrationCommand command = new EventRegistrationCommand();
		command.setEvent(new Event());
		command.setUser(new User());
		return command;
	}

	/**
	 * @return The used logic interface.
	 */
	public LogicInterface getLogic() {
		return this.logic;
	}
 
	/**
	 * The logic interface to be used to access the data base.
	 * 
	 * @param logic
	 */
	@Required
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return The manager for the events.
	 */
	public EventManager getEventManager() {
		return this.eventManager;
	}

	/**
	 * An object providing information about available events.
	 * 
	 * @param eventManager
	 */
	@Required
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

}
