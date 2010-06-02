package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;

/**
 * @author mwa
 * @version $Id$
 */
public class GetResourcesForHashAndUserCount extends StatisticChainElement{

	@Override
	protected List<Integer> handle(StatisticsParam param, DBSession session) {
		List<Integer> counts = new ArrayList<Integer>();
		
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForHashAndUser(BibTex.class, param.getHash(), HashID.getSimHash(param.getSimHash()), param.getRequestedUserName(), session)); 
		} else if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForHashAndUser(Bookmark.class, param.getHash(), HashID.getSimHash(param.getSimHash()), param.getRequestedUserName(), session));
		}
		return counts;
	}
	
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return (present(param.getHash()) &&
				present(param.getSimHash()) &&
				param.getGrouping() == GroupingEntity.USER &&
				present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()));
	}
}
