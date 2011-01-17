package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
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
public class BibtexPageController extends SingleResourceListControllerWithTags implements MinimalisticController<BibtexResourceViewCommand> {
	private static final String GOLD_STANDARD_USER_NAME = "";
	private static final int TAG_LIMIT = 1000;

	@SuppressWarnings("unchecked")
	@Override
	public View workOn(final BibtexResourceViewCommand command) {
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);

		final String hash = command.getRequBibtex();
		/*
		 * if no hash given -> error
		 */
		if (!present(hash)){
			throw new MalformedURLSchemeException("error.bibtex_no_hash");
		}

		/*
		 * Set hash, username, grouping entity
		 */
		final String requUser = command.getRequestedUser();
		final GroupingEntity groupingEntity = requUser != null ? GroupingEntity.USER : GroupingEntity.ALL;

		/* 
		 * handle case when only tags are requested
		 * retrieve only TAG_LIMIT tags for this resource
		 */
		command.setResourcetype(Collections.<Class<? extends Resource>>singleton(BibTex.class));
		this.handleTagsOnly(command, groupingEntity, requUser, null, null, hash, TAG_LIMIT, null);

		// for getting the gold standard
		String goldHash = hash.substring(1); // remove leading 1 TODODZ

		/*
		 * retrieve and set the requested publication(s)
		 */
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
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
				final List<Post<BibTex>> bibtex = new ArrayList<Post<BibTex>>();
				for (final Post<BibTex> b : command.getBibtex().getList()) {
					final BibTex publication = b.getResource();

					Post<BibTex> postDetails = null;
					try {
						postDetails = (Post<BibTex>) this.logic.getPostDetails(publication.getIntraHash(), b.getUser().getName());
						bibtex.add(postDetails);

						goldHash = postDetails.getResource().getInterHash();
					} catch (final ResourceNotFoundException ex) {
						// ignore
					} catch (final ResourceMovedException ex) {
						// ignore
					}
				}
				command.getBibtex().setList(bibtex);
			}
			/*
			 * post process and sort list (e.g., insert open URL)
			 */
			this.postProcessAndSortList(command, resourceType);
		}

		Post<GoldStandardPublication> goldStandard = null;
		try {
			/*
			 * get the gold standard
			 */
			goldStandard = (Post<GoldStandardPublication>) this.logic.getPostDetails(goldHash, GOLD_STANDARD_USER_NAME);
			command.setGoldStandardPublication(goldStandard);
		} catch (final ResourceNotFoundException ex) {
			// ignore
		} catch (final ResourceMovedException ex) {
			// ignore
		}
		

		/*
		 * extract first bibtex; if list is empty, return blank page
		 */
		final BibTex firstBibtex;
		if (present(command.getBibtex().getList())){
			firstBibtex = command.getBibtex().getList().get(0).getResource();			
		} else {
			if ("html".equals(format)) {
				return (GroupingEntity.USER.equals(groupingEntity) ? Views.BIBTEXDETAILS : Views.BIBTEXPAGE);				
			} 
			return Views.getViewByFormat(format);
		}
		command.setDocuments(firstBibtex.getDocuments());
		
		/*
		 * Set page title to title of first publication 
		 */
		command.setTitle(firstBibtex.getTitle());
		this.endTiming();
		if ("html".equals(format)) {
			command.setPageTitle("publication :: " + command.getTitle()); // TODO: i18n
			
			if (GroupingEntity.USER.equals(groupingEntity) || present(goldStandard)) {
				/*
				 * fetch posts of all users with the given hash, add users to related
				 * users list				
				 */
				final List<Post<BibTex>> allPosts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, firstBibtex.getInterHash(), null, null, 0, 1000, null);
				for (final Post<BibTex> post : allPosts) {
					command.getRelatedUserCommand().getRelatedUsers().add(post.getUser());
				}
			}

			if (GroupingEntity.USER.equals(groupingEntity)) {
				// bibtex/HASH/USER
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
				 */
				this.setTags(command, BibTex.class, GroupingEntity.ALL, null, null, null, hash, TAG_LIMIT, null);
				return Views.BIBTEXDETAILS;
			}
			/*
			 * get only those tags, related to the resource
			 */
			this.setTags(command, BibTex.class, groupingEntity, requUser, null, null, hash, TAG_LIMIT, null);			
			return Views.BIBTEXPAGE;
		}

		// export - return the appropriate view
		return Views.getViewByFormat(format);

	}

	@Override
	public BibtexResourceViewCommand instantiateCommand() {
		return new BibtexResourceViewCommand();
	}

}
