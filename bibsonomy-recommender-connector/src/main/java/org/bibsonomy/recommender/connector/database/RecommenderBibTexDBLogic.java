package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.impl.SqlMapClientDBSessionFactory;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.recommender.connector.database.params.DummyRecommendationRequestParam;
import org.bibsonomy.recommender.connector.database.params.RecommendationBibTexParam;
import org.bibsonomy.recommender.connector.model.RecommendedPost;
import org.bibsonomy.recommender.connector.model.ResourceWrapper;
import org.bibsonomy.recommender.connector.model.UserWrapper;

import recommender.core.database.RecommenderDBSession;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendedItem;
import recommender.impl.multiplexer.MultiplexingRecommender;

/**
 * 
 * This class implements the database access on the bibsonomy database
 *  for the recommendation library
 * 
 * @author Lukas
 *
 */

public class RecommenderBibTexDBLogic extends RecommenderDBLogic{
			
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
			List<Post<BibTex>> results = (List<Post<BibTex>>) this.queryForList("getMostActualBibTex", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			for(Post<BibTex> bibtex : results) {
				RecommendationItem item =  new RecommendedPost<BibTex>(bibtex);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
		
	}

}
