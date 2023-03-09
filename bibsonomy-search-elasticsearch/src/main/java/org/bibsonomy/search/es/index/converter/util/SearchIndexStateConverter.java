/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.index.converter.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.search.model.SearchIndexError;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;

/**
 * converts the {@link SearchIndexState} to elasticsearch documents and vise versa
 *
 * @author dzo
 */
public class SearchIndexStateConverter implements Converter<SearchIndexState, Map<String, Object>, Object> {

	/**
	 * @param date the date for the index
	 * @return date
	 */
	private static long getDateForIndex(Date date) {
		if (!present(date)) {
			return new Date().getTime();
		}
		return date.getTime();
	}

	private static Date getDateFromIndex(Long timestamp) {
		return new Date(timestamp);
	}

	@Override
	public Map<String, Object> convert(SearchIndexState state) {
		final Map<String, Object> doc = new HashMap<>();

		doc.put(SearchIndexState.FIELD_INDEX_ID, state.getIndexId());
		doc.put(SearchIndexState.FIELD_MAPPING_VERSION, state.getMappingVersion());
		doc.put(SearchIndexState.FIELD_UPDATED_AT, getDateForIndex(state.getUpdatedAt()));
		doc.put(SearchIndexState.FIELD_BUILD_TIME, state.getBuildTime());
		doc.put(SearchIndexState.FIELD_ERRORS, state.getErrors()); // TODO

		doc.put(SearchIndexState.FIELD_ENTITY_ID, state.getEntityId());
		doc.put(SearchIndexState.FIELD_ENTITY_LOG_DATE, getDateForIndex(state.getEntityLogDate()));

		doc.put(SearchIndexState.FIELD_COMMUNITY_ENTITY_ID, state.getCommunityEntityId());
		doc.put(SearchIndexState.FIELD_COMMUNITY_ENTITY_LOG_DATE, getDateForIndex(state.getCommunityEntityLogDate()));

		doc.put(SearchIndexState.FIELD_TAS_ID, state.getTasId());
		doc.put(SearchIndexState.FIELD_TAS_LOG_DATE, getDateForIndex(state.getTasLogDate()));

		doc.put(SearchIndexState.FIELD_DOCUMENT_ID, state.getDocumentId());
		doc.put(SearchIndexState.FIELD_DOCUMENT_LOG_DATE, getDateForIndex(state.getDocumentLogDate()));

		doc.put(SearchIndexState.FIELD_PERSON_ID, state.getPersonId());
		doc.put(SearchIndexState.FIELD_PERSON_LOG_DATE, getDateForIndex(state.getPersonLogDate()));

		doc.put(SearchIndexState.FIELD_RELATION_ID, state.getRelationId());
		doc.put(SearchIndexState.FIELD_RELATION_LOG_DATE, getDateForIndex(state.getRelationLogDate()));

		doc.put(SearchIndexState.FIELD_PREDICTION_ID, state.getPredictionId());
		doc.put(SearchIndexState.FIELD_PREDICTION_LOG_DATE, getDateForIndex(state.getPredictionLogDate()));

		return doc;
	}

	@Override
	public SearchIndexState convert(Map<String, Object> source, Object options) {
		final SearchIndexState state = new SearchIndexState();

		state.setIndexId((String) source.get(SearchIndexState.FIELD_INDEX_ID));
		state.setUpdatedAt(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_UPDATED_AT)));
		state.setBuildTime((Integer) source.get(SearchIndexState.FIELD_BUILD_TIME));

		// mapping version
		String mappingVersion = (String) source.get(SearchIndexState.FIELD_MAPPING_VERSION);
		if (mappingVersion == null) {
			mappingVersion = SearchIndexState.UNKNOWN_VERSION;
		}
		state.setMappingVersion(mappingVersion);
		state.setErrors((List<SearchIndexError>) source.get(SearchIndexState.FIELD_ERRORS)); // TODO

		state.setEntityId((Integer) source.get(SearchIndexState.FIELD_ENTITY_ID));
		state.setEntityLogDate(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_ENTITY_LOG_DATE)));

		state.setCommunityEntityId((Integer) source.get(SearchIndexState.FIELD_COMMUNITY_ENTITY_ID));
		state.setCommunityEntityLogDate(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_COMMUNITY_ENTITY_LOG_DATE)));

		state.setTasId((Integer) source.get(SearchIndexState.FIELD_TAS_ID));
		state.setTasLogDate(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_TAS_LOG_DATE)));

		state.setDocumentId((Integer) source.get(SearchIndexState.FIELD_DOCUMENT_ID));
		state.setDocumentLogDate(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_DOCUMENT_LOG_DATE)));

		state.setPersonId((Integer) source.get(SearchIndexState.FIELD_PERSON_ID));
		state.setPersonLogDate(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_PERSON_LOG_DATE)));

		state.setRelationId((Integer) source.get(SearchIndexState.FIELD_RELATION_ID));
		state.setRelationLogDate(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_RELATION_LOG_DATE)));

		state.setPredictionId((Integer) source.get(SearchIndexState.FIELD_PREDICTION_ID));
		state.setPredictionLogDate(getDateFromIndex((Long) source.get(SearchIndexState.FIELD_PREDICTION_LOG_DATE)));

		return state;
	}
}
