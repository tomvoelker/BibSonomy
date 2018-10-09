package org.bibsonomy.search.index.generator.post;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.Date;
import java.util.List;

/**
 * this generation logic queries the community posts
 *
 * @param <R>
 *
 * @author dzo
 */
public class CommunityPostIndexGenerationLogic<R extends Resource> extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<Post<R>> {

	private final Class<R> resourceClass;

	/**
	 * default constructor
	 * @param resourceClass
	 */
	public CommunityPostIndexGenerationLogic(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	@Override
	public int getNumberOfEntities() {
		try (final DBSession session = this.openSession()) {
			return saveConvertToint(this.queryForObject("get" + this.getResourceName() + "Count", Integer.class, session));
		}
	}

	private String getResourceName() {
		return this.resourceClass.getSimpleName();
	}

	@Override
	public List<Post<R>> getEntites(int lastContenId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLimit(limit);
			param.setLastContentId(lastContenId);
			return (List<Post<R>>) this.queryForList("get" + this.getResourceName() + "ForCommunityIndex", param, session);
		}
	}

	// TODO: move
	@Override
	public SearchIndexSyncState getDbState() {
		try (final DBSession session = this.openSession()) {
			final SearchIndexSyncState searchIndexSyncState = new SearchIndexSyncState();
			final ConstantID contentType = ConstantID.getContentTypeByClass(this.resourceClass);
			final int contentTypeId = contentType.getId();

			final Date lastLogDate = this.queryForObject("getLastLogDateCommunity", contentTypeId, Date.class, session);
			searchIndexSyncState.setLast_log_date(lastLogDate);

			final Integer lastContentId = this.queryForObject("getLastContentIdCommunity", contentTypeId, Integer.class, session);
			searchIndexSyncState.setLast_tas_id(lastContentId);
			return searchIndexSyncState;
		}
	}
}
