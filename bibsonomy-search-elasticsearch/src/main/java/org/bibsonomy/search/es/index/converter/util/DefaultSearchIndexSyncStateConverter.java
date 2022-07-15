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

import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * converts the {@link DefaultSearchIndexSyncState} to elasticsearch documents and vise versa
 *
 * @author dzo
 */
public class DefaultSearchIndexSyncStateConverter implements Converter<DefaultSearchIndexSyncState, Map<String, Object>, Object> {

	private static final String LAST_TAS_KEY = "last_tas_id";
	private static final String LAST_LOG_DATE_KEY = "last_log_date";
	private static final String LAST_PERSON_LOG_DATE = "last_person_log_date";
	private static final String LAST_RELATION_LOG_DATE = "last_relation_log_date";
	private static final String LAST_DOCUMENT_DATE_KEY = "last_document_date";
	private static final String LAST_PREDICTION_CHANGE_DATE = "lastPredictionChangeDate";
	private static final String LAST_POST_CONTENT_ID_KEY = "last_post_content_id";
	private static final String LAST_PERSON_CHANGE_ID_KEY = "last_person_change_id";
	protected static final String MAPPING_VERSION = "mapping_version";

	/**
	 * @param date the date for the index
	 * @return
	 */
	private static Date getDateForIndex(Date date) {
		if (!present(date)) {
			return new Date();
		}
		return date;
	}

	@Override
	public Map<String, Object> convert(DefaultSearchIndexSyncState state) {
		final Map<String, Object> values = new HashMap<>();
		values.put(LAST_TAS_KEY, state.getLastTasId());

		final Date lastLogDate = getDateForIndex(state.getLastLogDate());
		values.put(LAST_LOG_DATE_KEY, lastLogDate.getTime());

		final Date lastPersonLogDate = state.getLastPersonLogDate();
		if (present(lastPersonLogDate)) {
			values.put(LAST_PERSON_LOG_DATE, lastPersonLogDate.getTime());
		}

		final Date lastRelationLogDate = state.getLastRelationLogDate();
		if (present(lastRelationLogDate)) {
			values.put(LAST_RELATION_LOG_DATE, lastRelationLogDate.getTime());
		}

		final Date lastDocumentDate = getDateForIndex(state.getLastDocumentDate());
		values.put(LAST_DOCUMENT_DATE_KEY, lastDocumentDate.getTime());

		final Date lastPredictionDate = state.getLastPredictionChangeDate();
		if (present(lastPredictionDate)) {
			values.put(LAST_PREDICTION_CHANGE_DATE, lastPredictionDate.getTime());
		}

		values.put(LAST_POST_CONTENT_ID_KEY, state.getLastPostContentId());
		values.put(LAST_PERSON_CHANGE_ID_KEY, state.getLastPersonChangeId());
		values.put(MAPPING_VERSION, state.getMappingVersion());

		return values;
	}

	@Override
	public DefaultSearchIndexSyncState convert(Map<String, Object> source, Object options) {
		final DefaultSearchIndexSyncState searchIndexState = new DefaultSearchIndexSyncState();
		searchIndexState.setLastTasId((Integer) source.get(LAST_TAS_KEY));

		final Long dateAsTime = (Long) source.get(LAST_LOG_DATE_KEY);
		final Date lastLogDate = new Date(dateAsTime);
		searchIndexState.setLastLogDate(lastLogDate);

		final Long lastPersonLogDateTime = (Long) source.get(LAST_PERSON_LOG_DATE);
		if (present(lastPersonLogDateTime)) {
			searchIndexState.setLastPersonLogDate(new Date(lastPersonLogDateTime));
		}

		final Long lastRelationLogDate = (Long) source.get(LAST_RELATION_LOG_DATE);
		if (present(lastRelationLogDate)) {
			searchIndexState.setLastRelationLogDate(new Date(lastRelationLogDate));
		}

		final Long documentDateAsTime = (Long) source.get(LAST_DOCUMENT_DATE_KEY);
		final Date lastDocumentDate;
		if (present(documentDateAsTime)) {
			lastDocumentDate = new Date(documentDateAsTime);
		} else {
			lastDocumentDate = null;
		}
		searchIndexState.setLastDocumentDate(lastDocumentDate);

		final Long predictionChangeDateAsTime = (Long) source.get(LAST_PREDICTION_CHANGE_DATE);
		final Date predictionChangeDate;
		if (present(predictionChangeDateAsTime)) {
			predictionChangeDate = new Date(predictionChangeDateAsTime);
		} else {
			// the change date was the last log date
			predictionChangeDate = lastLogDate;
		}
		searchIndexState.setLastPredictionChangeDate(predictionChangeDate);

		if (source.containsKey(LAST_POST_CONTENT_ID_KEY)) {
			searchIndexState.setLastPostContentId((Integer) source.get(LAST_POST_CONTENT_ID_KEY));
		}
		searchIndexState.setLastPersonChangeId(((Integer) source.get(LAST_PERSON_CHANGE_ID_KEY)).longValue());

		// mapping version
		String mappingVersion = (String) source.get(MAPPING_VERSION);
		if (mappingVersion == null) {
			mappingVersion = "unknown";
		}
		searchIndexState.setMappingVersion(mappingVersion);
		return searchIndexState;
	}
}
