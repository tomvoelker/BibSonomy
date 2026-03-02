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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 
 * @author sst
 */
public class LoggingDatabaseManager {
	private static final String INSERT_LOGDATA_SQL = "INSERT INTO clicklog ("
			+ "logdate, dompath, dompathwclasses, type, pageurl, ahref, acontent, useragent, host, "
			+ "completeheader, xforwardedfor, username, sessionid, listpos, clientwindowsize, "
			+ "mouseclientpos, mousedocumentpos, anumofposts, abmown, referer"
			+ ") VALUES ("
			+ "NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, LEFT(?, 250)"
			+ ")";

	private DataSource dataSource;
	
	/**
	 * inserts the log data into the db
	 * @param logdata
	 */
	public void insertLogdata(final LogData logdata) {
		if (this.dataSource == null) {
			throw new IllegalStateException("No logging data source configured");
		}
		try (final Connection connection = this.dataSource.getConnection();
			 final PreparedStatement statement = connection.prepareStatement(INSERT_LOGDATA_SQL)) {
			statement.setString(1, logdata.getDompath());
			statement.setString(2, logdata.getDompath2());
			statement.setString(3, logdata.getType());
			statement.setString(4, logdata.getPageurl());
			statement.setString(5, logdata.getAhref());
			statement.setString(6, logdata.getAcontent());
			statement.setString(7, logdata.getUseragent());
			statement.setString(8, logdata.getHost());
			statement.setString(9, logdata.getCompleteheader());
			statement.setString(10, logdata.getXforwardedfor());
			statement.setString(11, logdata.getUsername());
			statement.setString(12, logdata.getSessionid());
			statement.setString(13, logdata.getListpos());
			statement.setString(14, logdata.getWindowsize());
			statement.setString(15, logdata.getMouseclientpos());
			statement.setString(16, logdata.getMousedocumentpos());
			statement.setString(17, logdata.getAnumberofposts());
			statement.setString(18, logdata.getAbmown());
			statement.setString(19, logdata.getReferer());
			statement.executeUpdate();
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
		} catch (final SQLException e) {
			throw new IllegalStateException("Couldn't insert logging data", e);
		}
	}

	/**
	 * @param dataSource
	 *            the datasource used for log inserts
	 */
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
