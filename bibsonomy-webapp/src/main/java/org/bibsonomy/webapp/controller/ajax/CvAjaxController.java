package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.webapp.command.ajax.AjaxCvCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

/**
 * @author Bernd
 * @version $Id$
 */
public class CvAjaxController extends AjaxController implements MinimalisticController<AjaxCvCommand>, ErrorAware, ValidationAwareController<AjaxCvCommand> {

	private static final Log log = LogFactory.getLog(CvAjaxController.class);
	private Errors errors;
	
	private MessageSource messageSource;
	
	@Override
	public AjaxCvCommand instantiateCommand() {
		return new AjaxCvCommand();
	}
	
	@Override
	public View workOn(AjaxCvCommand command) {
		log.debug("workOn CvAjaxController");
		final Locale locale = requestLogic.getLocale();

		// -- Validating the request --
		/*
		 * Check whether user is logged in
		 */
		if (!command.getContext().isUserLoggedIn()) {
			return handleError("error.general.login");
		}

		/*
		 * check if the ckey is valid
		 */
		if (!command.getContext().isValidCkey()) {
			return handleError("error.field.valid.ckey");
		}
		
		final String layout = command.getLayout();
		if(present(layout)) {
			if("default".equals(layout)) {
				return defaultCV(command,locale);
			} else if("clear".equals(layout)) {
				return clearCV(command);
			}
		}
		return handleError("error.405");
	}
	
	private View clearCV(AjaxCvCommand command) {
		log.debug("ajax -> clearCV");
		try{
			Wiki wiki = new Wiki();
			wiki.setWikiText("");
			logic.updateWiki(logic.getAuthenticatedUser().getName(), wiki);
			command.setResponseString(getXmlSucceeded(command,wiki.getWikiText()));
			return Views.AJAX_XML;
		}catch(Exception e) {
			return handleError("database.exception.unspecified");
		}
	}
	
	private View defaultCV(AjaxCvCommand command, Locale locale) {
		log.debug("ajax -> defaultCV");
		try{
			Wiki wiki = new Wiki();
			wiki.setWikiText(messageSource.getMessage("cv.default_wiki",null, locale));
			logic.updateWiki(logic.getAuthenticatedUser().getName(), wiki);
			command.setResponseString(getXmlSucceeded(command,wiki.getWikiText()));
			return Views.AJAX_XML;
		}catch(Exception e) {
			return handleError("database.exception.unspecified");
		}
	}
	
	// TODO: The two functions below are (almost) equal to the functions in "AdditionalURLController" -> redudant code
	/**
	 * Method to handle Errors based on urlError enum.
	 * 
	 * @return Error View
	 */
	private View handleError(final String messageKey) {
		log.debug("An error occured: " + messageKey);
		errors.reject(messageKey);
		return Views.AJAX_ERRORS;
	}
	/**
	 * Method which generates a XML response string [success]
	 * 
	 * @return XML success string.
	 */
	private String getXmlSucceeded(final AjaxCvCommand command,String wikiText) {
		return "<root><status>ok</status><ckey>" + command.getContext().getCkey() + "</ckey><wikitext> " +Utils.escapeXmlChars(wikiText) +"</wikitext></root>";
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
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public boolean isValidationRequired(AjaxCvCommand command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Validator<AjaxCvCommand> getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
