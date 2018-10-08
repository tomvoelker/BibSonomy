package org.bibsonomy.search.index.update.person;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.Date;
import java.util.List;

/**
 * implementation to get the new person resource relations and the deleted ones
 *
 * @author dzo
 */
public class PersonResourceRelationUpdateLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<ResourcePersonRelation> {

	@Override
	public List<ResourcePersonRelation> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastContentId(lastEntityId);
			param.setLastLogDate(lastLogDate);
			param.setLimit(size);
			param.setOffset(offset);
			return this.queryForList("getUpdatedOrNewPersonRelations", param, ResourcePersonRelation.class, session);
		}
	}

	@Override
	public List<ResourcePersonRelation> getDeletedEntites(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return this.queryForList("getDeletedPersonResourceRelations", lastLogDate, ResourcePersonRelation.class, session);
		}
	}

	@Override
	public SearchIndexSyncState getDbState() {
		throw new UnsupportedOperationException("use parent update logic to get the db state");
	}
}
