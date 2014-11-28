/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.sync;

/**
 * @author wla
 */
public enum SynchronizationDirection {
	/*
	 * NOTE: column is a varchar(4), so please use short names
	 */
	/**
	 * both directions 
	 */
	BOTH("both"),
	
	/**
	 * only server changes will be applied to client 
	 */
	SERVER_TO_CLIENT("stoc"),
	
	/**
	 * only client changes will be applied to server
	 */
	CLIENT_TO_SERVER("ctos");
	
	
	private String direction;

	private SynchronizationDirection(final String direction) {
		this.direction = direction;
	}
	
	/**
	 * @return The string representation for the synchronization direction.
	 */
	public String getSynchronizationDirection() {
		return direction;
	}
	
	/**
	 * @param direction
	 * @return synchronization direction for given string.
	 */
	public static SynchronizationDirection getSynchronizationDirectionByString(String direction) {
		if("stoc".equals(direction)) {
			return SERVER_TO_CLIENT;
		} else if("ctos".equals(direction)) {
			return CLIENT_TO_SERVER;
		} else {
			return BOTH;
		}
	}
}
