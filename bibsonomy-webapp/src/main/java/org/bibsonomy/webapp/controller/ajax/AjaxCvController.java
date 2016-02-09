/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Wiki;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxCvCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.bibsonomy.wiki.CVWikiModel;
import org.bibsonomy.wiki.TemplateManager;
import org.bibsonomy.wiki.enums.DefaultLayout;
import org.springframework.validation.Errors;

/**
 * TODO: use json as reponse format
 * 
 * Ajax controller for the CV page. - /ajax/cv
 * 
 * @author Bernd Terbrack
 */
public class AjaxCvController extends AjaxController implements MinimalisticController<AjaxCvCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(AjaxCvController.class);

	private static final String SAVE_OPTION = "save";
	private static final String PUBLIC_PREVIEW = "publicPreview";
	
	
	private LogicInterface notLoggedInUserLogic;
	private Errors errors;
	private CVWikiModel wikiRenderer;

	@Override
	public AjaxCvCommand instantiateCommand() {
		return new AjaxCvCommand();
	}

	@Override
	public View workOn(final AjaxCvCommand command) {
		log.debug("workOn AjaxCvController");

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
		final Group requestedGroup = getRequestedGroup(command.getContext().getLoginUser().getGroups(), command.getRequestedGroup());
		
		final LogicInterface interfaceToUse;
		
		/* if we chose to render the layout as it is publicly viewable
		 * (i.e. also for users who are not logged in), do this.
		 */
		if (PUBLIC_PREVIEW.equals(renderOptions)) {
			interfaceToUse = this.notLoggedInUserLogic;
		} else {
			interfaceToUse = this.logic;
		}
		
		/*
		 * Determine if the requested page is a group cv or a user cv
		 */
		if (present(requestedGroup)) {
			this.wikiRenderer.setRequestedGroup(requestedGroup);
		} else {
			this.wikiRenderer.setRequestedUser(interfaceToUse.getUserDetails(authUser));
		}

		/*
		 * If the renderOption was set to "SAVE_OPTION", we just write the whole thing into the database.
		 */
		if (SAVE_OPTION.equals(renderOptions)) {
			if (command.getLayout() == null && (!present(wikiText))) {
				return handleError("cv.error.edit.noEmptyCV");
			}

			final Wiki wiki = new Wiki();
			wiki.setWikiText(wikiText);
			
			if (present(requestedGroup)) {
				// TODO: why do we need the group details here? TODO_GROUPS
				final Group g = this.logic.getGroupDetails(command.getRequestedGroup());
				this.logic.updateWiki(g.getName(), wiki);
			} else {
				this.logic.updateWiki(authUser, wiki);
			}
		}
		
		/*
		 * Return the rendered wiki.
		 */
		try {
			return renderLayout(command, wikiText, interfaceToUse);
		} catch (final Exception e) {
			return handleError("error.405");
		}
	}
	
	/**
	 * renders a layout and returns it.
	 * @param command the Ajax command for the CV page.
	 * @param locale some locale necessary for rendering
	 * @return some view. Actually it is more important that the ajax response string contains
	 * the rendered layout.
	 */
	private View renderLayout(final AjaxCvCommand command, final String wikiText, final LogicInterface interfaceToUse) {
		log.debug("ajax -> getLayout");
		
		this.wikiRenderer.setLogic(interfaceToUse);
		// if asked for a default layout, fetch it from the messages.
		String currentWikiText = "";
		if (command.getLayout() == null) {
			// render the custom layout.
			currentWikiText = wikiText;
		} else {
			// Layout was requested. Current or default layout?
			if (DefaultLayout.LAYOUT_CURRENT.equals(command.getLayout()) ) {
				if (present(wikiRenderer.getRequestedUser())) {
					currentWikiText = logic.getWiki(wikiRenderer.getRequestedUser().getName(), null).getWikiText();
				} else {
					currentWikiText = logic.getWiki(wikiRenderer.getRequestedGroup().getName(), null).getWikiText();
				}
			} else {
				currentWikiText = TemplateManager.getTemplate(command.getLayout().getRef());
			}
		}
		// TODO: render json output
		command.setResponseString(generateXMLSuccessString(command, currentWikiText, wikiRenderer.render(currentWikiText)));

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
	private String generateXMLSuccessString(final AjaxCvCommand command, final String wikiText, final String renderedWikiText) {
		return "<root><status>ok</status><ckey>"
				+ command.getContext().getCkey() + "</ckey><wikitext>"
				+ StringEscapeUtils.escapeXml(wikiText)
				+ "</wikitext><renderedwikitext><![CDATA[" + renderedWikiText
				+ "]]></renderedwikitext></root>";
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

	private Group getRequestedGroup(List<Group> groups, String requestedGroup) {
		if (present(requestedGroup)) {
			for (Group g : groups) {
				if (g.getName().equals(requestedGroup)) {
					return this.logic.getGroupDetails(g.getName());
				}
			}
		}
		return null;
	}
}
