package org.bibsonomy.search.update;

/**
 * a index sync state information state for community indices
 *
 * @author dzo
 */
public class SearchCommunityIndexSyncState extends SearchIndexSyncState {

	private DefaultSearchIndexSyncState communitySearchIndexState;

	private DefaultSearchIndexSyncState normalSearchIndexState;

	/**
	 * @return the communitySearchIndexState
	 */
	public DefaultSearchIndexSyncState getCommunitySearchIndexState() {
		return communitySearchIndexState;
	}

	/**
	 * @param communitySearchIndexState the communitySearchIndexState to set
	 */
	public void setCommunitySearchIndexState(DefaultSearchIndexSyncState communitySearchIndexState) {
		this.communitySearchIndexState = communitySearchIndexState;
	}

	/**
	 * @return the normalSearchIndexState
	 */
	public DefaultSearchIndexSyncState getNormalSearchIndexState() {
		return normalSearchIndexState;
	}

	/**
	 * @param normalSearchIndexState the normalSearchIndexState to set
	 */
	public void setNormalSearchIndexState(DefaultSearchIndexSyncState normalSearchIndexState) {
		this.normalSearchIndexState = normalSearchIndexState;
	}
}
