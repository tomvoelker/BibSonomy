package org.bibsonomy.lucene.param;

public class LuceneResourceIndexInfo {

	/**
	 * The id of the index represented by this object
	 */
	private int id;
	
	/**
	 * statistics
	 */
	private LuceneIndexStatistics statistics;

	private boolean generatingIndex;
	private int indexGenerationProgress;
	private boolean isEnabled;
	private boolean isActive;
	

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setEnabled(final boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @param indexGenerationProgress
	 *            the progress of the index-generation
	 */
	public void setIndexGenerationProgress(final int indexGenerationProgress) {
		this.indexGenerationProgress = indexGenerationProgress;
	}

	/**
	 * @return the progress of the index-generation
	 */
	public int getIndexGenerationProgress() {
		return indexGenerationProgress;
	}
	
	/**
	 * @return the statistics
	 */
	public LuceneIndexStatistics getIndexStatistics() {
		return this.statistics;
	}

	/**
	 * @param statistics the statistics to set
	 */
	public void setIndexStatistics(final LuceneIndexStatistics statistics) {
		this.statistics = statistics;
	}
    /**
     * @param generatingIndex isGeneratingIndex
     */
	public void setGeneratingIndex(final boolean generatingIndex) {
		this.generatingIndex = generatingIndex;
	}
	
	/**
	 * @return isGeneratingIndex
	 */
	public boolean isGeneratingIndex() {
		return generatingIndex;
	}
	

}
