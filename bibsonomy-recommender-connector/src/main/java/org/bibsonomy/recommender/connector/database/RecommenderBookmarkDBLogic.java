package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.impl.SqlMapClientDBSessionFactory;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.recommender.connector.database.params.DummyRecommendationRequestParam;
import org.bibsonomy.recommender.connector.database.params.RecommendationBookmarkParam;
import org.bibsonomy.recommender.connector.model.RecommendedPost;

import recommender.core.database.RecommenderDBSession;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

public class RecommenderBookmarkDBLogic extends RecommenderDBLogic {
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@Override
	public List<RecommendationItem> getMostActualItems(int count, final ItemRecommendationEntity entity) {
		
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final DummyRecommendationRequestParam param = new DummyRecommendationRequestParam();
			param.setCount(count);
			param.setRequestingUserName(entity.getUserName());
			List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getMostActualBookmark", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			for(Post<Bookmark> bookmark : results) {
				RecommendationItem item =  new RecommendedPost<Bookmark>(bookmark);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
	}
	
}
