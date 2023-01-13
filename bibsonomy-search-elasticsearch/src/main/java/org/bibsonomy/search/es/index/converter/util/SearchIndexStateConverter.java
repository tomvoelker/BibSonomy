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
import java.util.Map;

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
	private static Date getDateForIndex(Date date) {
		if (!present(date)) {
			return new Date();
		}
		return date;
	}

	@Override
	public Map<String, Object> convert(SearchIndexState state) {
		final Map<String, Object> values = new HashMap<>();

		values.put(SearchIndexState.LAST_ENTITY_CONTENT_ID, state.getLastEntityContentId());

		final Date lastLogDate = getDateForIndex(state.getLastEntityLogDate());
		values.put(SearchIndexState.LAST_ENTITY_LOG_DATE, lastLogDate.getTime());

		values.put(SearchIndexState.MAPPING_VERSION, state.getMappingVersion());

		final Date updatedAt = getDateForIndex(state.getUpdatedAt());
		values.put(SearchIndexState.UPDATED_AT, updatedAt.getTime());

		return values;
	}

	@Override
	public SearchIndexState convert(Map<String, Object> source, Object options) {
		final SearchIndexState searchIndexState = new SearchIndexState();

		final Long dateAsTime = (Long) source.get(SearchIndexState.LAST_ENTITY_LOG_DATE);
		final Date lastLogDate = new Date(dateAsTime);
		searchIndexState.setLastEntityLogDate(lastLogDate);

		if (source.containsKey(SearchIndexState.LAST_ENTITY_CONTENT_ID)) {
			searchIndexState.setLastEntityContentId((Integer) source.get(SearchIndexState.LAST_ENTITY_CONTENT_ID));
		}

		// mapping version
		String mappingVersion = (String) source.get(SearchIndexState.MAPPING_VERSION);
		if (mappingVersion == null) {
			mappingVersion = "unknown";
		}
		searchIndexState.setMappingVersion(mappingVersion);

		return searchIndexState;
	}
}
