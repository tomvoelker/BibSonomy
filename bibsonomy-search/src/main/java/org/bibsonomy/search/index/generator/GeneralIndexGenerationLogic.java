package org.bibsonomy.search.index.generator;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.List;

/**
 * general implementation that call the statements based on the simple name of the provided entity class
 *
 * @author dzo
 */
public class GeneralIndexGenerationLogic<T> extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<T> {

	private final Class<T> entityClass;

	/**
	 * @param entityClass
	 */
	public GeneralIndexGenerationLogic(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	private String getEntityName() {
		return this.entityClass.getSimpleName();
	}

	@Override
	public int getNumberOfEntities() {
		try (final DBSession session = this.openSession()) {
			return this.queryForObject("get" + this.getEntityName() + "sCount", Integer.class, session);
		}
	}

	@Override
	public List<T> getEntities(int lastContenId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastContentId(lastContenId);
			param.setLimit(limit);
			return this.queryForList("get" + this.getEntityName() + "s", param, this.entityClass, session);
		}
	}
}
