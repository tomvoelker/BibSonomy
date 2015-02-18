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
package org.bibsonomy.es;

import java.util.Date;

/**
 * Information of the system like System url, lastTasId, lastLogDate
 *
 * @author lutful
 */
public class SystemInformation {
	private String systemUrl;
	private Integer last_tas_id;
	private Date last_log_date;
	private String postType;

	/**
	 * @return the systemUrl
	 */
	public String getSystemUrl() {
		return this.systemUrl;
	}

	/**
	 * @param systemUrl the systemUrl to set
	 */
	public void setSystemUrl(String systemUrl) {
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

	/**
	 * @return the last_log_date
	 */
	public Date getLast_log_date() {
		return this.last_log_date;
	}

	/**
	 * @param last_log_date the last_log_date to set
	 */
	public void setLast_log_date(Date last_log_date) {
		this.last_log_date = last_log_date;
	}

	/**
	 * @return the last_tas_id
	 */
	public Integer getLast_tas_id() {
		return this.last_tas_id;
	}

	/**
	 * @param last_tas_id the last_tas_id to set
	 */
	public void setLast_tas_id(Integer last_tas_id) {
		this.last_tas_id = last_tas_id;
	}

}
