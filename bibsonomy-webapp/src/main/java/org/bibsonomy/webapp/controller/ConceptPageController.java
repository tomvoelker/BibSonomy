package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.ConceptResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for concept pages
 * 
 * @author Michael Wagner
 * @version $Id$
 */
public class ConceptPageController extends SingleResourceListController implements MinimalisticController<ConceptResourceViewCommand>{
	private static final Log log = LogFactory.getLog(ConceptPageController.class);

	public View workOn(final ConceptResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		// if no concept given -> error
		if(!present(command.getRequestedTags())) {
			log.error("Invalid query /concept without concept name");
			throw new MalformedURLSchemeException("error.concept_page_without_conceptname");
		}
		
		final List<String> requTags = command.getRequestedTagsList();

		for (int i = 0; i < requTags.size(); i++){
			requTags.set(i, "->" + requTags.get(i));
		}
		
		final String requUser = command.getRequestedUser();
		final String requGroup = command.getRequestedGroup();
		
		GroupingEntity groupingEntity = GroupingEntity.ALL;
		String groupingName = null;
		
		// title
		final StringBuilder pageTitle = new StringBuilder("concept :: ");
		
		//if URI looks like concept/USER/USERNAME/TAGNAME, change GroupingEntity to USER
		if(present(requUser)){
			groupingEntity = GroupingEntity.USER;
			groupingName = requUser;
			pageTitle.append(" user :: ");
		}
		
		//if URI looks like concept/GROUP/GROUPNAME/TAGNAME, change GroupingEntity to GROUP 
		if (present(requGroup)) {
			groupingEntity = GroupingEntity.GROUP;
			groupingName = requGroup;
			pageTitle.append(" group :: ");
		}
		
		pageTitle.append(groupingName + " :: " + StringUtils.implodeStringCollection(requTags, " "));		
		command.setPageTitle(pageTitle.toString());
	
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.listsToInitialise) {			
			this.setList(command, resourceType, groupingEntity, groupingName, requTags, null, null, null, null, command.getListCommand(resourceType).getEntriesPerPage());
			this.postProcessAndSortList(command, resourceType);
		}	
		
		// retrieve concepts
		final List<Tag> concepts = new ArrayList<Tag>();
		for (final String requTag : requTags) {
			final Tag conceptDetails = this.logic.getConceptDetails(requTag.substring(2), groupingEntity, groupingName);
			if (present(conceptDetails)){
				concepts.add(conceptDetails);
			}
		}
		
		if (present(concepts)) {
			command.getConcepts().setConceptList(concepts);
			command.getConcepts().setNumConcepts(concepts.size());
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
			if(groupingEntity != GroupingEntity.ALL) {
				this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, 1000, null);
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
