package org.bibsonomy.lucene.param;

import java.util.List;


/**
 * Bean for classifier settings
 * 
 * @author Sven Stefani
 * @author bsc
 * @version $Id$
 */
public class LuceneResourceIndices {
	/**
	 * the name of the resource the index contains
	 */
	private String resourceName;
	
	/**
	 * statistics
	 */
	private LuceneIndexStatistics statistics;
	
	private boolean isEnabled;
	
	private boolean generatingIndex;
	private int indexGenerationProgress;
	
	private List<LuceneIndexStatistics> inactiveIndecesStatistics;

	/**
	 * @return the inactiveIndecesStatistics
	 */
	public List<LuceneIndexStatistics> getInactiveIndecesStatistics() {
		return this.inactiveIndecesStatistics;
	}


	/**
	 * @param inactiveIndecesStatistics the inactiveIndecesStatistics to set
	 */
	public void setInactiveIndecesStatistics(final List<LuceneIndexStatistics> inactiveIndecesStatistics) {
		this.inactiveIndecesStatistics = inactiveIndecesStatistics;
	}


	/**
	 * @param indexStatistics
	 * 
	 */
	public void setIndexStatistics(final LuceneIndexStatistics indexStatistics){
		this.statistics = indexStatistics;
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
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(final String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
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

	/**
	 * @param indexGenerationProgress the progress of the index-generation
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
	public LuceneIndexStatistics getStatistics() {
		return this.statistics;
	}

	/**
	 * @param statistics the statistics to set
	 */
	public void setStatistics(final LuceneIndexStatistics statistics) {
		this.statistics = statistics;
	}
}