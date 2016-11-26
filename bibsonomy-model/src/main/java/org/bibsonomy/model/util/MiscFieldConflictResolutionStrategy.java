package org.bibsonomy.model.util;

/**
 * a conflict resolution strategy for syncing {@link org.bibsonomy.model.BibTex#misc} with
 * the {@link org.bibsonomy.model.BibTex#miscFields} map
 *
 * @author dzo
 */
public interface MiscFieldConflictResolutionStrategy {

	/** a {@link MiscFieldConflictResolutionStrategy} where the misc field value wins */
	public static final MiscFieldConflictResolutionStrategy MISC_FIELD_WINS = new MiscFieldConflictResolutionStrategy() {
		@Override
		public String resoloveConflict(String key, String miscFieldValue, String miscFieldMapValue) {
			return miscFieldValue;
		}
	};

	/** a {@link MiscFieldConflictResolutionStrategy} where the misc field map value wins */
	public static final MiscFieldConflictResolutionStrategy MISC_FIELD_MAP_WINS = new MiscFieldConflictResolutionStrategy() {
		@Override
		public String resoloveConflict(String key, String miscFieldValue, String miscFieldMapValue) {
			return miscFieldMapValue;
		}
	};

	/**
	 * resolve the conflict while syncing the misc string field and the misc field map of a {@link org.bibsonomy.model.BibTex}
	 * @param key the key of the entry
	 * @param miscFieldValue the value of the misc field
	 * @param miscFieldMapValue the value of the misc field map
	 * @return the resolved value
	 */
	public String resoloveConflict(String key, String miscFieldValue, String miscFieldMapValue);
}
