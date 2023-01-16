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

import java.util.Date;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.model.SearchIndexState;

/**
 * database information for community posts
 *
 * @author dzo
 */
public class CommunityPostDatabaseInformationLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements DatabaseInformationLogic<SearchIndexState> {

	private final DatabaseInformationLogic<SearchIndexState> normalPostDatabaseInformationLogic;

	/**
	 * default constructor
	 *
	 * @param resourceClass
	 * @param normalPostDatabaseInformationLogic
	 */
	public CommunityPostDatabaseInformationLogic(Class<R> resourceClass, DatabaseInformationLogic<SearchIndexState> normalPostDatabaseInformationLogic) {
		super(resourceClass);
		this.normalPostDatabaseInformationLogic = normalPostDatabaseInformationLogic;
	}

	@Override
	public SearchIndexState getDbState() {
		final SearchIndexState searchIndexState = this.normalPostDatabaseInformationLogic.getDbState();

		try (final DBSession session = this.openSession()) {

			final ConstantID contentType = this.getConstantID();
			final int contentTypeId = contentType.getId();

			final Integer lastCommunityContentId = this.queryForObject("getLastContentIdCommunity", contentTypeId, Integer.class, session);
			searchIndexState.setCommunityEntityId(lastCommunityContentId);

			Date lastCommunityContentDate = this.queryForObject("getLastLogDateCommunity", contentTypeId, Date.class, session);
			if (!present(lastCommunityContentDate)) {
				lastCommunityContentDate = new Date();
			}
			searchIndexState.setCommunityEntityLogDate(lastCommunityContentDate);

			final Integer lastPersonChangeId = this.queryForObject("getLastPersonChangeId", Integer.class, session);
			searchIndexState.setPersonId(lastPersonChangeId);

			Date lastRelationLogDate = this.queryForObject("getLastRelationLogDateCommunity", Date.class, session);
			if (!present(lastRelationLogDate)) {
				lastRelationLogDate = new Date();
			}
			searchIndexState.setRelationLogDate(lastRelationLogDate);
		}

		return searchIndexState;
	}

}
