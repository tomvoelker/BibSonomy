package org.bibsonomy.search.index.update;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

/**
 * general index update logic
 *
 * @author dzo
 */
public class GeneralIndexUpdateLogic<T> extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<T> {

	private final Class<T> entityClass;

	/**
	 * default constructor
	 * @param entityClass
	 */
	public GeneralIndexUpdateLogic(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	private String getEntityName() {
		return this.entityClass.getSimpleName();
	}

	@Override
	public List<T> getNewerEntities(final long lastEntityId, final Date lastLogDate, final int size, final int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);

			return this.queryForList("getUpdatedAndNew" + this.getEntityName() + "s", param, this.entityClass, session);
		}
	}

	@Override
	public List<T> getDeletedEntities(final Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getDeleted" + this.getEntityName() + "s", lastLogDate, this.entityClass, session);
		}
	}
}
