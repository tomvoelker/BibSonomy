package org.bibsonomy.database.managers.metadata;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.statistics.meta.MetaDataQuery;

/**
 * metadata provider
 *
 * @param <R>
 */
public interface MetaDataProvider<R> {

	/**
	 * @param loggedinUser
	 * @param query
	 *
	 * @return the meta data
	 */
	R getMetaData(User loggedinUser, MetaDataQuery<R> query);
}
