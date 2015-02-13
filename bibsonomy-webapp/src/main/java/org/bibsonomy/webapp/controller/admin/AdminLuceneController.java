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

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.es.SharedIndexUpdatePlugin;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.lucene.param.LuceneIndexInfo;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.admin.AdminLuceneViewCommand;
import org.bibsonomy.webapp.command.admin.LuceneResourceIndicesInfoContainer;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * Controller for lucene admin page
 * controller for /admin/generateSharedIndex page
 * 
 * @author Sven Stefani
 */
public class AdminLuceneController implements MinimalisticController<AdminLuceneViewCommand> {
	private static final Log log = LogFactory.getLog(AdminLuceneController.class);
	
	private static final String GENERATE_INDEX = "generateIndex";
	private static final String GENERATE_ONE_INDEX = "generateOneIndex";
	
	private List<LuceneResourceManager<? extends Resource>> luceneResourceManagers;
	/** plugin for elasticsearch */
	private SharedIndexUpdatePlugin<? extends Resource> srPlugin;

	@Override
	public View workOn(final AdminLuceneViewCommand command) {
		log.debug(this.getClass().getSimpleName());

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}	
		
		//check if ckey is valid
//		if (!context.isValidCkey()) {
//			errors.reject("error.field.valid.ckey");
//			return Views.ERROR;
//		}
		
//		if (generateSharedIndex) {
//			srPlugin.generateIndex(luceneResourceManagers);
//			return Views.SUCCESS;
//		}
		
		if (GENERATE_INDEX.equals(command.getAction()) 
				|| (GENERATE_ONE_INDEX.equals(command.getAction()))) {
			if (command.getResource() == null) {
				if ((srPlugin != null) && "elasticsearch".equals(command.getIndexType())) {
					srPlugin.generateIndex(luceneResourceManagers);
				} else {
					throw new IllegalArgumentException("unsupported indextype '" + command.getIndexType() + "'");
				}
			} else {
				final LuceneResourceManager<? extends Resource> mng = getManagerByResourceName(command.getResource());
				if (mng != null) {
					if ((srPlugin != null) && "elasticsearch".equals(command.getIndexType())) {
						srPlugin.generateIndex(mng);
					} else {
						if (!mng.isGeneratingIndex()) {
							if (GENERATE_INDEX.equals(command.getAction())) {
								mng.generateIndex();	
							}
							if (GENERATE_ONE_INDEX.equals(command.getAction())) {
								mng.regenerateIndex(command.getId());
							}
						} else {
							command.setAdminResponse("Already building lucene-index for resource \"" + command.getResource() + "\".");
						}
					}
				} else {
					command.setAdminResponse("Cannot build new index because there exists no manager for resource \"" + command.getResource() + "\".");
				}
			}
			
			return new ExtendedRedirectView("/admin/lucene");
		}
		// Infos über die einzelnen Indexe
		// Anzahl Einträge, letztes Update, ...
		for (final LuceneResourceManager<? extends Resource> manager: luceneResourceManagers) {
			LuceneResourceIndicesInfoContainer lriic = new LuceneResourceIndicesInfoContainer();
			lriic.setResourceName(manager.getResourceName());
			lriic.getLuceneResoruceIndicesInfos().addAll(manager.getIndicesInfos());
			command.getIndicesInfos().add(lriic);
		}
		
		if (srPlugin != null) {
			String globalError = srPlugin.getGlobalIndexNonExistanceError();
			if (globalError != null) {
				command.setEsGlobalMessage(globalError);
			} else {
				for (final LuceneResourceManager<? extends Resource> manager: luceneResourceManagers) {
					Collection<? extends LuceneIndexInfo> infos = srPlugin.getIndicesInfos(manager);
					for (LuceneIndexInfo info : infos) {
						LuceneResourceIndicesInfoContainer infoCon = new LuceneResourceIndicesInfoContainer();
						infoCon.setResourceName(manager.getResourceName() + " elasticsearch");
						infoCon.getLuceneResoruceIndicesInfos().add(info);
						command.getEsIndicesInfos().add(infoCon);
					}
					
				}
			}
		}
		
		return Views.ADMIN_LUCENE;
	}
	
	private LuceneResourceManager<? extends Resource> getManagerByResourceName(final String resource) {
		for(final LuceneResourceManager<? extends Resource> mng: luceneResourceManagers) {
			if(mng.getResourceName().equals(resource)) {
				return mng;
			}
		}
		return null;
	}
	

	@Override
	public AdminLuceneViewCommand instantiateCommand() {
		return new AdminLuceneViewCommand();
	}
	
	/**
	 * TODO: use the Map<ResourceClass, LuceneResourceManager>
	 * @param luceneResourceManagers
	 */
	public void setLuceneResourceManagers(final List<LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		this.luceneResourceManagers = luceneResourceManagers;
	}

	/**
	 * @return srPlugin
	 */
	public SharedIndexUpdatePlugin<? extends Resource> getSrPlugin() {
		return this.srPlugin;
	}

	/**
	 * @param srPlugin
	 */
	public void setSrPlugin(SharedIndexUpdatePlugin<? extends Resource> srPlugin) {
		this.srPlugin = srPlugin;
	}
}