/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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


import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.layout.citeproc.CSLUtils;
import org.bibsonomy.layout.citeproc.renderer.AdhocRenderer;
import org.bibsonomy.layout.citeproc.renderer.LanguageFile;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.model.*;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPublicationCommand;
import org.bibsonomy.webapp.controller.PersonPageController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Simple AJAX controller that is used to request publications (for auto-loading) for person pages.
 * @author mho
 */
public class PersonPublicationAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPublicationCommand> {

	private LogicInterface adminLogic;

	private AdhocRenderer renderer;
	private CSLFilesManager cslFilesManager;
	private URLGenerator urlGenerator;

	@Override
	public AjaxPersonPublicationCommand instantiateCommand() {
		return new AjaxPersonPublicationCommand();
	}

	@Override
	public View workOn(final AjaxPersonPublicationCommand command) {
		final String requestedPersonId = command.getRequestedPersonId();

		final Person person = this.getPersonById(requestedPersonId);
		if (!present(person)) {
			return Views.AJAX_ERRORS;
		}

		final int postsPerPage = command.getSize();
		final int start = postsPerPage * command.getPage();
		PersonPageController.fillCommandWithPersonResourceRelations(this.logic, command, person, start, postsPerPage);


		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/// TODO: this block is currently copy/paste from PersonPageController - rework whole feature

		// Get the linked user's person posts style settings
		final User user = adminLogic.getUserDetails(person.getUser());
		final PersonPostsStyle personPostsStyle = user.getSettings().getPersonPostsStyle();
		final String personPostsLayout = user.getSettings().getPersonPostsLayout();
		command.setPersonPostsLayout(personPostsLayout);
		command.setPersonPostsStyle(personPostsStyle);

		if (!present(personPostsLayout) || personPostsLayout.startsWith("DEFAULT")) {
			return Views.AJAX_PERSON_PUBLICATIONS;
		}

		Map<String, String> renderedPosts;

		// convert posts
		List<Post<? extends BibTex>> postsToConvert = new ArrayList<>();
		if (personPostsStyle.equals(PersonPostsStyle.MYOWN)) {
			postsToConvert.addAll(command.getMyownPosts());
		} else {
			postsToConvert.addAll(command.getOtherPubs().stream()
					.map(ResourcePersonRelation::getPost)
					.collect(Collectors.toList()));
		}

		// the prefix is set in settings->person
		String prefix = "CUSTOM/";
		CSLStyle cslStyle;

		if (personPostsLayout.startsWith("CUSTOM/")) {
			cslStyle = cslFilesManager.getStyleByName(personPostsLayout.substring(prefix.length()));
		} else {
			cslStyle = cslFilesManager.getStyleByName(personPostsLayout);
		}

		if (!present(cslStyle)) {
			// TODO DEFAULT STYLE?? - or just keep old way of displying?
			throw new RuntimeException("FU");
		}

		final String userDefaultLanguage = user.getSettings().getDefaultLanguage();
		LanguageFile localeProvider = new LanguageFile();
		switch (userDefaultLanguage) {
			case "de":
				localeProvider.setLocale(cslFilesManager.getLocaleFile("de-DE"));
				break;
			case "en":
			default:
				localeProvider.setLocale(cslFilesManager.getLocaleFile("en-US"));
				break;
		}

		try {
			renderedPosts = renderer.renderPosts(postsToConvert , cslStyle.getContent(), localeProvider, true);
		} catch (Exception e) {
			e.printStackTrace();
			renderedPosts = new HashMap<>();
		}

		command.setRenderedPosts(renderedPosts);

		if (personPostsStyle.equals(PersonPostsStyle.MYOWN)) {
			Map<String, String> myOwnPostsRendered = new HashMap<>();
			for (Post<BibTex> post: command.getMyownPosts()) {
				String renderedPost = renderedPosts.get(post.getResource().getIntraHash());

				// CSL replacements
				renderedPost = CSLUtils.replacePlaceholdersFromCSLRendering(renderedPost, post, urlGenerator);

				myOwnPostsRendered.put(post.getResource().getIntraHash(), renderedPost);
			}
			command.setMyownPostsRendered(myOwnPostsRendered);
		} else {
			for (ResourcePersonRelation resourcePersonRelation: command.getOtherPubs()) {
				String renderedPost = renderedPosts.get(resourcePersonRelation.getPost().getResource().getIntraHash());

				// CSL replacements
				renderedPost = CSLUtils.replacePlaceholdersFromCSLRendering(renderedPost, resourcePersonRelation.getPost(), urlGenerator);

				resourcePersonRelation.setRenderedPost(renderedPost);
			}
		}
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////


		return Views.AJAX_PERSON_PUBLICATIONS;
	}

	private Person getPersonById(final String requestedPersonId) {
		try {
			/*
			 * get the person; if person with the requested id was merged with another person, this method
			 * throws a ObjectMovedException and the wrapper would render the redirect, that we do not want
			 */
			return this.logic.getPersonById(PersonIdType.PERSON_ID, requestedPersonId);

		} catch (final ObjectMovedException e) {
			final String newPersonId = e.getNewId();
			return this.logic.getPersonById(PersonIdType.PERSON_ID, newPersonId);
		}
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	public void setRenderer(AdhocRenderer renderer) {
		this.renderer = renderer;
	}

	public void setCslFilesManager(CSLFilesManager cslFilesManager) {
		this.cslFilesManager = cslFilesManager;
	}

	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
