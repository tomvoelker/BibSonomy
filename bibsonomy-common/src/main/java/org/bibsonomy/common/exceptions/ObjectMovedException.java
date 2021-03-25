/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common.exceptions;

import java.util.Date;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class ObjectMovedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String newId;
	private final String userName;
	private final Date date;
	private final Class<?> type;
	
	/**
	 * Constructs a new resource moved exception with the specified resource
	 * id.
	 * 
	 * @param id
	 *            the intra hash of the resource that has been moved. This is written
	 *            into a detail message which is saved for later retrieval by
	 *            the {@link #getMessage()} method.
	 * @param type - type of the resource that has moved
	 * @param newId
	 * 			  the intra hash of the new resource when the resource changed 
	 * @param userName 
	 * 			  the name of the user who owns the resource
	 * @param date
	 * 			  the new date of the resource. This is necessary to identify 
	 *            resources whose date has not changed.  
	 */
	public ObjectMovedException(final String id, final Class<?> type, final String newId, final String userName, final Date date) {
		super("The requested object (with ID " + id + ") has been moved to new ID " + newId + ". \n");
		this.type = type;
		this.newId = newId;
		this.userName = userName;
		this.date = date;
	}
	
	/**
	 * @return The new intra hash of the requested resource. 
	 */
	public String getNewId() {
		return this.newId;
	}

	/**
	 * @return The name of the user who owns the resource.
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @return The new posting date of the resource. 
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @return The type of the resource that has moved.
	 */
	public Class<?> getType() {
		return this.type;
	}
	
	
}