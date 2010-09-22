package org.bibsonomy.webapp.controller.admin;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.webapp.command.admin.AdminLuceneViewCommand;
import org.bibsonomy.webapp.command.admin.LuceneIndexSettingsCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for lucene admin page
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class AdminLuceneController implements MinimalisticController<AdminLuceneViewCommand> {
	private static final Log log = LogFactory.getLog(AdminLuceneController.class);
	
	@SuppressWarnings("unused") // FIXME: currently unused
	private UserSettings userSettings;
	private List<LuceneResourceManager<? extends Resource>> luceneResourceManagers;

	@Override
	public View workOn(AdminLuceneViewCommand command) {
		log.debug(this.getClass().getSimpleName());

		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		command.setPageTitle("admin lucene");
		
		// Infos über die einzelnen Indexe
		// Anzahl Einträge, letztes Update, ...
		
		List<LuceneIndexSettingsCommand> indices = command.getIndices();
		
		for(LuceneResourceManager<? extends Resource> manager: luceneResourceManagers) {
			LuceneIndexSettingsCommand indexCmd         = new LuceneIndexSettingsCommand();
			LuceneIndexSettingsCommand indexCmdInactive = new LuceneIndexSettingsCommand();
			
			indexCmd.setIndexStatistics(manager.getStatistics());
			indexCmd.setName(manager.getResourceName() + " index");
			indexCmd.setInactiveIndex(indexCmdInactive);
			
			indexCmdInactive.setIndexStatistics(manager.getInactiveIndexStatistics());

			indices.add(indexCmd);
		}
		
		return Views.ADMIN_LUCENE;
	}

	@Override
	public AdminLuceneViewCommand instantiateCommand() {
		return new AdminLuceneViewCommand();
	}
	
	/**
	 * @param luceneResourceManagers
	 */
	public void setLuceneResourceManagers(List<LuceneResourceManager<? extends Resource>> luceneResourceManagers) {
		this.luceneResourceManagers = luceneResourceManagers;
	}

	/**
	 * @param userSettings the userSettings to set
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

}