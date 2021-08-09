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
package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.exceptions.IndexAlreadyGeneratingException;
import org.bibsonomy.search.management.SearchIndexManager;
import org.bibsonomy.search.model.SearchIndexInfo;
import org.bibsonomy.webapp.command.admin.AdminFullTextSearchCommand;
import org.bibsonomy.webapp.command.admin.AdminFullTextSearchCommand.AdminFullTextAction;
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

	private Map<Class<?>, SearchIndexManager> managers;
	
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
		
		final AdminFullTextAction action = command.getAction();
		if (present(action)) {
			final Class<?> entityClass = getEntityClass(command.getEntity());
			final SearchIndexManager mananger = this.managers.get(entityClass);
			if (mananger == null) {
				throw new IllegalArgumentException("cannot find manager for resource " + entityClass);
			}
			final String indexId = command.getId();
			switch (action) {
			case REGENERATE_INDEX:
				try {
					mananger.regenerateIndex(indexId);
				} catch (final IndexAlreadyGeneratingException e) {
					throw new IllegalStateException(e);
				}
				break;
			case GENERATE_INDEX:
				try {
					mananger.generateIndex();
				} catch (final IndexAlreadyGeneratingException e) {
					throw new IllegalStateException(e);
				}
				break;
			case ENABLE_INDEX:
				mananger.enableIndex(indexId);
				break;
			case DELETE_INDEX:
				mananger.deleteIndex(indexId);
				break;
			}
			return new ExtendedRedirectView("/admin/fulltextsearch");
		}
		
		// get some infos about the search indices
		final Map<String, List<SearchIndexInfo>> infoMap = command.getSearchIndexInfo();
		for (final Entry<Class<?>, SearchIndexManager> managementEntry : this.managers.entrySet()) {
			final SearchIndexManager manager = managementEntry.getValue();
			
			final List<SearchIndexInfo> information = manager.getIndexInformations();
			infoMap.put(managementEntry.getKey().getSimpleName(), information);
		}
		
		return Views.ADMIN_FULL_TEXT_SEARCH;
	}

	private static Class<?> getEntityClass(final String entity) {
		// TODO: move to some model factory
		if (present(entity)) {
			switch (entity) {
				case "Person": return Person.class;
				case "Group": return Group.class;
				case "Project" : return Project.class;
			}
		}

		return ResourceFactory.getResourceClass(entity);
	}

	@Override
	public AdminFullTextSearchCommand instantiateCommand() {
		return new AdminFullTextSearchCommand();
	}

	/**
	 * @param managers the managers to set
	 */
	public void setManagers(Map<Class<?>, SearchIndexManager> managers) {
		this.managers = managers;
	}
}