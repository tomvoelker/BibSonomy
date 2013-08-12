/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model.metadata;

import java.util.Date;

/**
 * @author clemensbaier
 * @version $Id$
 */
public class PostMetaData {

	private String interHash;
	private String intraHash;
	private String key;
	private String userName;
	private String value;
	private Date date;
	
	public String getInterHash() {
		return this.interHash;
	}
	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}
	public String getIntraHash() {
		return this.intraHash;
	}
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String user_name) {
		this.userName = user_name;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Date getDate() {
		return this.date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
