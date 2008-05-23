package org.bibsonomy.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author mwa
 * @version $Id$
 */
public class BibtexPageController extends MultiResourceListController implements MinimalisticController<BibtexResourceViewCommand>{

	private static final Logger LOGGER = Logger.getLogger(BibtexPageController.class);
	
	public View workOn(BibtexResourceViewCommand command) {
		
		LOGGER.debug(this.getClass().getSigners());
		this.startTiming(this.getClass(), command.getFormat());
		
		//if no hash given return
		if(command.getRequBibtex().length() == 0){
			LOGGER.error("Invalid query /bibtex without hashvalue");
			throw new MalformedURLSchemeException("error.bibtex_no_hash");
		}

		final String hash = command.getRequBibtex();
		String requUser = command.getRequestedUser();

		// retrieve only tags
		if (!command.getRestrictToTags().equals("false")) {
			this.setTags(command, BibTex.class, GroupingEntity.ALL, null, null, null, hash, null, 0, 1000, null);
			
			// TODO: other output formats
			return Views.JSONTAGS;
		}		
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			this.setList(command, resourceType, requUser!=null?GroupingEntity.USER:GroupingEntity.ALL, requUser, null, hash, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}	
		
		//get the title of the publication with the requested hash (intrahash)
		if(command.getBibtex().getList().size() > 0){
			command.setTitle(command.getBibtex().getList().get(0).getResource().getTitle());
		}
		
		if (command.getFormat().equals("html")) {
			this.endTiming();
			if(requUser != null){
				
				// retrieve concepts
				List<Tag> concepts = this.logic.getConcepts(null, GroupingEntity.USER, requUser, null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
				command.getConcepts().setConceptList(concepts);
				command.getConcepts().setNumConcepts(concepts.size());
				
				ArrayList<Post<BibTex>> bibtex = new ArrayList<Post<BibTex>>();
				for(Post<BibTex> b: command.getBibtex().getList()){
					bibtex.add((Post<BibTex>) this.logic.getPostDetails(b.getResource().getIntraHash(), b.getUser().getName()));
				}
				command.getBibtex().setList(bibtex);
				this.setTags(command, Resource.class, GroupingEntity.USER, requUser, null, null, null, null, 0, 1000, null);
				return Views.BIBTEXDETAILS;
			}
			this.setTags(command, BibTex.class, GroupingEntity.ALL, requUser, null, null, hash, null, 0, 1000, null);
			return Views.BIBTEXPAGE;
			
	
		}
		this.endTiming();
		
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
		
	}
	
	public BibtexResourceViewCommand instantiateCommand() {
		
		return new BibtexResourceViewCommand();
	}

}
