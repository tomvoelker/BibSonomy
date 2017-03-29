/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.model.metadata;

import java.util.Date;

/**
 * @author clemensbaier
 */
public class PostMetaData {

	private String interHash;
	private String intraHash;
	private String key;
	private String userName;
	private String value;
	private Date date;
	
	/**
	 * @return the interHash
	 */
	public String getInterHash() {
		return this.interHash;
	}
	
	/**
	 * @param interHash the interHash to set
	 */
	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}
	
	/**
	 * @return the intraHash
	 */
	public String getIntraHash() {
		return this.intraHash;
	}
	
	/**
	 * @param intraHash the intraHash to set
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
}
