/**
 * BibSonomy Entity Resolver - Username/author identiy resolving for BibSonomy.
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
package org.bibsonomy.entity.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import no.priv.garshol.duke.Column;
import no.priv.garshol.duke.RecordIterator;

/**
 * 
 * @author dzo
 */
public class JDBCDataSource extends no.priv.garshol.duke.datasources.JDBCDataSource {
	private DataSource dataSource;

	@Override
	public RecordIterator getRecords() {
		try {
			final Connection connection = this.dataSource.getConnection();
			final Statement stmt = connection.createStatement();
			final ResultSet rs = stmt.executeQuery(this.getQuery());
			return new JDBCIterator(rs);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * @param columns the columns to set
	 */
	public void setColumns(final Map<String, Column> columns) {
		this.columns = columns;
	}
}
