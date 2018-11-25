package org.bibsonomy.search.index.update.cris;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.Date;
import java.util.List;

/**
 * {@link CRISLink} update logic
 *
 * @author dzo
 */
public class CRISLinkUpdateLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexUpdateLogic<CRISLink> {

	private final CRISEntityType sourceType;
	private final CRISEntityType targetType;

	/**
	 * inits the update logic with the required {@link CRISEntityType}s
	 * @param sourceType
	 * @param targetType
	 */
	public CRISLinkUpdateLogic(CRISEntityType sourceType, CRISEntityType targetType) {
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	@Override
	public List<CRISLink> getNewerEntities(final long lastEntityId, final Date lastLogDate, final int size, final int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);
			param.setSourceType(this.sourceType);
			param.setTargetType(this.targetType);
			return this.queryForList("getUpdatedAndNewCRISLinks" + SearchParamUtils.buildResultMapID(this.sourceType, this.targetType), param, CRISLink.class, session);
		}
	}

	@Override
	public List<CRISLink> getDeletedEntities(final Date lastLogDate) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildParam(this.sourceType, this.targetType);
			param.setLastLogDate(lastLogDate);
			return this.queryForList("getDeletedCRISLinks" + SearchParamUtils.buildResultMapID(this.sourceType, this.targetType), param, CRISLink.class, session);
		}
	}
}
