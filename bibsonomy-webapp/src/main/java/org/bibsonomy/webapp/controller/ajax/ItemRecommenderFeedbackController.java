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

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.model.RecommendationPost;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.webapp.command.ajax.AjaxItemRecommenderFeedbackCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.impl.model.RecommendedItem;
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
	
	private MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> multiplexingBibTexRecommender;
	private MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> multiplexingBookmarkRecommender;
	
	@Override
	public AjaxItemRecommenderFeedbackCommand instantiateCommand() {
		return new AjaxItemRecommenderFeedbackCommand();
	}

	@Override
	public View workOn(AjaxItemRecommenderFeedbackCommand command) {
		
		if(command.getAction().equalsIgnoreCase(ACTION_BIBTEX)) {
			List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.USER, command.getUserName(), null, command.getIntraHash(), null,SearchType.LOCAL, null, null, null, null, 0, 1);
			if(posts != null && posts.size() > 0) {
				this.multiplexingBibTexRecommender.setFeedback(new UserWrapper(command.getContext().getLoginUser()), new RecommendedItem(new RecommendationPost(posts.get(0))));
			}
		} else if(command.getAction().equalsIgnoreCase(ACTION_BOOKMARK)) {
			List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, command.getUserName(), null, command.getIntraHash(), null, SearchType.LOCAL,null, null, null, null, 0, 1);
			if(posts != null && posts.size() > 0) {
				this.multiplexingBookmarkRecommender.setFeedback(new UserWrapper(command.getContext().getLoginUser()), new RecommendedItem(new RecommendationPost(posts.get(0))));
			}
		}
		return Views.AJAX_XML;
	}

	/**
	 * @param multiplexingBibTexRecommender the multiplexing recommender for bibtex recommendations
	 */
	public void setMultiplexingBibTexRecommender(MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> multiplexingBibTexRecommender) {
		this.multiplexingBibTexRecommender = multiplexingBibTexRecommender;
	}

	/**
	 * @param multiplexingBookmarkRecommender the multiplexing recommender for bookmark recommendations
	 */
	public void setMultiplexingBookmarkRecommender(MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> multiplexingBookmarkRecommender) {
		this.multiplexingBookmarkRecommender = multiplexingBookmarkRecommender;
	}
	
	

}

