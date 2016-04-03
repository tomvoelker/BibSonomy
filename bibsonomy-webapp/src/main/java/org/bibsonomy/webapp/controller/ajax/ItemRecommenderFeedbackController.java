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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.webapp.command.ajax.AjaxItemRecommenderFeedbackCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import recommender.impl.multiplexer.MultiplexingRecommender;

/**
 * This controller is called if a resource on the recommended posts page
 * was clicked (only positive clicks like the resource link or copy to clipboard).
 * 
 * @author lukas
 */
public class ItemRecommenderFeedbackController extends AjaxController implements MinimalisticController<AjaxItemRecommenderFeedbackCommand>{
	private static final String ACTION_BOOKMARK = "bookmark";
	private static final String ACTION_BIBTEX = "bibtex";
	
	
	private MultiplexingRecommender<RecommendationUser, RecommendedPost<BibTex>> multiplexingBibTexRecommender;
	private MultiplexingRecommender<RecommendationUser, RecommendedPost<Bookmark>> multiplexingBookmarkRecommender;
	
	@Override
	public AjaxItemRecommenderFeedbackCommand instantiateCommand() {
		return new AjaxItemRecommenderFeedbackCommand();
	}

	@Override
	public View workOn(final AjaxItemRecommenderFeedbackCommand command) {
		final String loggedInUserName = command.getContext().getLoginUser().getName();
		final RecommendationUser recommendationUser = new RecommendationUser();
		recommendationUser.setUserName(loggedInUserName);
		// TODO: why not getpostDetails?
		if (command.getAction().equalsIgnoreCase(ACTION_BIBTEX)) {
			List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.USER, command.getUserName(), null, command.getIntraHash(), null,SearchType.LOCAL, null, null, null, null, 0, 1);
			if (present(posts)) {
				RecommendedPost<BibTex> result = new RecommendedPost<BibTex>();
				result.setPost(posts.get(0));
				this.multiplexingBibTexRecommender.setFeedback(loggedInUserName, recommendationUser, result);
			}
		} else if (command.getAction().equalsIgnoreCase(ACTION_BOOKMARK)) {
			List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, command.getUserName(), null, command.getIntraHash(), null, SearchType.LOCAL, null, null, null, null, 0, 1);
			if (present(posts)) {
				RecommendedPost<Bookmark> result = new RecommendedPost<Bookmark>();
				result.setPost(posts.get(0));
				this.multiplexingBookmarkRecommender.setFeedback(loggedInUserName, recommendationUser, result);
			}
		}
		return Views.AJAX_XML;
	}

	/**
	 * @param multiplexingBibTexRecommender the multiplexingBibTexRecommender to set
	 */
	public void setMultiplexingBibTexRecommender(MultiplexingRecommender<RecommendationUser, RecommendedPost<BibTex>> multiplexingBibTexRecommender) {
		this.multiplexingBibTexRecommender = multiplexingBibTexRecommender;
	}

	/**
	 * @param multiplexingBookmarkRecommender the multiplexingBookmarkRecommender to set
	 */
	public void setMultiplexingBookmarkRecommender(MultiplexingRecommender<RecommendationUser, RecommendedPost<Bookmark>> multiplexingBookmarkRecommender) {
		this.multiplexingBookmarkRecommender = multiplexingBookmarkRecommender;
	}
}

