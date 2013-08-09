package org.bibsonomy.webapp.controller;

import java.util.ArrayList;
import java.util.SortedSet;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.RecommendedPostsCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import recommender.core.Recommender;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendedItem;

/**
 * Controller for triggering a post recommendation and return the sorted list of recommended posts
 * 
 * @author Lukas
 * @version $Id$
 */
public class RecommendedPostsPageController extends SingleResourceListController implements MinimalisticController<RecommendedPostsCommand>{

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
			
			
			setList(command, command.getContext().getLoginUser());
			
		}
		
		setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, 50, null);
		
		this.endTiming();
		
		return Views.RECOMMENDEDPAGE;
	}
	
	private <T extends Resource> void setList(SimpleResourceViewCommand cmd, User user) {
		
		/** get post recommendations */
		SortedSet<RecommendedItem> result = bibtexRecommender.getRecommendation(new UserWrapper(user));
		final ListCommand<Post<BibTex>> listCommand = cmd.getBibtex();
		ArrayList<Post<BibTex>> posts = new ArrayList<Post<BibTex>>();
		
		for(RecommendedItem item : result) {
			posts.addAll(this.logic.getPosts(BibTex.class, GroupingEntity.USER, item.getId().split("-")[1], null, item.getId().split("-")[0], "", cmd.getFilter(), null, null, null, 0, 10));
		}
		
		listCommand.setList(posts);
		
		
		/** get bookmark recommendations */
		SortedSet<RecommendedItem> resultBookmark = bookmarkRecommender.getRecommendation(new UserWrapper(user));
		final ListCommand<Post<Bookmark>> bookmarkListCmd = cmd.getBookmark();
		ArrayList<Post<Bookmark>> bookmarkPosts = new ArrayList<Post<Bookmark>>();
		
		for(RecommendedItem item : resultBookmark) {
			bookmarkPosts.addAll(this.logic.getPosts(Bookmark.class, GroupingEntity.USER, item.getId().split("-")[1], null, item.getId().split("-")[0], "", cmd.getFilter(), null, null, null, 0, 10));
		}
		
		bookmarkListCmd.setList(bookmarkPosts);

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
