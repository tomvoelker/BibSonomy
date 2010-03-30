package org.bibsonomy.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for bibtex pages:
 *   - /bibtex/HASH
 *   - /bibtex/HASH/USERNAME
 * 
 * @author Michael Wagner
 * @author Dominik Benz, benz@cs.uni-kassel.de 
 * @version $Id$
 */
public class BibtexPageController extends SingleResourceListControllerWithTags implements MinimalisticController<BibtexResourceViewCommand>{

	public View workOn(BibtexResourceViewCommand command) {

		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		/*
		 * if no hash given -> error
		 */
		if(command.getRequBibtex() == null || command.getRequBibtex().length() == 0){
			throw new MalformedURLSchemeException("error.bibtex_no_hash");
		}

		/*
		 * Set hash, username, grouping entity
		 */
		final String hash     = command.getRequBibtex();
		final String requUser = command.getRequestedUser();
		final GroupingEntity groupingEntity = (requUser != null ? GroupingEntity.USER : GroupingEntity.ALL);

		/* 
		 * handle case when only tags are requested
		 * retrieve only 1000 tags for this resource
		 * FIXME: hardcoded end value
		 */
		command.setResourcetype("bibtex");
		this.handleTagsOnly(command, groupingEntity, requUser, null, null, hash, 1000, null);

		/*
		 * retrieve and set the requested bibtex(s)
		 */
		for (final Class<? extends Resource> resourceType : listsToInitialise) {

			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();		
			this.setList(command, resourceType, groupingEntity, requUser, null, hash, null, null, null, entriesPerPage);
			
			if (GroupingEntity.ALL.equals(groupingEntity)) {
				/* 
				 * retrieve total count with given hash 
				 * (only for /bibtex/HASH)
				 */
				this.setTotalCount(command, resourceType, groupingEntity, requUser, null, hash, null, null, null, entriesPerPage, null);
			} else if (GroupingEntity.USER.equals(groupingEntity)) {
				/*
				 * complete post details for a single post of a given user 
				 * (only for /bibtex/HASH/USER)
				 */
				final ArrayList<Post<BibTex>> bibtex = new ArrayList<Post<BibTex>>();
				for (final Post<BibTex> b : command.getBibtex().getList()){
					bibtex.add((Post<BibTex>) this.logic.getPostDetails(b.getResource().getIntraHash(), b.getUser().getName()));
				}			
				command.getBibtex().setList(bibtex);			
			}
			/*
			 * post process and sort list (e.g., insert open URL)
			 */
			this.postProcessAndSortList(command, resourceType);

		}

		/*
		 * extract first bibtex; if list is empty, return blank page
		 */
		final BibTex firstBibtex;
		if (command.getBibtex().getList() != null && command.getBibtex().getList().size() > 0){
			firstBibtex = command.getBibtex().getList().get(0).getResource();			
		} else {
			if ("html".equals(format)) {
				return (GroupingEntity.USER.equals(groupingEntity) ? Views.BIBTEXDETAILS : Views.BIBTEXPAGE);				
			} 
			return Views.getViewByFormat(format);
		}

		/*
		 * Set page title to title of first publication 
		 */
		command.setTitle(firstBibtex.getTitle());

		if (format.equals("html")) {
			this.endTiming();

			command.setPageTitle("bibtex :: " + command.getTitle() );

			if (GroupingEntity.USER.equals(groupingEntity)) { //bibtex/HASH/USER
				/*
				 * fetch posts of all users with the given hash, add users to related
				 * users list				
				 */
				final List<Post<BibTex>> allPosts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, firstBibtex.getInterHash(), null, null, 0, 1000, null);
				for (Post<BibTex> post : allPosts) {
					command.getRelatedUserCommand().getRelatedUsers().add(post.getUser());
				}

				/* 
				 * set "correct" count .This is the number of ALL users having the publication
				 * with the interHash of firstBibtex in their collection. In allPosts, only public posts
				 * are contained, hence it can be smaller.
				 */
				this.setTotalCount(command, BibTex.class, GroupingEntity.ALL, null, null, firstBibtex.getInterHash(), null, null, null, 1000, null);
				firstBibtex.setCount(command.getBibtex().getTotalCount());

				/*
				 * show tags by all users for this resource; the ones by the given user
				 * will be highlighted later
				 * FIXME: hardcoded end value
				 */
				this.setTags(command, BibTex.class, GroupingEntity.ALL, null, null, null, hash, 1000, null);
				return Views.BIBTEXDETAILS;
			}
			/*
			 * get only those tags, related to the resource
			 * FIXME: hardcoded end value
			 */
			this.setTags(command, BibTex.class, groupingEntity, requUser, null, null, hash, 1000, null);			
			return Views.BIBTEXPAGE;

		}
		this.endTiming();

		// export - return the appropriate view
		return Views.getViewByFormat(format);

	}

	public BibtexResourceViewCommand instantiateCommand() {
		return new BibtexResourceViewCommand();
	}

}
