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
package org.bibsonomy.search.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;

/**
 * class for all search index sync states
 *
 * @author dzo
 */
@Getter
@Setter
public class SearchIndexState {

	public static final String LAST_ENTITY_LOG_DATE = "last_entity_log_date";

	public static final String LAST_ENTITY_CONTENT_ID = "last_entity_content_id";

	public static final String LAST_TAS_ID = "last_tas_id";

	public static final String LAST_DOCUMENT_DATE = "last_document_date";

	public static final String LAST_PREDICTION_DATE = "last_prediction_date";

	//public static final String LAST_PERSON_LOG_DATE = "last_person_log_date";
	//public static final String LAST_RELATION_LOG_DATE = "last_relation_log_date";
	//public static final String LAST_PERSON_CHANGE_ID_KEY = "last_person_change_id";

	public static final String MAPPING_VERSION = "mapping_version";

	public static final String UPDATED_AT = "updated_at";

	/** Mapping version of the index (currently the project version) */
	protected String mappingVersion;

	/** Timestamp when the index was last updated at */
	protected Date updatedAt;

	/** Timestamp when the last CREATED/DELETED entity was logged in the database corresponding the type of the index */
	protected Integer lastEntityContentId;

	/** Timestamp when the last DELETED entity was logged in the database corresponding the type of the index */
	protected Date lastEntityLogDate;

	/** Last logged TAS ID */
	protected Integer lastTasId;

	/** Last logged document date */
	protected Date lastDocumentDate;

	/** Last logged prediction date */
	protected Date lastPredictionDate;

	/** Last logged person ID to a person-resource relation */
	protected Integer lastPersonChangeId;

	/** Last timestamp when the last person-resource relation was changed */
	protected Date lastRelationChangeDate;

	/** Last timestamp when the last CREATED/DELETED community entity was logged in the database corresponding the type of the index */
	protected Integer lastCommunityEntityContentId;

	/** Last timestamp when the last DELETED community entity was logged in the database corresponding the type of the index */
	protected Date lastCommunityEntityLogDate;

	/**
	 * default constructor
	 */
	public SearchIndexState() {
		// noop
	}

	public SearchIndexState(SearchIndexState state) {
		this.lastEntityContentId = state.lastEntityContentId;
		this.lastEntityLogDate = state.lastEntityLogDate;
		this.mappingVersion = state.mappingVersion;
		this.updatedAt = state.updatedAt;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.lastEntityContentId) + ObjectUtils.hashCode(this.lastEntityLogDate) + ObjectUtils.hashCode(this.mappingVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SearchIndexState)) {
			return false;
		}

		final SearchIndexState otherState = (SearchIndexState) obj;

		if (!ObjectUtils.equals(this.lastEntityContentId, otherState.lastEntityContentId)) {
			return false;
		}

		if (!ObjectUtils.equals(this.lastEntityLogDate, otherState.lastEntityLogDate)) {
			return false;
		}

		if (!ObjectUtils.equals(this.mappingVersion, otherState.mappingVersion)) {
			return false;
		}

		if (!ObjectUtils.equals(this.updatedAt, otherState.updatedAt)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + this.lastEntityContentId + ", " + this.lastEntityLogDate + ", " + this.mappingVersion + ", " + this.updatedAt + "]";
	}
}
