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

import org.bibsonomy.search.model.SearchIndexDualState;
import org.bibsonomy.search.model.SearchIndexState;
import org.bibsonomy.search.util.Converter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * converter for {@link SearchIndexDualState}
 *
 * @author dzo
 */
public class SearchIndexDualStateConverter implements Converter<SearchIndexDualState, Map<String, Object>, Object> {

	private final SearchIndexStateConverter converter = new SearchIndexStateConverter();

	@Override
	public Map<String, Object> convert(SearchIndexDualState state) {
		final Map<String, Object> doc = new HashMap<>();

		doc.put(SearchIndexState.FIELD_INDEX_ID, state.getIndexId());
		doc.put(SearchIndexState.FIELD_MAPPING_VERSION, state.getMappingVersion());
		if (!present(state.getUpdatedAt())) {
			doc.put(SearchIndexState.FIELD_UPDATED_AT, new Date().getTime());
		} else {
			doc.put(SearchIndexState.FIELD_UPDATED_AT, state.getUpdatedAt().getTime());
		}

		final Map<String, Object> communityState = this.converter.convert(state.getFirstState());
		doc.put(SearchIndexDualState.FIELD_FIRST_STATE, communityState);

		final Map<String, Object> normalState = this.converter.convert(state.getSecondState());
		doc.put(SearchIndexDualState.FIELD_SECOND_STATE, normalState);

		return doc;
	}

	@Override
	public SearchIndexDualState convert(Map<String, Object> source, Object options) {
		final SearchIndexDualState state = new SearchIndexDualState();

		state.setIndexId((String) source.get(SearchIndexState.FIELD_INDEX_ID));
		state.setUpdatedAt(new Date((Long) source.get(SearchIndexState.FIELD_UPDATED_AT)));

		// mapping version
		String mappingVersion = (String) source.get(SearchIndexState.FIELD_MAPPING_VERSION);
		if (mappingVersion == null) {
			mappingVersion = SearchIndexState.UNKNOWN_VERSION;
		}
		state.setMappingVersion(mappingVersion);

		final Map<String, Object> communitySource = (Map<String, Object>) source.get(SearchIndexDualState.FIELD_FIRST_STATE);
		final SearchIndexState commnunityState = this.converter.convert(communitySource, null);
		state.setFirstState(commnunityState);

		final Map<String, Object> normalSource = (Map<String, Object>) source.get(SearchIndexDualState.FIELD_SECOND_STATE);
		final SearchIndexState normalState = this.converter.convert(normalSource, null);
		state.setSecondState(normalState);

		return state;
	}
}
