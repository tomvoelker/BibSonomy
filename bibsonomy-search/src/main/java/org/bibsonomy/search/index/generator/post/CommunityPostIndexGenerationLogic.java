package org.bibsonomy.search.index.generator.post;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

import java.util.Date;
import java.util.List;

/**
 * this generation logic queries the community posts
 *
 * @param <R>
 *
 * @author dzo
 */
public class CommunityPostIndexGenerationLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements IndexGenerationLogic<Post<R>> {

	/**
	 * default constructor
	 *
	 * @param resourceClass the resource class
	 */
	public CommunityPostIndexGenerationLogic(Class<R> resourceClass) {
		super(resourceClass);
	}


	@Override
	public int getNumberOfEntities() {
		try (final DBSession session = this.openSession()) {
			return saveConvertToint(this.queryForObject("get" + this.getResourceName() + "Count", Integer.class, session));
		}
	}

	@Override
	public List<Post<R>> getEntities(int lastContenId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLimit(limit);
			param.setLastContentId(lastContenId);
			return (List<Post<R>>) this.queryForList("get" + this.getResourceName() + "ForCommunityIndex", param, session);
		}
	}
}
