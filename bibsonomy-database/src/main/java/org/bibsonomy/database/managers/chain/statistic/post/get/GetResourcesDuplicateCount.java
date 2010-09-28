package org.bibsonomy.database.managers.chain.statistic.post.get;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetResourcesDuplicateCount extends StatisticChainElement {

	@Override
	protected Integer handle(StatisticsParam param, DBSession session) {
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return this.db.getNumberOfDuplicates(BibTex.class, param.getRequestedUserName(), session);
		}
		
		throw new UnsupportedResourceTypeException("Resource type not supported for this query.");
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	param.getGrouping() == GroupingEntity.USER &&
				param.getFilter() == FilterEntity.DUPLICATES;
	}
}