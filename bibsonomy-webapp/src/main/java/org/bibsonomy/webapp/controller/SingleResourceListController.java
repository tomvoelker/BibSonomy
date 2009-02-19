package org.bibsonomy.webapp.controller;


import org.apache.log4j.Logger;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * controller for retrieving a windowed list with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class SingleResourceListController extends ResourceListController {
	private static final Logger log = Logger.getLogger(SingleResourceListController.class);
		
	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 */
	protected <T extends SimpleResourceViewCommand> void postProcessAndSortList(T cmd, Class<? extends Resource> resourceType) {				
		if (resourceType == BibTex.class) {
			postProcessAndSortList(cmd, cmd.getBibtex().getList());
		}
	}
	
}