package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
import org.bibsonomy.wiki.WikiUtil;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

/**
 * 
 * Ajax controller for the CV page.
 * - /ajax/cv
 * 
 * @author Bernd
 * @version $Id$
 */
public class CvAjaxController extends AjaxController implements MinimalisticController<AjaxCvCommand>, ErrorAware, ValidationAwareController<AjaxCvCommand> {

	private static final Map<String,String> layouts = new HashMap<String,String>();
	private static final Log log = LogFactory.getLog(CvAjaxController.class);
	private Errors errors;
	private WikiUtil wikiRenderer;
	private MessageSource messageSource;
	
	static {
		layouts.put("table", "cv.layout.table");
		layouts.put("robert", "cv.layout.robert");
	}

	@Override
	public AjaxCvCommand instantiateCommand() {
		return new AjaxCvCommand();
	}

	@Override
	public View workOn(AjaxCvCommand command) {
		log.debug("workOn CvAjaxController");
		final Locale locale = requestLogic.getLocale();
		this.wikiRenderer.setUser(logic.getAuthenticatedUser());
		
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
		final String isSave = command.getIsSave();
		final String wikiText = command.getWikiText();
		
		//FIXME: Handle empty wiki text
		if (present(isSave) && present(wikiText)) {
			return renderWiki(command,wikiText,isSave);
		}
		
		//TODO: Current layout
		if (layouts.containsKey(layout)) {
			return getLayout(command, locale);
		}
		return handleError("error.405");
	}

	private View renderWiki(AjaxCvCommand command, String wikiText, String isSave) {
		log.debug("ajax -> renderWiki");
		
		Wiki wiki = new Wiki();
		wiki.setWikiText(wikiText);
		if("true".equals(isSave)) {
			logic.updateWiki(logic.getAuthenticatedUser().getName(), wiki);
		}
		command.setResponseString(getXmlSucceeded(command, wikiText, wikiRenderer.render(wikiText)));
		return Views.AJAX_XML;
	}

	private View getLayout(AjaxCvCommand command, Locale locale) {
		log.debug("ajax -> getLayout");
		String wikiText = messageSource.getMessage(layouts.get(command.getLayout()), null, locale);
		command.setResponseString(getXmlSucceeded(command, wikiText, wikiRenderer.render(wikiText)));

		return Views.AJAX_XML;
	}

	// TODO: The two functions below are (almost) equal to the functions in
	// "AdditionalURLController" -> redudant code
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
	private String getXmlSucceeded(final AjaxCvCommand command, String... wikiText) {
		if (wikiText.length > 1) {
			return "<root><status>ok</status><ckey>" + command.getContext().getCkey() + "</ckey><wikitext> " + Utils.escapeXmlChars(wikiText[0]) + "</wikitext><renderedwikitext><![CDATA[ " + wikiText[1] + "]]></renderedwikitext></root>";
		} else if (wikiText.length == 1) {
			return "<root><status>ok</status><ckey>" + command.getContext().getCkey() + "</ckey><wikitext> " + Utils.escapeXmlChars(wikiText[0]) + "</wikitext></root>";
		} else {
			return "<root><status>ok</status><ckey>" + command.getContext().getCkey() + "</ckey></root>";
		}
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
	 * @param messageSource
	 *            the messageSource to set
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

	/**
	 * @return the wikiRenderer
	 */
	public WikiUtil getWikiRenderer() {
		return wikiRenderer;
	}

	/**
	 * @param wikiRenderer
	 *            the wikiRenderer to set
	 */
	public void setWikiRenderer(WikiUtil wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}

}
