package org.bibsonomy.webapp.util;

/**
 * methods for tag clouds / lists
 *
 * @author dzo
 */
public final class TagViewUtils {
	private TagViewUtils() {
		// noop
	}
	
	/*
	 * used by computeTagFontSize.
	 * 
	 * - scalingFactor: Controls difference between smallest and largest tag
	 * (size of largest: 90 -> 200% font size; 40 -> ~170%; 20 -> ~150%; all for
	 * offset = 10) - offset: controls size of smallest tag ( 10 -> 100%) -
	 * default: default tag size returned in case of an error during computation
	 */
	private static final int TAGCLOUD_SIZE_SCALING_FACTOR = 45;
	private static final int TAGCLOUD_SIZE_OFFSET = 10;
	private static final int TAGCLOUD_SIZE_DEFAULT = 100;
	
	/**
	 * Computes font size for given tag frequency and maximum tag frequency
	 * inside tag cloud.
	 * 
	 * This is used as attribute font-size=X%. We expect 0 < tagMinFrequency <=
	 * tagFrequency <= tagMaxFrequency. We return a value between 200 and 300 if
	 * tagsizemode=popular, and between 100 and 200 otherwise.
	 * 
	 * @param tagFrequency
	 *        - the frequency of the tag
	 * @param tagMinFrequency
	 *        - the minimum frequency within the tag cloud
	 * @param tagMaxFrequency
	 *        - the maximum frequency within the tag cloud
	 * @param tagSizeMode
	 *        - which kind of tag cloud is to be done (the one for the
	 *        popular tags page vs. standard)
	 * @return font size for the tag cloud with the given parameters
	 */
	public static Integer computeTagFontsize(final Integer tagFrequency, final Integer tagMinFrequency, final Integer tagMaxFrequency, final String tagSizeMode) {
		try {
			Double size = ((tagFrequency.doubleValue() - tagMinFrequency) / (tagMaxFrequency - tagMinFrequency)) * TAGCLOUD_SIZE_SCALING_FACTOR;
			if ("popular".equals(tagSizeMode)) {
				size *= 10;
			}
			size += TAGCLOUD_SIZE_OFFSET;
			size = Math.log10(size);
			size *= 100;
			return size.intValue() == 0 ? TAGCLOUD_SIZE_DEFAULT : size.intValue();
		} catch (final Exception ex) {
			return TAGCLOUD_SIZE_DEFAULT;
		}
	}
	
	/**
	 * returns the css Class for a given tag
	 * 
	 * @param tagCount
	 *        the count aof the current Tag
	 * @param maxTagCount
	 *        the maximum tag count
	 * @return the css class for the tag
	 */
	public static String getTagSize(final Integer tagCount, final Integer maxTagCount) {
		/*
		 * catch incorrect values
		 */
		if ((tagCount == 0) || (maxTagCount == 0)) {
			return "tagtiny";
		}

		final int percentage = ((tagCount * 100) / maxTagCount);

		if (percentage < 25) {
			return "tagtiny";
		} else if ((percentage >= 25) && (percentage < 50)) {
			return "tagnormal";
		} else if ((percentage >= 50) && (percentage < 75)) {
			return "taglarge";
		} else if (percentage >= 75) {
			return "taghuge";
		}

		return "";
	}
}
