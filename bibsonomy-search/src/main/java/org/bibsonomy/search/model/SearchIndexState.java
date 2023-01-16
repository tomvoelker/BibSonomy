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
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * class for all search index sync states
 *
 * @author dzo
 */
@Getter
@Setter
public class SearchIndexState {

	public static final String UNKNOWN_VERSION = "UNKNOWN";

	public static final String FIELD_INDEX_ID = "index_id";
	public static final String FIELD_MAPPING_VERSION = "mapping_version";
	public static final String FIELD_UPDATED_AT = "updated_at";
	public static final String FIELD_ENTITY_ID = "entity_id";
	public static final String FIELD_ENTITY_LOG_DATE = "entity_log_date";
	public static final String FIELD_COMMUNITY_ENTITY_ID = "community_entity_id";
	public static final String FIELD_COMMUNITY_ENTITY_LOG_DATE = "community_entity_log_date";
	public static final String FIELD_TAS_ID = "tas_id";
	public static final String FIELD_TAS_LOG_DATE = "tas_log_date";
	public static final String FIELD_DOCUMENT_ID = "document_id";
	public static final String FIELD_DOCUMENT_LOG_DATE = "document_log_date";
	public static final String FIELD_PERSON_ID = "person_id";
	public static final String FIELD_PERSON_LOG_DATE = "person_log_date";
	public static final String FIELD_RELATION_ID = "relation_id";
	public static final String FIELD_RELATION_LOG_DATE = "relation_log_date";
	public static final String FIELD_PREDICTION_ID = "prediction_id";
	public static final String FIELD_PREDICTION_LOG_DATE = "prediction_log_date";

	public static final String FIELD_ERRORS = "errors";

	/** Name/ID of the index */
	private String indexId;
	/** Mapping version of the index (currently the project version) */
	private String mappingVersion;
	/** Timestamp when the index was last updated at */
	private Date updatedAt;
	private List<SearchIndexError> errors;

	/** Last entity ID and change log date in DB from previous index update */
	private Integer entityId;
	private Date entityLogDate;

	/** Last community entity ID and change log date in DB from previous index update */
	private Integer communityEntityId;
	private Date communityEntityLogDate;

	/** Last TAS ID and change log date in DB from previous index update */
	private Integer tasId;
	private Date tasLogDate;

	/** Last document ID and change log date in DB from previous index update */
	private Integer documentId;
	private Date documentLogDate;

	/** Last person ID and change log date in DB from previous index update */
	private Integer personId;
	private Date personLogDate;

	/** Last person-resource-relation ID and change log date in DB from previous index update */
	private Integer relationId;
	private Date relationLogDate;

	/** Last prediction ID and change log date in DB from previous index update */
	private Integer predictionId;
	private Date predictionLogDate;

	/**
	 * default constructor
	 */
	public SearchIndexState() {
		// noop
	}

	public SearchIndexState(SearchIndexState state) {
		this.indexId = state.indexId;
		this.mappingVersion = state.mappingVersion;
		this.updatedAt = state.updatedAt;
		this.errors = state.errors;

		this.entityId = state.entityId;
		this.entityLogDate = state.entityLogDate;

		this.communityEntityId = state.communityEntityId;
		this.communityEntityLogDate = state.communityEntityLogDate;

		this.tasId = state.tasId;
		this.tasLogDate = state.tasLogDate;

		this.documentId = state.documentId;
		this.documentLogDate = state.documentLogDate;

		this.personId = state.personId;
		this.personLogDate = state.personLogDate;

		this.relationId = state.relationId;
		this.relationLogDate = state.relationLogDate;

		this.predictionId = state.predictionId;
		this.predictionLogDate = state.predictionLogDate;
	}

}
