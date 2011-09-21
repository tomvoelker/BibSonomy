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
import org.bibsonomy.wiki.enums.GroupLayout;
import org.bibsonomy.wiki.enums.UserLayout;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

/**
 * Ajax controller for the CV page. - /ajax/cv
 * 
 * @author Bernd Terbrack
 * @version $Id$
 */
public class CvAjaxController extends AjaxController implements MinimalisticController<AjaxCvCommand>, ErrorAware, ValidationAwareController<AjaxCvCommand> {

	private static final Log log = LogFactory.getLog(CvAjaxController.class);
	private LogicInterface notLoggedInUserLogic;
	private Errors errors;
	private CVWikiModel wikiRenderer;
	private MessageSource messageSource;
	private static final String PUBLIC_PREVIEW = "publicPreview";

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

		final String renderOptions = command.getRenderOptions();
		final String authUser = logic.getAuthenticatedUser().getName();
		final String wikiText = command.getWikiText();
		final Group requestedGroup;

		requestedGroup = this.logic.getGroupDetails(authUser);
		boolean isGroup = present(requestedGroup);
		if (isGroup) {
			final GroupingEntity groupingEntity = GroupingEntity.GROUP;

			/*
			 * Check if its a public preview. If so, use the public preview
			 * logic.
			 */
			final List<User> groupUsers;
			if (PUBLIC_PREVIEW.equals(renderOptions)) {
				groupUsers = this.notLoggedInUserLogic.getUsers(null, groupingEntity, requestedGroup.getName(), null, null, null, null, null, 0, 1000);
			} else {
				groupUsers = this.logic.getUsers(null, groupingEntity, requestedGroup.getName(), null, null, null, null, null, 0, 1000);
			}
			requestedGroup.setUsers(groupUsers);

			this.wikiRenderer.setRequestedGroup(requestedGroup);
		} else {
			/*
			 * Check if its a public preview. If so, use the public preview
			 * logic.
			 */
			if (PUBLIC_PREVIEW.equals(renderOptions)) {
				this.wikiRenderer.setRequestedUser(this.notLoggedInUserLogic.getUserDetails(authUser));
			} else {
				this.wikiRenderer.setRequestedUser(this.logic.getUserDetails(authUser));
			}
		}

		if (present(renderOptions) && wikiText != null) {
			return renderWiki(command, wikiText, renderOptions);
		}
		try {
			return getLayout(command, locale, isGroup);
		} catch (Exception e) {
			return handleError("error.405");
		}
	}

	/**
	 * Method which renders the wikiText
	 * @param command
	 * @param wikiText
	 * @param renderOption
	 * @return the rendered wiki view
	 */
	private View renderWiki(AjaxCvCommand command, String wikiText, String renderOption) {
		log.debug("ajax -> renderWiki");

		Wiki wiki = new Wiki();
		wiki.setWikiText(wikiText);
		if ("publicPreview".equals(renderOption)) {
			wikiRenderer.setLogic(notLoggedInUserLogic);
		} else if ("save".equals(renderOption)) {
			logic.updateWiki((wikiRenderer.getRequestedGroup() != null ? wikiRenderer.getRequestedGroup().getName() : wikiRenderer.getRequestedUser().getName()), wiki);
		}
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
	private View getLayout(AjaxCvCommand command, Locale locale, boolean isGroup) throws Exception {
		log.debug("ajax -> getLayout");
		final String layoutName = command.getLayout();
		if (!UserLayout.LAYOUT_CURRENT.name().equals(layoutName)) {
			String layoutRef = isGroup ?  GroupLayout.valueOf(layoutName).getRef() : UserLayout.valueOf(layoutName).getRef();
			String wikiText = messageSource.getMessage(layoutRef, null, locale);
			command.setResponseString(getXmlSucceeded(command, wikiText, wikiRenderer.render(wikiText)));
		} else {
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
	private String getXmlSucceeded(final AjaxCvCommand command, String... wikiText) {
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
	public CVWikiModel getWikiRenderer() {
		return wikiRenderer;
	}

	/**
	 * @param wikiRenderer
	 *            the wikiRenderer to set
	 */
	public void setWikiRenderer(CVWikiModel wikiRenderer) {
		this.wikiRenderer = wikiRenderer;
	}

	/**
	 * @return the notLoggedInUserLogic
	 */
	public LogicInterface getNotLoggedInUserLogic() {
		return notLoggedInUserLogic;
	}

	/**
	 * @param notLoggedInUserLogic
	 *            the notLoggedInUserLogic to set
	 */
	public void setNotLoggedInUserLogic(final LogicInterface notLoggedInUserLogic) {
		this.notLoggedInUserLogic = notLoggedInUserLogic;
	}

}
