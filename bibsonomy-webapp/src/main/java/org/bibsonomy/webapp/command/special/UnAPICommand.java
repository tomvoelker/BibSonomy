/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.special;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * 
 * The data of a UnAPI request. See <a href="http://unapi.info">UnAPI.info</a>.
 * 
 * Represents a post (by an id) in a certain format.
 * 
 * @author rja
 */
public class UnAPICommand extends BaseCommand {

	private String id;
	private String format;
	
	/**
	 * @return The requested id of the post.
	 */
	public String getId() {
		return this.id;
	}
	/** 
	 * @param id - the id of the post.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return The requested format.
	 */
	public String getFormat() {
		return this.format;
	}
	/**
	 * @param format - the format the post should be returned.
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
}
