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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.search.es.update.SharedIndexUpdatePlugin;
import org.bibsonomy.search.model.SearchIndexInfo;
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
	
//	private List<LuceneResourceManager<? extends Resource>> luceneResourceManagers;
//	/** plugin for elasticsearch */
//	private SharedIndexUpdatePlugin<? extends Resource> srPlugin;
//	
	private Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>>   luceneResourceManagers;
	private Map<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> sharedIndexUpdatePlugins;

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
				if ("elasticsearch".equals(command.getIndexType())) {
					for (SharedIndexUpdatePlugin<? extends Resource> srPlugin : sharedIndexUpdatePlugins.values()) {
						srPlugin.generateIndex(false);
					}
				} else {
					command.setAdminResponse("unsupported indextype '" + command.getIndexType() + "'");
				}
			} else {
				final Class<? extends Resource> cls = ResourceUtils.getResourceClassBySimpleName(command.getResource().replaceAll(" elasticsearch", ""));
				final LuceneResourceManager<? extends Resource> mng = luceneResourceManagers.get(cls);
				final SharedIndexUpdatePlugin<? extends Resource> esUpdater = sharedIndexUpdatePlugins.get(cls);
				if (mng == null) {
					command.setAdminResponse("Cannot build new index because there exists no manager for resource \"" + command.getResource() + "\".");
				} else if (esUpdater == null) {
					command.setAdminResponse("Cannot build new index because there exists no updater for resource \"" + command.getResource() + "\".");
				} else {
					if ("elasticsearch".equals(command.getIndexType())) {
						esUpdater.generateIndex(false);
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
				}
			}
			
			return new ExtendedRedirectView("/admin/lucene");
		}
		// Infos über die einzelnen Indexe
		// Anzahl Einträge, letztes Update, ...
		for (final LuceneResourceManager<? extends Resource> manager: luceneResourceManagers.values()) {
			LuceneResourceIndicesInfoContainer lriic = new LuceneResourceIndicesInfoContainer();
			lriic.setResourceName(manager.getResourceName());
			lriic.getLuceneResoruceIndicesInfos().addAll(manager.getIndicesInfos());
			command.getIndicesInfos().add(lriic);
		}
		
		if (sharedIndexUpdatePlugins != null) {

			
			for (Entry<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> e : sharedIndexUpdatePlugins.entrySet()) {
				final SharedIndexUpdatePlugin<? extends Resource> esUpdatePlugin = e.getValue();
				final String globalError = esUpdatePlugin.getGlobalIndexNonExistanceError();
				if (globalError != null) {
					command.setEsGlobalMessage(globalError);
				}
					final LuceneResourceManager<? extends Resource> mng = luceneResourceManagers.get(e.getKey());
					if (mng == null) {
						command.setAdminResponse("Cannot show elasticsearch index info for \"" + command.getResource() + "\" because there is no luceneResourceManager.");
					} else {
						Collection<? extends SearchIndexInfo> infos = esUpdatePlugin.getIndicesInfos(mng.getResourceName());
						for (SearchIndexInfo info : infos) {
							LuceneResourceIndicesInfoContainer infoCon = new LuceneResourceIndicesInfoContainer();
							infoCon.setResourceName(mng.getResourceName() + " elasticsearch");
							infoCon.getLuceneResoruceIndicesInfos().add(info);
							if(mng.getResourceName().equalsIgnoreCase(BibTex.class.getSimpleName())){
								command.getEsIndicesInfosBibtex().add(infoCon);
							}else if(mng.getResourceName().equalsIgnoreCase(Bookmark.class.getSimpleName())){
								command.getEsIndicesInfosBookmark().add(infoCon);
							}else if(mng.getResourceName().equalsIgnoreCase(GoldStandardPublication.class.getSimpleName())){
								command.getEsIndicesInfosGoldStandard().add(infoCon);
							}
						}
					}
				
			}
			
		}
		
		return Views.ADMIN_LUCENE;
	}
	

	@Override
	public AdminLuceneViewCommand instantiateCommand() {
		return new AdminLuceneViewCommand();
	}


	public Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>> getLuceneResourceManagers() {
		return this.luceneResourceManagers;
	}


	public void setLuceneResourceManagers(Map<Class<? extends Resource>, LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		this.luceneResourceManagers = luceneResourceManagers;
	}


	public Map<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> getSharedIndexUpdatePlugins() {
		return this.sharedIndexUpdatePlugins;
	}


	public void setSharedIndexUpdatePlugins(Map<Class<? extends Resource>, SharedIndexUpdatePlugin<? extends Resource>> sharedIndexUpdatePlugins) {
		this.sharedIndexUpdatePlugins = sharedIndexUpdatePlugins;
	}
	
}