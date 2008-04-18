package org.bibsonomy.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.ConceptResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;


/**
 * Controller for concept pages
 * 
 * @author Michael Wagner
 * @version $Id$
 */
public class ConceptPageController  extends MultiResourceListController implements MinimalisticController<ConceptResourceViewCommand>{
	private static final Logger LOGGER = Logger.getLogger(ConceptPageController.class);

	public View workOn(ConceptResourceViewCommand command) {
		LOGGER.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		//if no tags given return
		if(command.getRequestedTags().length() == 0) return null;
		final List<String> requTags = command.getRequestedTagsList();

		for(int i = 0; i < requTags.size(); i++){
			String conceptTag = "->" + requTags.get(i);
			requTags.set(i, conceptTag);
		}
		
		final String requUser = command.getRequestedUser();
		final String requGroup = command.getRequestedGroup();
		
		GroupingEntity groupingEntity = GroupingEntity.ALL;
		String groupingName = null;
		
		//if URI looks like concept/USER/USERNAME/TAGNAME, change GroupingEntity to USER
		if(requUser.length() > 0){
			groupingEntity = GroupingEntity.USER;
			groupingName = requUser;
		}
		
		//if URI looks like concept/GROUP/GROUPNAME/TAGNAME, change GroupingEntity to GROUP 
		if (requGroup.length() > 0) {
			groupingEntity = GroupingEntity.GROUP;
			groupingName = requGroup;
		}
	
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}	
		
		// retrieve concepts
		List<Tag> concepts = new ArrayList<Tag>();
		for(int i = 0; i < requTags.size(); i++){
			if(this.logic.getConceptDetails(requTags.get(i).substring(2), groupingEntity, groupingName) != null){
				concepts.add(this.logic.getConceptDetails(requTags.get(i).substring(2), groupingEntity, groupingName));
			}
		}
		if(concepts.size() > 0){
			command.getConcepts().setConceptList(concepts);
			command.getConcepts().setNumConcepts(concepts.size());
		}

		// html format - retrieve tags and return HTML view
		if (command.getFormat().equals("html")) {
			if(groupingEntity != GroupingEntity.ALL) {
				this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 1000, null);
			}
			this.endTiming();
			return Views.CONCEPTPAGE;			
		}
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
	}

	public ConceptResourceViewCommand instantiateCommand() {
		return new ConceptResourceViewCommand();
	}	
}
