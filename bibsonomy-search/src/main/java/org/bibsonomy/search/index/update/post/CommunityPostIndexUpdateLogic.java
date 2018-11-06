package org.bibsonomy.search.index.update.post;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

import java.util.Date;
import java.util.List;

/**
 * index update logic for normal posts for the community logic
 * @param <R>
 *
 * @author dzo
 */
public class CommunityPostIndexUpdateLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements IndexUpdateLogic<Post<R>> {

	/**
	 * default constructor
	 *
	 * @param resourceClass the resource class
	 */
	public CommunityPostIndexUpdateLogic(final Class<R> resourceClass) {
		super(resourceClass, true);
	}

	/**
	 * default constructor
	 * @param resourceClass
	 * @param useSuperiorResourceClass
	 */
	public CommunityPostIndexUpdateLogic(Class<R> resourceClass, boolean useSuperiorResourceClass) {
		super(resourceClass, useSuperiorResourceClass);
	}

	@Override
	public List<Post<R>> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastContentId(lastEntityId);
			param.setLastLogDate(lastLogDate);
			param.setLimit(size);
			param.setOffset(offset);
			return (List<Post<R>>) this.queryForList("getNew" + this.getResourceName() + "Posts", param, session);
		}
	}

	@Override
	public List<Post<R>> getDeletedEntities(Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			return (List<Post<R>>) this.queryForList("getDeleted" + this.getResourceName() + "Posts", lastLogDate, session);
		}
	}
}
