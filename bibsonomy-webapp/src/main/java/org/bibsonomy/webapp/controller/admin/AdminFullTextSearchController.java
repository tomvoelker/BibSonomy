/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.management.ElasticsearchManager;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.webapp.command.admin.AdminFullTextSearchCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * Controller for the full text search admin page
 * 
 * @author Sven Stefani
 * @author jensi
 * @author dzo
 */
public class AdminFullTextSearchController implements MinimalisticController<AdminFullTextSearchCommand> {
	private static final Log log = LogFactory.getLog(AdminFullTextSearchController.class);
	
	private static final String GENERATE_INDEX = "generateIndex";
	
	private Map<Class<? extends Resource>, ElasticsearchManager<? extends Resource>> managers;
	
	@Override
	public View workOn(final AdminFullTextSearchCommand command) {
		log.debug(this.getClass().getSimpleName());

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* 
		 * check user role
		 * If user is not logged in or not an admin: show error message
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}
		
		if (GENERATE_INDEX.equals(command.getAction())) {
			final ElasticsearchManager<? extends Resource> mananger = this.managers.get(command.getResource());
			
			if (mananger == null) {
				throw new IllegalArgumentException(""); // TODO: nice error handling
			}
			try {
				mananger.generateIndex();
			} catch (IndexAlreadyGeneratingException e) {
				throw new IllegalStateException(e);
			}
			
			return new ExtendedRedirectView("/admin/fulltextsearch");
		}
		
		// get some infos about the search indices
		final Map<String, List<SearchIndexInfo>> infoMap = command.getSearchIndexInfo();
		for (final Entry<Class<? extends Resource>, ElasticsearchManager<? extends Resource>> managementEntry : this.managers.entrySet()) {
			final ElasticsearchManager<? extends Resource> manager = managementEntry.getValue();
			
			final List<SearchIndexInfo> information = manager.getIndexInformations();
			infoMap.put(ResourceFactory.getResourceName(managementEntry.getKey()), information);
		}
		
		return Views.ADMIN_FULL_TEXT_SEARCH;
	}

	@Override
	public AdminFullTextSearchCommand instantiateCommand() {
		return new AdminFullTextSearchCommand();
	}

	/**
	 * @param managers the managers to set
	 */
	public void setManagers(Map<Class<? extends Resource>, ElasticsearchManager<? extends Resource>> managers) {
		this.managers = managers;
	}
}