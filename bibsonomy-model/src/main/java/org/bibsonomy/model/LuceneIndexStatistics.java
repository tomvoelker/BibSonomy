/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model;

/**
 * This class defines a user. An unknown user has an empty (<code>null</code>) name.
 * 
 * @version $Id$
 */
public class LuceneIndexStatistics {

	private String newestRecordDate = null;
	private int numDocs = 0;
	private int numDeletedDocs = 0;
	private long lastModified = 0;
	private long CurrentVersion = 0;
	private boolean isCurrent = true;
	
	/**
	 * @return the currentVersion
	 */
	public long getCurrentVersion() {
		return this.CurrentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(long currentVersion) {
		this.CurrentVersion = currentVersion;
	}

	/**
	 * @return the newestRecordDate
	 */
	public String getNewestRecordDate() {
		return this.newestRecordDate;
	}

	/**
	 * @param newestRecordDate the newestRecordDate to set
	 */
	public void setNewestRecordDate(String newestRecordDate) {
		this.newestRecordDate = newestRecordDate;
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
	public void setNumDocs(int numDocs) {
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
	public void setNumDeletedDocs(int numDeletedDocs) {
		this.numDeletedDocs = numDeletedDocs;
	}
	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return this.lastModified;
	}
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
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
	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	
	
	
	
}