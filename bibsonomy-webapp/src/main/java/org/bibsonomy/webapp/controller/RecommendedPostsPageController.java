/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.database.systemstags.SystemTagsExtractor;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.command.TagCloudCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
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
 */
public class RecommendedPostsPageController extends SingleResourceListController implements MinimalisticController<TagResourceViewCommand> {

	private Recommender<ItemRecommendationEntity, RecommendedItem> bibtexRecommender;
	private Recommender<ItemRecommendationEntity, RecommendedItem> bookmarkRecommender;
	
	private static final int TAGS_ON_SITE = 20;
	
	@Override
	public TagResourceViewCommand instantiateCommand() {
		return new TagResourceViewCommand();
	}

	@Override
	public View workOn(TagResourceViewCommand command) {
		
		// check the user logged in, only logged in users can receive recommendations
		if(!command.getContext().isUserLoggedIn()) {
			throw new org.springframework.security.access.AccessDeniedException("please log in");
		}
		
		final String format = command.getFormat();
		// no sorting, recommender gives sorted results
		command.setSortPage(null);
		
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command)) {

			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			listCommand.setStart(0);
			
			
			setList(command, command.getContext().getLoginUser(), resourceType);
			this.postProcessAndSortList(command, resourceType);
		}
		
		//initialize tag cloud by number of occurences in the recommended posts
		getTagCloud(command);
		
		this.endTiming();
		
		return Views.RECOMMENDEDPAGE;
	}
	
	/**
	 * private helper calculates a cloud with size scaled by the number of
	 * occurences of a tag in the recommended posts
	 * 
	 * @param command
	 */
	private void getTagCloud(final TagResourceViewCommand command) {
		
		final TagCloudCommand tagCloudCommand = command.getTagcloud();
		
		//count the number of occurences
		final List<Tag> tags = new ArrayList<Tag>();
		for(Post<BibTex> post : command.getBibtex().getList()) {
			for(Tag tag : post.getTags()) {
				if(!tags.contains(tag)) {
					final Tag copy = new Tag(tag);
					copy.setUsercount(1);
					tags.add(copy);
				} else {
					final Tag temp = tags.get(tags.indexOf(tag));
					temp.setUsercount(temp.getUsercount()+1);
				}
			}
		}
		for(Post<Bookmark> post : command.getBookmark().getList()) {
			for(Tag tag : post.getTags()) {
				if(!tags.contains(tag)) {
					tags.add(tag);
				} else {
					final Tag temp = tags.get(tags.indexOf(tag));
					temp.setUsercount(temp.getUsercount()+1);
				}
			}
		}
		
		tagCloudCommand.setTags(tags);
		
		tagCloudCommand.setMaxCount(TAGS_ON_SITE);
		
		// set tag cloud settings
		tagCloudCommand.setStyle(TagCloudStyle.getStyle(this.userSettings.getTagboxStyle()));
		tagCloudCommand.setSort(TagCloudSort.getSort(this.userSettings.getTagboxSort()));
		tagCloudCommand.setMaxFreq(TagUtils.getMaxUserCount(tagCloudCommand.getTags()));
	}
	
	/**
	 * private helper, triggering recommendations
	 * 
	 * @param cmd the command object
	 * @param user the requesting user
	 * @param resourceType the resourcetype to get recommendations for
	 */
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
			SystemTagsExtractor.removeHiddenSystemTags(tags);
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
