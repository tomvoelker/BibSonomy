package org.bibsonomy.webapp.controller;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTagsExtractor;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.RecommendedPostsCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import recommender.core.Recommender;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.impl.model.RecommendedItem;

/**
 * Controller for triggering a post recommendation and return the sorted list of recommended posts
 * 
 * @author Lukas
 * @version $Id$
 */
public class RecommendedPostsPageController extends SingleResourceListController implements MinimalisticController<RecommendedPostsCommand> {

	private Recommender<ItemRecommendationEntity, RecommendedItem> bibtexRecommender;
	private Recommender<ItemRecommendationEntity, RecommendedItem> bookmarkRecommender;
	
	@Override
	public RecommendedPostsCommand instantiateCommand() {
		return new RecommendedPostsCommand();
	}

	@Override
	public View workOn(RecommendedPostsCommand command) {
		
		// check the user logged in, only logged in users can receive recommendations
		if(!command.getContext().isUserLoggedIn()) {
			throw new org.springframework.security.access.AccessDeniedException("please log in");
		}
		
		final String format = command.getFormat();
		
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {

			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			listCommand.setStart(0);
			
			
			setList(command, command.getContext().getLoginUser(), resourceType);
			
		}
		
		setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, 50, null);
		
		this.endTiming();
		
		return Views.RECOMMENDEDPAGE;
	}
	
	private <T extends Resource> void setList(SimpleResourceViewCommand cmd, User user, Class<? extends Resource> resourceType) {
		
		final ItemRecommendationEntity entity = new UserWrapper(user);
		
		if (resourceType == BibTex.class) {
			/** get publication recommendations */
			final ListCommand<Post<BibTex>> listCommand = cmd.getBibtex();
			List<Post<BibTex>> posts = RecommendationUtilities.unwrapRecommendedItems(BibTex.class, this.bibtexRecommender.getRecommendation(entity));
			this.cleanVisibleTags(posts);
			listCommand.setList(posts);
		} else if(resourceType == Bookmark.class) { 
			/** get bookmark recommendations */
			final ListCommand<Post<Bookmark>> bookmarkListCmd = cmd.getBookmark();
			List<Post<Bookmark>> posts = RecommendationUtilities.unwrapRecommendedItems(Bookmark.class, this.bookmarkRecommender.getRecommendation(entity));
			this.cleanVisibleTags(posts);
			bookmarkListCmd.setList(posts);
		}

	}
	
	/**
	 * Helper method to remove system tags from each given posts tags and
	 * set the visible tags.
	 * 
	 * @param posts the posts to set the cleaned visible tags for
	 */
	private <T extends Resource> void cleanVisibleTags(Collection<Post<T>> posts) {
		for(Post<T> post : posts) {
			final Set<Tag> tags = post.getTags();
			SystemTagsExtractor.removeAllSystemTags(tags);
			post.setVisibleTags(tags);
		}
	}

	/**
	 * @return the bibtexRecommender
	 */
	public Recommender<ItemRecommendationEntity, RecommendedItem> getBibtexRecommender() {
		return this.bibtexRecommender;
	}

	/**
	 * @param bibtexRecommender the bibtexRecommender to set
	 */
	public void setBibtexRecommender(Recommender<ItemRecommendationEntity, RecommendedItem> bibtexRecommender) {
		this.bibtexRecommender = bibtexRecommender;
	}

	/**
	 * @return the bookmarkRecommender
	 */
	public Recommender<ItemRecommendationEntity, RecommendedItem> getBookmarkRecommender() {
		return this.bookmarkRecommender;
	}

	/**
	 * @param bookmarkRecommender the bookmarkRecommender to set
	 */
	public void setBookmarkRecommender(Recommender<ItemRecommendationEntity, RecommendedItem> bookmarkRecommender) {
		this.bookmarkRecommender = bookmarkRecommender;
	}
}
