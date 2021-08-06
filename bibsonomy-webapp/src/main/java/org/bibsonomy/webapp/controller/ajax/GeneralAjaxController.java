/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.BibTexKeySystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.util.XmlUtils;
import org.bibsonomy.webapp.command.ajax.GeneralAjaxCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Returns information about the given URL.
 * 
 * @author fba
 */
public class GeneralAjaxController extends AjaxController implements MinimalisticController<GeneralAjaxCommand> {

	@Override
	public View workOn(GeneralAjaxCommand command) {
		final String action = command.getAction();

		if ("getTitleForUrl".equals(action)) {
			getDetailsForUrl(command);
			return Views.AJAX_GET_TITLE_FOR_URL;
		} else if ("getBibtexKeysForUser".equals(action)) {
			this.getBibtexKeysForUser(command);
			return Views.AJAX_GET_BIBTEXKEYS_FOR_USER;
		}
		return Views.AJAX_TEXT;
	}

	
	/**
	 * Retrieve bibtex keys of a given user
	 * 
	 * @param command - the command bean for this controller
	 */
	private void getBibtexKeysForUser(GeneralAjaxCommand command) {
		/*
		 * check input params, make sure user is logged in
		 */
		if (!present(command.getQ()) || !command.getContext().isUserLoggedIn() || !present(command.getRequestedUser())) {
			return;
		}
		/*
		 * append percent sign to bibkey for fuzzy matching
		 * TODO: % is mysql specific and should be moved to the logic
		 */
		final String requestedBibTexKey = command.getQ().trim() + "%";
		final String requestedUserName   = command.getRequestedUser();
		/*
		 * fetch posts
		 */		
		final List<String> tags = Collections.singletonList(SystemTagsUtil.buildSystemTagString(BibTexKeySystemTag.NAME, requestedBibTexKey));

		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.USER)
				.setGroupingName(requestedUserName)
				.setTags(tags)
				.setScope(QueryScope.LOCAL)
				.entriesStartingAt(20, 0);
		command.setBibtexPosts(this.logic.getPosts(postQueryBuilder.createPostQuery(BibTex.class)));
	}
	
	/**
	 * Retrieve details for a given URL
	 * 
	 * @param command - the command containing the page URL
	 */
	private static void getDetailsForUrl(final GeneralAjaxCommand command) {
		final String pageURL = command.getPageURL();
		
		if (!present(pageURL)) {
			return;
		}

		try {
			final Document document = XmlUtils.getDOM(new URL(pageURL));
			final NodeList title = document.getElementsByTagName("title");
			command.setPageTitle(title.item(0).getChildNodes().item(0).getNodeValue());

			final NodeList metaList = document.getElementsByTagName("meta");
			for (int i = 0; i < metaList.getLength(); i++) {
				final Element metaElement = (Element) metaList.item(i);

				final Attr nameAttr = metaElement.getAttributeNode("name");
				if (nameAttr == null) continue; 

				if (nameAttr.getNodeValue().equalsIgnoreCase("description")) {
					command.setPageDescription(metaElement.getAttribute("content"));
				}
				if (nameAttr.getNodeValue().equalsIgnoreCase("keywords")) {
					command.setPageKeywords(metaElement.getAttribute("content"));
				}
			}

		} catch (final Exception ex) {
			// ignore exceptions silently
		}
	}

	@Override
	public GeneralAjaxCommand instantiateCommand() {
		return new GeneralAjaxCommand();
	}

}
