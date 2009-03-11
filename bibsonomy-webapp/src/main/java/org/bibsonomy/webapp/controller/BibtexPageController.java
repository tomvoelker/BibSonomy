package org.bibsonomy.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.ResourceType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author mwa
 * @version $Id$
 */
public class BibtexPageController extends SingleResourceListController implements MinimalisticController<BibtexResourceViewCommand>{

	private static final Logger LOGGER = Logger.getLogger(BibtexPageController.class);
	
	public View workOn(BibtexResourceViewCommand command) {
		
		LOGGER.debug(this.getClass().getSigners());
		this.startTiming(this.getClass(), command.getFormat());
		
		//if no hash given -> error
		if(command.getRequBibtex().length() == 0){
			LOGGER.error("Invalid query /bibtex without hashvalue");
			throw new MalformedURLSchemeException("error.bibtex_no_hash");
		}

		final String hash     = command.getRequBibtex();
		final String requUser = command.getRequestedUser();
		final GroupingEntity groupingEntity = (requUser != null ? GroupingEntity.USER : GroupingEntity.ALL);

		// handle case when only tags are requested
		// retrieve only 1000 tags for this resource
		// FIXME: hardcoded end value
		command.setResourcetype("bibtex");
		this.handleTagsOnly(command, groupingEntity, requUser, null, null, hash, null, 0, 1000, null);
		
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();
			
			this.setList(command, resourceType, groupingEntity, requUser, null, hash, null, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			this.setTotalCount(command, resourceType, groupingEntity, requUser, null, hash, null, null, null, command.getListCommand(resourceType).getEntriesPerPage(), null);
		}	
		
		// get the title of the publication with the requested hash
		final List<Post<BibTex>> bibtexList = command.getBibtex().getList();
		if (bibtexList != null && bibtexList.size() > 0){
			command.setTitle(bibtexList.get(0).getResource().getTitle());
		}
		
		if (command.getFormat().equals("html")) {
			this.endTiming();
			
			command.setPageTitle("bibtex :: " + hash );
			
			if (GroupingEntity.USER.equals(groupingEntity)) {
				/*
				 * /bibtex/HASH/USER
				 */

				/*
				 * complete post details
				 */
				final ArrayList<Post<BibTex>> bibtex = new ArrayList<Post<BibTex>>();
				for (Post<BibTex> b: bibtexList){
					bibtex.add((Post<BibTex>) this.logic.getPostDetails(b.getResource().getIntraHash(), b.getUser().getName()));
				}
				command.getBibtex().setList(bibtex);

				/*
				 * retrieve concepts for sidebar
				 */
				final List<Tag> concepts = this.logic.getConcepts(null, groupingEntity, requUser, null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
				command.getConcepts().setConceptList(concepts);
				command.getConcepts().setNumConcepts(concepts.size());
				
				/*
				 * show complete tag cloud of user
				 */
				this.setTags(command, Resource.class, groupingEntity, requUser, null, null, null, null, 0, Integer.MAX_VALUE, null);
				return Views.BIBTEXDETAILS;
			}
			/*
			 * get only those tags, related to the resource
			 * FIXME: hardcoded end value
			 * FIXME: here we assume, bibtexsare handled, further above we use listsToInitialize ...
			 */
			this.setTags(command, BibTex.class, groupingEntity, requUser, null, null, hash, null, 0, 1000, null);
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
