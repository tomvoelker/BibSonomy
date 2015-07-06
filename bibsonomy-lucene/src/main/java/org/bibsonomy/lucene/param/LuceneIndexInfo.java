/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
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

/**
 * TODO: add documentation to this class
 *
 * @author 
 */
public class LuceneIndexInfo {
	
	/**
	 * The FSPath for this index
	 */
	private String basePath;

	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * The id of the index represented by this object
	 */
	private int id;
	
	/**
	 * statistics
	 */
	private LuceneIndexStatistics statistics;
	
	/** 
	 * indicates if the index is in sync with the DB
	 */
	private boolean correct;
	

	/**
	 * @return 
	 */
	public boolean isCorrect() {
		return correct;
	}

	/**
	 * @param correct the correct to set
	 */
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	private boolean generatingIndex;
	private int indexGenerationProgress;
	private boolean isEnabled;
	private boolean isActive;
	private String errorMassage;
	

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

	/**
	 * @return the errorMassage
	 */
	public String getErrorMassage() {
		return this.errorMassage;
	}

	/**
	 * @param errorMassge the errorMassage to set
	 */
	public void setErrorMassage(String errorMassage) {
		this.errorMassage = errorMassage;
	}
	

}
