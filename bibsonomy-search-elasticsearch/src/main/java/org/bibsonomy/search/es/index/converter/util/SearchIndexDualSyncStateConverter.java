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

import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexDualSyncState;
import org.bibsonomy.search.util.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * converter for {@link SearchIndexDualSyncState}
 *
 * @author dzo
 */
public class SearchIndexDualSyncStateConverter implements Converter<SearchIndexDualSyncState, Map<String, Object>, Object> {

	private static final String FIRST_STATE = "firstState";
	private static final String SECOND_STATE = "secondState";

	private final DefaultSearchIndexSyncStateConverter converter = new DefaultSearchIndexSyncStateConverter();

	@Override
	public Map<String, Object> convert(SearchIndexDualSyncState source) {
		final Map<String, Object> converted = new HashMap<>();

		final Map<String, Object> communityState = this.converter.convert(source.getFirstState());
		converted.put(FIRST_STATE, communityState);

		final Map<String, Object> normalState = this.converter.convert(source.getSecondState());
		converted.put(SECOND_STATE, normalState);

		converted.put(DefaultSearchIndexSyncStateConverter.MAPPING_VERSION, source.getMappingVersion());

		return converted;
	}

	@Override
	public SearchIndexDualSyncState convert(Map<String, Object> source, Object options) {
		final SearchIndexDualSyncState state = new SearchIndexDualSyncState();

		final Map<String, Object> communitySource = (Map<String, Object>) source.get(FIRST_STATE);
		final DefaultSearchIndexSyncState commnunityState = this.converter.convert(communitySource, null);
		state.setFirstState(commnunityState);

		final Map<String, Object> normalSource = (Map<String, Object>) source.get(SECOND_STATE);
		final DefaultSearchIndexSyncState normalState = this.converter.convert(normalSource, null);
		state.setSecondState(normalState);

		state.setMappingVersion((String) source.get(DefaultSearchIndexSyncStateConverter.MAPPING_VERSION));

		return state;
	}
}
