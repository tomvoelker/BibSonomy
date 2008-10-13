package org.bibsonomy.webapp.controller;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.BibtexkeyCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for BibtexKey * 
 *
 * @author Flori
 * @version $Id$
 */
public class BibtexkeyPageController extends SingleResourceListController implements MinimalisticController<BibtexkeyCommand> {
	private static final Logger LOGGER = Logger.getLogger(BibtexkeyPageController.class);

	public View workOn(BibtexkeyCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());		
		
		if (!ValidationUtils.present(command.getRequestedKey())) {
			LOGGER.error("Invalid query /bibtexkey without key");
			throw new MalformedURLSchemeException("error.bibtexkey_no_key");
		}
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {
			// disable manual setting of start value for homepage
			command.getListCommand(resourceType).setStart(0);
			ArrayList<String> listWithBibtexKey = new ArrayList<String>();
			listWithBibtexKey.add("sys:bibtexkey:"+command.getRequestedKey());
			setList(command, resourceType, GroupingEntity.ALL, null, listWithBibtexKey, null, null, null, null, 20);
			
			postProcessAndSortList(command, resourceType);
		}
		
		command.setPageTitle("bibtexkey :: " + command.getRequestedKey());
		
		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			this.endTiming();
			return Views.BIBTEXKEYPAGE;	
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());				
	}

	
	public BibtexkeyCommand instantiateCommand() {
		return new BibtexkeyCommand();
	}

}
