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
package org.bibsonomy.search.index.database.post;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexDualSyncState;

import java.util.Date;

/**
 * database information for community posts
 *
 * @author dzo
 */
public class CommunityPostDatabaseInformationLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements DatabaseInformationLogic<SearchIndexDualSyncState> {

	private final DatabaseInformationLogic<DefaultSearchIndexSyncState> normalPostDatabaseInformationLogic;

	/**
	 * default constructor
	 *
	 * @param resourceClass
	 * @param normalPostDatabaseInformationLogic
	 */
	public CommunityPostDatabaseInformationLogic(Class<R> resourceClass, DatabaseInformationLogic<DefaultSearchIndexSyncState> normalPostDatabaseInformationLogic) {
		super(resourceClass);
		this.normalPostDatabaseInformationLogic = normalPostDatabaseInformationLogic;
	}

	@Override
	public SearchIndexDualSyncState getDbState() {
		final SearchIndexDualSyncState searchIndexDualSyncState = new SearchIndexDualSyncState();
		searchIndexDualSyncState.setFirstState(this.queryForCommunitySearchIndexState());
		searchIndexDualSyncState.setSecondState(this.normalPostDatabaseInformationLogic.getDbState());
		return searchIndexDualSyncState;
	}

	private DefaultSearchIndexSyncState queryForCommunitySearchIndexState() {
		try (final DBSession session = this.openSession()) {
			final DefaultSearchIndexSyncState searchIndexSyncState = new DefaultSearchIndexSyncState();
			final ConstantID contentType = this.getConstantID();
			final int contentTypeId = contentType.getId();

			final Integer lastContentId = this.queryForObject("getLastContentIdCommunity", contentTypeId, Integer.class, session);
			searchIndexSyncState.setLastTasId(lastContentId);

			Integer lastPersonChangeId = this.queryForObject("getLastPersonChangeId", Integer.class, session);
			searchIndexSyncState.setLastPersonChangeId(lastPersonChangeId);

			final Date lastLogDate = this.queryForObject("getLastLogDateCommunity", contentTypeId, Date.class, session);
			searchIndexSyncState.setLastLogDate(lastLogDate);

			Date lastPersonLogDate = this.queryForObject("getLastPersonChangeLogDate", Date.class, session);
			if (!present(lastPersonLogDate)) {
				lastPersonLogDate = new Date();
			}
			searchIndexSyncState.setLastPersonLogDate(lastPersonLogDate);

			Date lastRelationLogDate = this.queryForObject("getLastRelationLogDateCommunity", Date.class, session);
			if (!present(lastRelationLogDate)) {
				lastRelationLogDate = new Date();
			}
			searchIndexSyncState.setLastRelationLogDate(lastRelationLogDate);
			return searchIndexSyncState;
		}
	}
}
