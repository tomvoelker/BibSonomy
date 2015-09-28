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
package org.bibsonomy.search.es.management;

import java.io.Serializable;
import java.net.URI;

import org.bibsonomy.search.update.IndexUpdaterState;


/**
 * Information of the system like System url, lastTasId, lastLogDate
 *
 * @author lutful
 */
public class SystemInformation implements Serializable {
	private static final long serialVersionUID = 3300034332953151505L;
	
	private URI systemUrl;
	private IndexUpdaterState updaterState;
	private String postType;

	/**
	 * @return the systemUrl
	 */
	public URI getSystemUrl() {
		return this.systemUrl;
	}

	/**
	 * @param systemUrl the systemUrl to set
	 */
	public void setSystemUrl(URI systemUrl) {
		this.systemUrl = systemUrl;
	}


	/**
	 * @return the postType
	 */
	public String getPostType() {
		return this.postType;
	}

	/**
	 * @param postType the type of the post (Bookmark/Bibtex/GoldStandard)
	 */
	public void setPostType(String postType) {
		this.postType = postType;
	}

	public IndexUpdaterState getUpdaterState() {
		return this.updaterState;
	}

	public void setUpdaterState(IndexUpdaterState updaterState) {
		this.updaterState = updaterState;
	}
	
	

}
