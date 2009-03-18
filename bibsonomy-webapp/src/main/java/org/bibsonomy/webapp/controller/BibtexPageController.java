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
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author mwa
 * @version $Id$
 */
public class BibtexPageController extends SingleResourceListControllerWithTags implements MinimalisticController<BibtexResourceViewCommand>{

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
		
		// retrieve and set the requested bibtex(s)
		final int entriesPerPage = command.getListCommand(BibTex.class).getEntriesPerPage();
		
		this.setList(command, BibTex.class, groupingEntity, requUser, null, hash, null, null, null, entriesPerPage);
		this.postProcessAndSortList(command, BibTex.class);
		
		// only set total count if no user is given (is always 1 otherwise)
		if (GroupingEntity.ALL.equals(groupingEntity)) {
			this.setTotalCount(command, BibTex.class, groupingEntity, requUser, null, hash, null, null, null, command.getListCommand(BibTex.class).getEntriesPerPage(), null);
		}		
		else {
			/*
			 * complete post details for a single post of a given user (only for
			 * /bibtex/HASH/USER
			 */
			final ArrayList<Post<BibTex>> bibtex = new ArrayList<Post<BibTex>>();
			for (Post<BibTex> b: command.getBibtex().getList()){
				bibtex.add((Post<BibTex>) this.logic.getPostDetails(b.getResource().getIntraHash(), b.getUser().getName()));
			}
			command.getBibtex().setList(bibtex);

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
				//bibtex/HASH/USER
				
				// fetch other users who have tagged this publication				
				List<Post<BibTex>> allPosts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, bibtexList.get(0).getResource().getInterHash(), null, null, 0, 1000, null);
				for (Post<BibTex> post : allPosts) {
					command.getRelatedUserCommand().getRelatedUsers().add(post.getUser());
				}
								
				/*
				 * show tags by all users for this resource; the ones by the given user
				 * will be highlighted later
				 * FIXME: hardcoded end value
				 */
				//this.setTags(command, Resource.class, groupingEntity, requUser, null, tagsList, null, null, 0, Integer.MAX_VALUE, null);
				this.setTags(command, BibTex.class, GroupingEntity.ALL, null, null, null, hash, null, 0, 1000, null);
				return Views.BIBTEXDETAILS;
			}
			/*
			 * get only those tags, related to the resource
			 * FIXME: hardcoded end value
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
