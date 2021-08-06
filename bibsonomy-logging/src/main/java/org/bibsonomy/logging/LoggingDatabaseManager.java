/**
 * BibSonomy-Logging - Logs clicks from users of the BibSonomy webapp.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.logging;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;

/**
 * 
 * @author sst
 */
public class LoggingDatabaseManager extends AbstractDatabaseManager {
	
	private DBSessionFactory sessionFactory;
	
	private DBSession openSession() {
		return this.sessionFactory.getDatabaseSession();
	}
	
	/**
	 * inserts the log data into the db
	 * @param logdata
	 */
	public void insertLogdata(final LogData logdata) {
		final DBSession session = this.openSession();
		try {
			this.insert("BibLog.insertLogdata", logdata, session);
		} finally {
			session.close();
		}
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(final DBSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}