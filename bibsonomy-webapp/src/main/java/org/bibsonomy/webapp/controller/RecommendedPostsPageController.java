/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.command.TagCloudCommand;
import org.bibsonomy.webapp.command.TagResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import recommender.impl.multiplexer.MultiplexingRecommender;

/**
 * Controller for triggering a post recommendation and return the sorted list of recommended posts
 * 
 * @author Lukas
 */
public class RecommendedPostsPageController extends SingleResourceListController implements MinimalisticController<TagResourceViewCommand> {
	private static final int TAGS_ON_SITE = 20;
	
	private Map<Class<? extends Resource>, MultiplexingRecommender<RecommendationUser, RecommendedPost<? extends Resource>>> recommenderMap;
	
	@Override
	public TagResourceViewCommand instantiateCommand() {
		return new TagResourceViewCommand();
	}

	@Override
	public View workOn(TagResourceViewCommand command) {
		// check the user logged in, only logged in users can receive recommendations
		if (!command.getContext().isUserLoggedIn()) {
			throw new org.springframework.security.access.AccessDeniedException("please log in");
		}
		
		final String format = command.getFormat();
		// sorting by user not allowed, recommender gives sorted results
		command.setSortPage(null);
		
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {

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
	 * occurrences of a tag in the recommended posts
	 * 
	 * @param command
	 */
	private void getTagCloud(final TagResourceViewCommand command) {
		final TagCloudCommand tagCloudCommand = command.getTagcloud();
		
		final List<Tag> tags = new ArrayList<Tag>();
		
		// count the number of occurences
		for (Post<BibTex> post : command.getBibtex().getList()) {
			for (Tag tag : post.getTags()) {
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
		for (Post<Bookmark> post : command.getBookmark().getList()) {
			for (Tag tag : post.getTags()) {
				if (!tags.contains(tag)) {
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
	private <T extends Resource> void setList(SimpleResourceViewCommand cmd, User user, Class<T> resourceType) {
		
		final RecommendationUser recommendationUser = new RecommendationUser();
		recommendationUser.setUserName(user.getName());
		
		final MultiplexingRecommender<RecommendationUser, RecommendedPost<T>> recommender = getRecommenderForResourceType(resourceType);
		final SortedSet<RecommendedPost<T>> recommendationsForUser = recommender.getRecommendationsForUser(user.getName(), recommendationUser);
		final ListCommand<Post<T>> listCommand = cmd.getListCommand(resourceType);
		final List<Post<T>> posts = unwrapRecommendedItems(recommendationsForUser);
		cleanVisibleTags(posts);
		listCommand.setList(posts);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Resource> MultiplexingRecommender<RecommendationUser, RecommendedPost<T>> getRecommenderForResourceType(final Class<T> resourceType) {
		final MultiplexingRecommender<RecommendationUser, RecommendedPost<? extends Resource>> multiplexingRecommender = this.recommenderMap.get(resourceType);
		return (MultiplexingRecommender) multiplexingRecommender;
	}
	
	/**
	 * @param class1
	 * @param recommendation
	 * @return
	 */
	private static <T extends Resource> List<Post<T>> unwrapRecommendedItems(SortedSet<RecommendedPost<T>> recommendation) {
		final List<Post<T>> linkedList = new LinkedList<Post<T>>();
		for (final RecommendedPost<T> recommendedPost : recommendation) {
			linkedList.add(recommendedPost.getPost());
		}
		return linkedList;
	}

	/**
	 * Helper method to remove system tags from each given posts tags and
	 * set the visible tags.
	 * 
	 * @param posts the posts to set the cleaned visible tags for
	 */
	private static <T extends Resource> void cleanVisibleTags(Collection<Post<T>> posts) {
		for (Post<T> post : posts) {
			final Set<Tag> tags = post.getTags();
			SystemTagsExtractor.removeHiddenSystemTags(tags);
			post.setVisibleTags(tags);
		}
	}

	/**
	 * @param recommenderMap the recommenderMap to set
	 */
	public void setRecommenderMap(Map<Class<? extends Resource>, MultiplexingRecommender<RecommendationUser, RecommendedPost<? extends Resource>>> recommenderMap) {
		this.recommenderMap = recommenderMap;
	}
}
