/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import java.util.Date;

import org.bibsonomy.search.model.SearchIndexStatistics;


/**
 * lucene statistics like current version, number of docs
 *  
 * @author sst
 */
@Deprecated
public class LuceneIndexStatistics extends SearchIndexStatistics {

	private int numDocs = 0;
	private int numDeletedDocs = 0;
	private Date lastModified;
	private long currentVersion = 0;
	private boolean isCurrent = true;
	private int indexId;
	public LuceneIndexStatistics() {
		this.indexId = -1;
	}


	/**
	 * @return the currentVersion
	 */
	public long getCurrentVersion() {
		return this.currentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(final long currentVersion) {
		this.currentVersion = currentVersion;
	}

	/**
	 * @return the numDocs
	 */
	public int getNumDocs() {
		return this.numDocs;
	}

	/**
	 * @param numDocs the numDocs to set
	 */
	public void setNumDocs(final int numDocs) {
		this.numDocs = numDocs;
	}

	/**
	 * @return the numDeletedDocs
	 */
	public int getNumDeletedDocs() {
		return this.numDeletedDocs;
	}

	/**
	 * @param numDeletedDocs the numDeletedDocs to set
	 */
	public void setNumDeletedDocs(final int numDeletedDocs) {
		this.numDeletedDocs = numDeletedDocs;
	}
	
	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return this.lastModified;
	}
	
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(final Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the isCurrent
	 */
	public boolean isCurrent() {
		return this.isCurrent;
	}

	/**
	 * @param isCurrent the isCurrent to set
	 */
	public void setCurrent(final boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}
	
	public int getIndexId() {
		return indexId;
	}
}