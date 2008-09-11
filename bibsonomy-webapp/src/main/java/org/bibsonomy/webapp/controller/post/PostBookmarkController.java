package org.bibsonomy.webapp.controller.post;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.command.actions.UserLoginCommand;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostBookmarkValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * TODO:
 * - alles verstehen
 * - Seite "posten"
 *   - wenn alle Werte vorhanden Umleitung auf Success-Seite
 *   - wenn Fehler, Post-Seite weiterhin anzeigen
 * - "Fehler" nicht beim ersten Seiten-Aufruf anzeigen
 *   - Fehler schön machen
 * - prüfen, ob Benutzer auch wirklich eingelogged ist
 * - "viewable for" mit übergeben und verarbeiten
 * - Mehrsprachigkeit (deutsch, englisch)
 * - Spezialfall, wenn Scrapbare-Seite gebookmarked werden soll
 *   - <c:when test="${not empty scraped}">
 *   - http://bibsonomy.org/scraperinfo
 *   
 *    
 * 
 * @author fba
 * @version $Id$
 */
//public class PostBookmarkController implements MinimalisticController<EditBookmarkCommand>, ErrorAware, ValidationAwareController<EditBookmarkCommand>, RequestAware, CookieAware {
public class PostBookmarkController implements MinimalisticController<EditBookmarkCommand>, ErrorAware, ValidationAwareController<EditBookmarkCommand> {
	private static final Logger log = Logger.getLogger(PostBookmarkController.class);
	protected LogicInterface logic;	
	protected UserSettings userSettings;
	private Errors errors = null;
	
	public EditBookmarkCommand instantiateCommand() {
		return new EditBookmarkCommand();
	}
	public View workOn(EditBookmarkCommand command) {
		log.debug("--> PostBookmarkController: workOn() called");
		
		command.setPageTitle("post a new bookmark");

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		
		if (context.isUserLoggedIn()) {
			
		}
		
		
		
		
		
		return Views.POST_BOOKMARK;
	}
	public Errors getErrors() {
		return errors;
	}
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}
	
	public Validator<EditBookmarkCommand> getValidator() {
		return new PostBookmarkValidator();
	}
	public boolean isValidationRequired(EditBookmarkCommand command) {
		return true;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}
}
