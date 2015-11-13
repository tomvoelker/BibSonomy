/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.model;

import org.bibsonomy.search.update.SearchIndexSyncState;

/**
 * infos about a search index
 *
 * @author ?
 */
public class SearchIndexInfo {
	/** The id of the index represented by this object */
	private String id;
	
	private SearchIndexState state;
	
	private SearchIndexSyncState syncState;
	
	/** statistics */
	private SearchIndexStatistics statistics;
	
	private double indexGenerationProgress;

	/** 
	 * indicates if the index is in sync with the DB
	 */
	private boolean correct;

	/**
	 * @return correct
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
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the state
	 */
	public SearchIndexState getState() {
		return this.state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(SearchIndexState state) {
		this.state = state;
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
	public void setStatistics(SearchIndexStatistics statistics) {
		this.statistics = statistics;
	}

	/**
	 * @param indexGenerationProgress
	 *            the progress of the index-generation
	 */
	public void setIndexGenerationProgress(final double indexGenerationProgress) {
		this.indexGenerationProgress = indexGenerationProgress;
	}

	/**
	 * @return the syncState
	 */
	public SearchIndexSyncState getSyncState() {
		return this.syncState;
	}

	/**
	 * @param syncState the syncState to set
	 */
	public void setSyncState(SearchIndexSyncState syncState) {
		this.syncState = syncState;
	}

	/**
	 * @return the progress of the index-generation
	 */
	public double getIndexGenerationProgress() {
		return indexGenerationProgress;
	}
}