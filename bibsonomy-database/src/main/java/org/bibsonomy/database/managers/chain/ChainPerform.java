/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.common.DBSession;

/**
 * This interface encapsulates the getter for a L object
 *
 * @param <L> Type of the fetched result entities
 * @param <P> Type of the param object
 * 
 * @author Christian Schenk
 */
public interface ChainPerform<P, L> {

	/**
	 * Walks through the chain until a ChainElement is found that can handle the
	 * request.
	 * 
	 * @param param describes the requirements of the request
	 * @param session a database session
	 * @return the list of entities, which is returned by the fitting chainelement 
	 */
	public L perform(P param, DBSession session);
}