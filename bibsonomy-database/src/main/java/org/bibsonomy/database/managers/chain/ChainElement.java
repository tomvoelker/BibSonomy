/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;

/**
 * Represents one element in the chain of responsibility.
 * 
 * @param <L> Type of the fetched result entities
 * @param <P> Type of the param object
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 */
public abstract class ChainElement<L, P> {
	protected static final Log log = LogFactory.getLog(ChainElement.class);
	
	protected final GeneralDatabaseManager generalDb;
	protected final GroupDatabaseManager groupDb;

	/**
	 * Constructor
	 */
	public ChainElement() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.groupDb = GroupDatabaseManager.getInstance();
	}

	/**
	 * Handles the request.
	 */
	protected abstract L handle(P param, DBSession session);

	/**
	 * Returns true if the request can be handled by the current chain element,
	 * otherwise false.
	 */
	protected abstract boolean canHandle(P param);
}