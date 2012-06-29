package org.bibsonomy.webapp.controller.admin;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.lucene.index.manager.LuceneResourceManager;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.admin.AdminLuceneViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * Controller for lucene admin page
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class AdminLuceneController implements MinimalisticController<AdminLuceneViewCommand> {
	private static final Log log = LogFactory.getLog(AdminLuceneController.class);
	
	private static final String GENERATE_INDEX = "generateIndex";
	private static final String GENERATE_ONE_INDEX = "generateOneIndex";
	
	
	private List<LuceneResourceManager<? extends Resource>> luceneResourceManagers;
	
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
		
		if (GENERATE_INDEX.equals(command.getAction()) 
				|| (GENERATE_ONE_INDEX.equals(command.getAction()))) {
			final LuceneResourceManager<? extends Resource> mng = getManagerByResourceName(command.getResource());
			if (mng != null) {
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
			} else {
				command.setAdminResponse("Cannot build new index because there exists no manager for resource \"" + command.getResource() + "\".");
			}
			
			return new ExtendedRedirectView("/admin/lucene");
		}
		// Infos über die einzelnen Indexe
		// Anzahl Einträge, letztes Update, ...
		final List<LuceneResourceManager<? extends Resource>> indices = command.getIndices();
		
		for (final LuceneResourceManager<? extends Resource> manager: luceneResourceManagers) {
			indices.add(manager);
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
}