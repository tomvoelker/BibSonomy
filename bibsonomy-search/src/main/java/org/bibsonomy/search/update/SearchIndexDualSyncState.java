package org.bibsonomy.search.update;

/**
 * a index sync state information state for community indices
 *
 * @author dzo
 */
public class SearchIndexDualSyncState extends SearchIndexSyncState {

	private DefaultSearchIndexSyncState firstState;

	private DefaultSearchIndexSyncState secondState;

	/**
	 * @return the firstState
	 */
	public DefaultSearchIndexSyncState getFirstState() {
		return firstState;
	}

	/**
	 * @param firstState the firstState to set
	 */
	public void setFirstState(DefaultSearchIndexSyncState firstState) {
		this.firstState = firstState;
	}

	/**
	 * @return the secondState
	 */
	public DefaultSearchIndexSyncState getSecondState() {
		return secondState;
	}

	/**
	 * @param secondState the secondState to set
	 */
	public void setSecondState(DefaultSearchIndexSyncState secondState) {
		this.secondState = secondState;
	}
}
