/**
 * BibSonomy Search - Helper classes for search modules.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.search.index.update.cris;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.model.SearchParam;

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
	public CRISLinkUpdateLogic(final CRISEntityType sourceType, final CRISEntityType targetType) {
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
