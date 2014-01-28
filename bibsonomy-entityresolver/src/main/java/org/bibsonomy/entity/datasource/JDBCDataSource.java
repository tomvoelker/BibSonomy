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
