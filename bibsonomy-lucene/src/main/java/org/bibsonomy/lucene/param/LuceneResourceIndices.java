/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.lucene.param;

import java.util.List;

import org.bibsonomy.search.model.SearchIndexStatistics;


/**
 * Bean for classifier settings
 * 
 * @author Sven Stefani
 * @author bsc
 */
public class LuceneResourceIndices {
	/**
	 * the name of the resource the index contains
	 */
	private String resourceName;
	
	/**
	 * statistics
	 */
	private SearchIndexStatistics statistics;
	
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
	public void setIndexStatistics(final SearchIndexStatistics indexStatistics){
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
	public SearchIndexStatistics getStatistics() {
		return this.statistics;
	}

	/**
	 * @param statistics the statistics to set
	 */
	public void setStatistics(final SearchIndexStatistics statistics) {
		this.statistics = statistics;
	}
}