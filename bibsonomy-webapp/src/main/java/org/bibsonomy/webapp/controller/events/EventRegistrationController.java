package org.bibsonomy.webapp.controller.events;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.FieldLengthErrorMessage;
import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.events.model.Event;
import org.bibsonomy.events.model.ParticipantDetails;
import org.bibsonomy.events.services.EventManager;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.events.EventRegistrationCommand;
import org.bibsonomy.webapp.controller.actions.UpdateUserController;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestLogic;
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
 * @version $Id: EventRegistrationController.java,v 1.5 2010-08-26 18:17:01 mat
 *          Exp $
 */
public class EventRegistrationController implements ErrorAware, ValidationAwareController<EventRegistrationCommand> {

	private Errors errors;
	private LogicInterface logic;
	private EventManager eventManager;
	private RequestLogic requestLogic; // to extract the locale
	private static final Log log = LogFactory.getLog(EventRegistrationController.class);

	@Override
	public View workOn(final EventRegistrationCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * users must be logged in to register for an event
		 */
		if (!context.isUserLoggedIn()) {
			return new ExtendedRedirectView("/login?notice=login.notice.events&referer=" + UrlUtils.safeURIEncode("/events/" + command.getEvent().getId() + "/register" + "?lang=" + requestLogic.getLocale().getLanguage()));
		}

		/*
		 * we have a logged in user now. :-) --> get current user
		 */
		final User loginUser = context.getLoginUser();

		final Event event = eventManager.getEvent(command.getEvent().getId());
		if (!present(event)) {
			throw new MalformedURLSchemeException("The event " + command.getEvent().getId() + " does not exist."); // FIXME:
			// own
			// message?
		}

		log.info("got event " + event);
		command.setEvent(event);

		if (!context.isValidCkey()) {
			/*
			 * heuristic: no ckey given -> user visits the page the first time
			 * -> put him into the command and show him the page
			 */
			command.setUser(loginUser);
			command.getParticipantDetails().setEmail(loginUser.getEmail());
			return Views.EVENT_REGISTRATION;
		}

		/*
		 * FIXME: field length validation (like in UpdateUserController)
		 * missing!
		 */
		if (errors.hasErrors()) {
			return Views.EVENT_REGISTRATION;
		}

		/*
		 * register user for the event
		 */
		try {
			eventManager.registerUser(loginUser, event, command.getParticipantDetails());
		} catch (final Exception e) {
			// FIXME: handle case of already registered user!
			errors.reject("events.error.registration", e.getMessage());
		}

		/*
		 * update user's profile / settings
		 */
		updateUserProfile(loginUser, command.getUser(), command.getProfilePrivlevel());

		if (errors.hasErrors()) {
			return Views.EVENT_REGISTRATION;
		}
		
		/*
		 * FIXME: redirect to success page
		 */
		return Views.EVENT_REGISTRATION_SUCCESS;
	}

	/**
	 * updates the the profile settings of a user
	 * 
	 * @param user
	 * @param command
	 */
	private void updateUserProfile(final User user, final User commandUser, final String profilePrivlevel) {
		user.setRealname(commandUser.getRealname());
		user.setGender(commandUser.getGender());
		user.setBirthday(commandUser.getBirthday());
		user.setHomepage(commandUser.getHomepage());
		user.setProfession(commandUser.getProfession());
		/*
		 * FIXME: why do we check presence here? Why not for all attributes?
		 */
		if (present(commandUser.getInstitution())) {
			user.setInstitution(commandUser.getInstitution());
		}
		user.setInterests(commandUser.getInterests());
		user.setHobbies(commandUser.getHobbies());
		user.setPlace(commandUser.getPlace());

		user.getSettings().setProfilePrivlevel(ProfilePrivlevel.getProfilePrivlevel(profilePrivlevel));

		updateUser(user, errors);
		log.debug("updated profile for user " + user.getName());
	}
	
	/**
	 * Updates the user (including field length error checking!).
	 *
	 * 
	 * FIXME: duplicated code from {@link UpdateUserController}.
	 * 
	 * @param user
	 */
	private void updateUser(final User user, final Errors errors) {
		try {
			logic.updateUser(user, UserUpdateOperation.UPDATE_CORE);
		} catch(DatabaseException e) {
			final List<ErrorMessage> messages = e.getErrorMessages().get(user.getName());

			for(final ErrorMessage eMsg : messages) {
				if(eMsg instanceof FieldLengthErrorMessage) {
					final FieldLengthErrorMessage fError = (FieldLengthErrorMessage) eMsg;
					final Iterator<String> it = fError.iteratorFields();
					while(it.hasNext()) {
						final String current = it.next();
						final String[] values = { String.valueOf(fError.getMaxLengthForField(current)) };
						errors.rejectValue("user." + current, "error.field.valid.limit_exceeded", values, fError.getDefaultMessage());
					}
				}
			}
		}
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
		command.setParticipantDetails(new ParticipantDetails());
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

	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

}
