package org.bibsonomy.webapp.controller;


import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * controller for retrieving a windowed list with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class SingleResourceListController extends ResourceListController {
		
	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 */
	protected <T extends SimpleResourceViewCommand> void postProcessAndSortList(T cmd, Class<? extends Resource> resourceType) {				
		final List<Post<BibTex>> list = cmd.getBibtex().getList();
		if (resourceType == BibTex.class && list != null) {
			postProcessAndSortList(cmd, list);
		}
	}
	
}