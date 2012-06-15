package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxCvCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.CVWikiModel;
import org.bibsonomy.wiki.enums.DefaultLayout;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

/**
 * TODO: use json as reponse format
 * TODO: add validation? if not remove {@link ValidationAwareController} interface
 * 
 * Ajax controller for the CV page. - /ajax/cv
 * 
 * @author Bernd Terbrack
 * @version $Id$
 */
public class CvAjaxController extends AjaxController implements MinimalisticController<AjaxCvCommand>, ErrorAware, ValidationAwareController<AjaxCvCommand> {
	private static final Log log = LogFactory.getLog(CvAjaxController.class);

	private static final String SAVE_OPTION = "save";
	private static final String PUBLIC_PREVIEW = "publicPreview";
	
	
	private LogicInterface notLoggedInUserLogic;
	private Errors errors;
	private CVWikiModel wikiRenderer;
	private MessageSource messageSource;

	@Override
	public AjaxCvCommand instantiateCommand() {
		return new AjaxCvCommand();
	}

	@Override
	public View workOn(final AjaxCvCommand command) {
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

		final String renderOptions = command.getRenderOptions();
		final String authUser = this.logic.getAuthenticatedUser().getName();
		final String wikiText = command.getWikiText();
		final Group requestedGroup = this.logic.getGroupDetails(authUser);
		
		final boolean isGroup = present(requestedGroup);
		
		final LogicInterface interfaceToUse;
		if (PUBLIC_PREVIEW.equals(renderOptions)) {
			interfaceToUse = this.notLoggedInUserLogic;
		} else {
			interfaceToUse = this.logic;
		}
		
		/*
		 * handle save action
		 * first update 
		 */
		if (SAVE_OPTION.equals(renderOptions)) {
			final Wiki wiki = new Wiki();
			wiki.setWikiText(wikiText);
			/*
			 * TODO: add support for group members to edit group cv page
			 */
			this.logic.updateWiki(authUser, wiki);
			/*
			 * go on and return the preview
			 */
		}
		
		if (isGroup) {
			/*
			 * get all members of the group
			 */
			final List<User> groupUsers = interfaceToUse.getUsers(null, GroupingEntity.GROUP, requestedGroup.getName(), null, null, null, null, null, 0, 1000);
			requestedGroup.setUsers(groupUsers);

			this.wikiRenderer.setRequestedGroup(requestedGroup);
		} else {
			this.wikiRenderer.setRequestedUser(interfaceToUse.getUserDetails(authUser));
		}

		/*
		 * if a renderOption was specified and the wikitext is not null
		 * render a preview
		 */
		if (present(renderOptions) && wikiText != null) {
			return renderWiki(command, wikiText, interfaceToUse);
		}
		
		/*
		 * if no renderOption is given try to set a layout
		 */
		try {
			return getLayout(command, locale);
		} catch (final Exception e) {
			return handleError("error.405");
		}
	}
	
	private View renderWiki(final AjaxCvCommand command, final String wikiText, final LogicInterface interfaceToUse) {
		log.debug("ajax -> renderWiki");
		
		this.wikiRenderer.setLogic(interfaceToUse);
		command.setResponseString(getXmlSucceeded(command, wikiText, wikiRenderer.render(wikiText)));
		return Views.AJAX_XML;
	}

	/**
	 * 
	 * @param command
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	private View getLayout(final AjaxCvCommand command, final Locale locale) throws Exception {
		log.debug("ajax -> getLayout");
		if ( ! DefaultLayout.LAYOUT_CURRENT.equals(command.getLayout()) ) {
			// fetch default wiki text from messages.properties
			final String wikiText = messageSource.getMessage(command.getLayout().getRef(), null, locale);
			command.setResponseString(getXmlSucceeded(command, wikiText, wikiRenderer.render(wikiText)));
		} 
		else {
			// fetch current user wiki text from database
			String wikiText;
			if (present(wikiRenderer.getRequestedUser())) {
				wikiText = logic.getWiki(wikiRenderer.getRequestedUser().getName(), null).getWikiText();
			} else {
				wikiText = logic.getWiki(wikiRenderer.getRequestedGroup().getName(), null).getWikiText();
			}
			command.setResponseString(getXmlSucceeded(command, wikiText, wikiRenderer.render(wikiText)));
		}

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
	private String getXmlSucceeded(final AjaxCvCommand command, final String... wikiText) {
		return (wikiText.length > 1) ? "<root><status>ok</status><ckey>" + command.getContext().getCkey() + "</ckey><wikitext>" + Utils.escapeXmlChars(wikiText[0]) + "</wikitext><renderedwikitext><![CDATA[" + wikiText[1] + "]]></renderedwikitext></root>" : "<root><status>ok</status><ckey>" + command.getContext().getCkey() + "</ckey></root>";

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
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public boolean isValidationRequired(final AjaxCvCommand command) {
		return false;
	}

	@Override
	public Validator<AjaxCvCommand> getValidator() {
		return null;
	}

	/**
	 * @param wikiRenderer
	 *            the wikiRenderer to set
	 */
	public void setWikiRenderer(final CVWikiModel wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}
	
	/**
	 * @param notLoggedInUserLogic
	 *            the notLoggedInUserLogic to set
	 */
	public void setNotLoggedInUserLogic(final LogicInterface notLoggedInUserLogic) {
		this.notLoggedInUserLogic = notLoggedInUserLogic;
	}

}
