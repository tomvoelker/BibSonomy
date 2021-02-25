package org.bibsonomy.search.es.index.converter.util;

import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchCommunityIndexSyncState;
import org.bibsonomy.search.util.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * converter for {@link SearchCommunityIndexSyncState}
 *
 * @author dzo
 */
public class SearchCommunityIndexSyncStateConverter implements Converter<SearchCommunityIndexSyncState, Map<String, Object>, Object> {

	private static final String NORMAL = "normalState";
	private static final String COMMUNITY = "communityState";

	private final DefaultSearchIndexSyncStateConverter converter = new DefaultSearchIndexSyncStateConverter();

	@Override
	public Map<String, Object> convert(SearchCommunityIndexSyncState source) {
		final Map<String, Object> converted = new HashMap<>();

		final Map<String, Object> normalState = this.converter.convert(source.getNormalSearchIndexState());
		converted.put(NORMAL, normalState);

		final Map<String, Object> communityState = this.converter.convert(source.getCommunitySearchIndexState());
		converted.put(COMMUNITY, communityState);

		converted.put(DefaultSearchIndexSyncStateConverter.MAPPING_VERSION, source.getMappingVersion());

		return converted;
	}

	@Override
	public SearchCommunityIndexSyncState convert(Map<String, Object> source, Object options) {
		final SearchCommunityIndexSyncState state = new SearchCommunityIndexSyncState();

		final Map<String, Object> normalSource = (Map<String, Object>) source.get(NORMAL);
		final DefaultSearchIndexSyncState normalState = this.converter.convert(normalSource, null);
		state.setNormalSearchIndexState(normalState);

		final Map<String, Object> communitySource = (Map<String, Object>) source.get(COMMUNITY);
		final DefaultSearchIndexSyncState commnuityState = this.converter.convert(communitySource, null);
		state.setCommunitySearchIndexState(commnuityState);

		state.setMappingVersion((String) source.get(DefaultSearchIndexSyncStateConverter.MAPPING_VERSION));

		return state;
	}
}
