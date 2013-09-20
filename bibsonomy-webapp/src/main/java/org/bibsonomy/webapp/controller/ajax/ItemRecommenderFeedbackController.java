package org.bibsonomy.webapp.controller.ajax;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
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
 * @version $Id$
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
			List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.USER, command.getUserName(), null, command.getIntraHash(), null, null, null, null, null, 0, 1);
			if(posts != null && posts.size() > 0) {
				this.multiplexingBibTexRecommender.setFeedback(new UserWrapper(command.getContext().getLoginUser()), new RecommendedItem(new RecommendationPost(posts.get(0))));
			}
		} else if(command.getAction().equalsIgnoreCase(ACTION_BOOKMARK)) {
			List<Post<Bookmark>> posts = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, command.getUserName(), null, command.getIntraHash(), null, null, null, null, null, 0, 1);
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

