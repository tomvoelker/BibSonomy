package org.bibsonomy.database.managers.metadata;

import org.bibsonomy.model.logic.query.statistics.meta.MetaDataQuery;

/**
 * metadata provider
 *
 * @param <R>
 */
public interface MetaDataProvider<R> {

	/**
	 *
	 * @return the meta data
	 */
	R getMetaData(MetaDataQuery<R> query);
}
