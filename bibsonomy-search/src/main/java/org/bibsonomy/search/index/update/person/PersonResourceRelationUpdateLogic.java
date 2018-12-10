package org.bibsonomy.search.index.update.person;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.Date;
import java.util.List;

/**
 * implementation to get the new person resource relations and the deleted ones
 *
 * @author dzo
 */
public class PersonResourceRelationUpdateLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<ResourcePersonRelation> {

	private final boolean includeRelatedEntityUpdates;

	/**
	 * constructor that sets if the related entity updates should be considered by returning new entities
	 * @param includeRelatedEntityUpdates
	 */
	public PersonResourceRelationUpdateLogic(final boolean includeRelatedEntityUpdates) {
		this.includeRelatedEntityUpdates = includeRelatedEntityUpdates;
	}

	@Override
	public List<ResourcePersonRelation> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);
			param.setIncludeRelatedEntityUpdates(this.includeRelatedEntityUpdates);
			return this.queryForList("getUpdatedOrNewPersonRelations", param, ResourcePersonRelation.class, session);
		}
	}

	@Override
	public List<ResourcePersonRelation> getDeletedEntities(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getDeletedPersonResourceRelations", lastLogDate, ResourcePersonRelation.class, session);
		}
	}
}
