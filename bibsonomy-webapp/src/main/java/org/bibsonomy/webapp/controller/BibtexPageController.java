package org.bibsonomy.webapp.controller;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for BibTeX pages
 * 
 * bibtex/HASH and bibtex/HASH/USERNAME
 * 
 * @author mwa
 * @author sts
 * @version $Id$
 */
public class BibtexPageController extends MultiResourceListController implements MinimalisticController<BibtexResourceViewCommand>{

	private static final Logger LOGGER = Logger.getLogger(BibtexPageController.class);
	
	public View workOn(BibtexResourceViewCommand command) {
		
		LOGGER.debug(this.getClass().getSigners());
		this.startTiming(this.getClass(), command.getFormat());
		
		// retrieve only tags
		if (!command.getRestrictToTags().equals("false")) {
			System.out.println("get json tags");
			this.setTags(command, BibTex.class, GroupingEntity.ALL, null, null, null, command.getRequBibtex(), null, 0, 1000, null);
			
			// TODO: other output formats
			return Views.JSONTAGS;
		}		
		
		/** Michaels part
		//if no hash given return
		if(command.getRequBibtex().length() == 0){return null;}

		final String hash = command.getRequBibtex();
		final String hashType = command.getRequSim();
		final String requUser = "";
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			this.setList(command, resourceType, GroupingEntity.ALL, null, null, hash, null,null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}	
		
		if (command.getFormat().equals("html")) {
			
			this.endTiming();
			if(requUser.length() > 0){
				return Views.BIBTEXDETAILS;
			}
			
			this.setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, 0, 1000, null);
			return Views.BIBTEXPAGE;
	
		}
		this.endTiming();
		
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());		
		*/
		return null;
	}
	
	public BibtexResourceViewCommand instantiateCommand() {		
		return new BibtexResourceViewCommand();
	}
}