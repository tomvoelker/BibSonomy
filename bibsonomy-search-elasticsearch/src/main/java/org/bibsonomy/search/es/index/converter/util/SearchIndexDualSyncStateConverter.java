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

		final Map<String, Object> normalState = this.converter.convert(source.getSecondState());
		converted.put(FIRST_STATE, normalState);

		final Map<String, Object> communityState = this.converter.convert(source.getFirstState());
		converted.put(SECOND_STATE, communityState);

		converted.put(DefaultSearchIndexSyncStateConverter.MAPPING_VERSION, source.getMappingVersion());

		return converted;
	}

	@Override
	public SearchIndexDualSyncState convert(Map<String, Object> source, Object options) {
		final SearchIndexDualSyncState state = new SearchIndexDualSyncState();

		final Map<String, Object> normalSource = (Map<String, Object>) source.get(FIRST_STATE);
		final DefaultSearchIndexSyncState normalState = this.converter.convert(normalSource, null);
		state.setSecondState(normalState);

		final Map<String, Object> communitySource = (Map<String, Object>) source.get(SECOND_STATE);
		final DefaultSearchIndexSyncState commnuityState = this.converter.convert(communitySource, null);
		state.setFirstState(commnuityState);

		state.setMappingVersion((String) source.get(DefaultSearchIndexSyncStateConverter.MAPPING_VERSION));

		return state;
	}
}
