package org.bibsonomy.webapp.controller.resource;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 * @version $Id$
 * @param <R> the resource of the controller
 */
public abstract class AbstractResourcePageController<R extends Resource> extends SingleResourceListControllerWithTags implements MinimalisticController<ResourcePageCommand<R>> {
	private static final String GOLD_STANDARD_USER_NAME = "";
	private static final int TAG_LIMIT = 1000;
	
	@Override
	public ResourcePageCommand<R> instantiateCommand() {
		return new ResourcePageCommand<R>();
	}
	
	@Override
	public final View workOn(final ResourcePageCommand<R> command) {
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);
		
		/*
		 * This hash has 33 characters and contains at the first position the
		 * type of the hash (see SimHash class).
		 */
		final String longHash = command.getRequestedHash();
		
		/*
		 * if no hash given -> error
		 */
		if (!present(longHash)) {
			throw new MalformedURLSchemeException("error.resource_no_hash");
		}

		/*
		 * Set hash, username, grouping entity
		 */
		final String requUser = command.getRequestedUser();
		final GroupingEntity groupingEntity = present(requUser) ? GroupingEntity.USER : GroupingEntity.ALL;
		
		return this.workOnResource(command, format, longHash, requUser, groupingEntity);
	}
	
	protected String shortHash(final String longHash) {
		if (!present(longHash) || longHash.length() != 33) {
			return longHash;
		}
		
		return longHash.substring(1);
	}

	protected View workOnResource(final ResourcePageCommand<R> command, final String format, final String longHash, final String requUser, final GroupingEntity groupingEntity) {
		/* 
		 * handle case when only tags are requested
		 * retrieve only TAG_LIMIT tags for this resource
		 */
		command.setResourcetype(Collections.<Class<? extends Resource>>singleton(this.getResourceClass()));
		this.handleTagsOnly(command, groupingEntity, requUser, null, null, longHash, TAG_LIMIT, null);
		
		/*
		 * The hash without the type of hash identifier at the first position.
		 * 32 characters long.
		 */
		final String shortHash = this.shortHash(longHash);

		/*
		 * To later retrieve the corresponding gold standard post. The intra hash
		 * of gold standard posts equals the inter hash of the corresponding 
		 * regular posts.
		 * 
		 * If an inter hash was queried, this is already the correct hash.
		 * If an intra hash was queried, we later must overwrite it with the 
		 * inter hash.
		 */
		String goldHash = shortHash; 
		
		/*
		 * retrieve and set the requested resource(s)
		 * 
		 * We always get the resource(s) as list - also when the GroupingEntity 
		 * is "USER" (where we will only show one publication!) - because we don't 
		 * know the type of the requested hash. The getPosts() method of the 
		 * LogicInterface checks for the type and returns the corresponding post(s). 
		 */
		final int entriesPerPage = command.getListCommand(this.getResourceClass()).getEntriesPerPage();		
		this.setList(command, this.getResourceClass(), groupingEntity, requUser, null, longHash, null, null, null, entriesPerPage);

		if (GroupingEntity.ALL.equals(groupingEntity)) {
			/* 
			 * retrieve total count with given hash 
			 * (only for /<RESOURCE>/HASH)
			 */
			this.setTotalCount(command, this.getResourceClass(), groupingEntity, requUser, null, longHash, null, null, null, entriesPerPage, null);
		} else if (GroupingEntity.USER.equals(groupingEntity)) {
			/*
			 * Complete the post details for the first post of a given user 
			 * (only for /<RESOURCE>/HASH/USER)
			 * 
			 * We will use the intrahash to get all details for the post using
			 * getPostDetails().
			 */
			final String intraHash;
			final List<Post<R>> posts = command.getListCommand(this.getResourceClass()).getList();
			if (present(posts)) {
				/*
				 * a post was found -> extract the publication
				 */
				intraHash = posts.get(0).getResource().getIntraHash();
			} else {
				/*
				 * No post was found: we use the requested hash to query for the
				 * post. (Note: if an interhash was requested, we won't get a
				 * post here.) 
				 */
				intraHash = shortHash;
			}
			final Post<R> post = (Post<R>) this.logic.getPostDetails(intraHash, requUser);
			/*
			 * if we did not find a post -> throw an exception
			 */
			if (!present(post)) throw new ResourceNotFoundException(intraHash);
			/*
			 * Why do we set the goldHash here again?
			 * Because at first it might have been an intra hash of a 
			 * user's post. Here we ensure, that it's the post's interhash
			 * because the intrahashes of gold standard posts are the interhashes. 
			 */
			goldHash = post.getResource().getInterHash();
			/*
			 * store the post in the command's list (and replace the original 
			 * list of post)
			 */
			command.getListCommand(this.getResourceClass()).setList(Collections.singletonList(post));
		}
		
		/*
		 * post process and sort list (e.g., insert open URL)
		 */
		this.postProcessAndSortList(command, this.getResourceClass());
		
		/*
		 * We always get the gold standard post from the database - even for
		 * user's posts - to show a link to it in the sidebar 
		 */
		Post<R> goldStandard = null;
		try {
			/*
			 * get the gold standard
			 */
			goldStandard = (Post<R>) this.logic.getPostDetails(goldHash, GOLD_STANDARD_USER_NAME);
			
			command.setGoldStandard(goldStandard);
		} catch (final ResourceNotFoundException ex) {
			// ignore
		} catch (final ResourceMovedException ex) {
			// ignore
		}
		
		R firstResource = null;
		/*
		 * if gold standard not present and list is empty, send a 404 error.
		 */
		if (present(goldStandard)) {
			firstResource = goldStandard.getResource();
		} else {
			final List<Post<R>> resourceList = command.getListCommand(this.getResourceClass()).getList();
			if (!present(resourceList)) {
				/*
				 * We throw a ResourceNotFoundException such that we don't get empty
				 * resource pages.
				 */
				throw new ResourceNotFoundException(shortHash);
			}
			firstResource = resourceList.get(0).getResource();			
		}
		
		this.endTiming();		
		return this.handleFormat(command, format, longHash, requUser, groupingEntity, goldHash, goldStandard, firstResource);
	}

	protected View handleFormat(final ResourcePageCommand<R> command, final String format, final String longHash, final String requUser, final GroupingEntity groupingEntity, final String goldHash, final Post<R> goldStandard, final R firstResource) {
		if ("html".equals(format)) {
			/*
			 * Add additional data for HTML view, e.g., tags, other user's posts, ...
			 */
			if (GroupingEntity.USER.equals(groupingEntity) || present(goldStandard)) {
				/*
				 * fetch posts of all users with the given hash, add users to related
				 * users list				
				 */
				final List<Post<R>> allPosts = this.logic.getPosts(this.getResourceClass(), GroupingEntity.ALL, null, null, firstResource.getInterHash(), null, null, 0, 1000, null);
				for (final Post<R> post : allPosts) {
					command.getRelatedUserCommand().getRelatedUsers().add(post.getUser());
				}
			}
			
			/*
			 * the gold standard contains the discussion by default
			 * if not present we have to retrieve the items here
			 */
			if (!present(goldStandard)) {
				command.setDiscussionItems(this.logic.getDiscussionSpace(goldHash));
			}

			if (GroupingEntity.USER.equals(groupingEntity)) {
				// <RESOURCE>/HASH/USER
				/* 
				 * set "correct" count .This is the number of ALL users having the publication
				 * with the interHash of firstBibtex in their collection. In allPosts, only public posts
				 * are contained, hence it can be smaller.
				 */
				this.setTotalCount(command, this.getResourceClass(), GroupingEntity.ALL, null, null, firstResource.getInterHash(), null, null, null, 1000, null);
				firstResource.setCount(command.getListCommand(this.getResourceClass()).getTotalCount());

				/*
				 * show tags by all users for this resource; the ones by the given user
				 * will be highlighted later
				 */
				this.setTags(command, this.getResourceClass(), GroupingEntity.ALL, null, null, null, longHash, TAG_LIMIT, null);
				return this.getDetailsView();
			}
			/*
			 * get only those tags, related to the resource
			 */
			this.setTags(command, this.getResourceClass(), groupingEntity, requUser, null, null, longHash, TAG_LIMIT, null);			
			return this.getResourcePage();
		}

		// export - return the appropriate view
		return Views.getViewByFormat(format);
	}

	protected abstract View getResourcePage();

	protected abstract View getDetailsView();
	
	protected abstract Class<R> getResourceClass();
}