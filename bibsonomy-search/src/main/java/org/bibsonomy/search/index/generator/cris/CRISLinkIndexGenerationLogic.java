package org.bibsonomy.search.index.generator.cris;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.List;

/**
 * generation logic for a CRISLink
 *
 * @author dzo
 */
public class CRISLinkIndexGenerationLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<CRISLink> {

	private final CRISEntityType sourceType;
	private final CRISEntityType targetType;

	/**
	 * @param sourceType
	 * @param targetType
	 */
	public CRISLinkIndexGenerationLogic(final CRISEntityType sourceType, final CRISEntityType targetType) {
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	@Override
	public int getNumberOfEntities() {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildParam(this.sourceType, this.targetType);
			return this.queryForObject("getCRISLinksCount", param, Integer.class, session);
		}
	}

	@Override
	public List<CRISLink> getEntities(int lastContenId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildParam(this.sourceType, this.targetType);
			param.setLastContentId(lastContenId);
			param.setLimit(limit);

			return this.queryForList("getCRISLinks" + SearchParamUtils.buildResultMapID(this.sourceType, this.targetType), param, CRISLink.class, session);
		}
	}

}
