package org.bibsonomy.entity.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import javax.sql.DataSource;

import no.priv.garshol.duke.Column;
import no.priv.garshol.duke.RecordIterator;

/**
 * 
 * @author dzo
 */
public class JDBCDataSource extends no.priv.garshol.duke.datasources.JDBCDataSource {
	private DataSource dataSource;
	
	private Set<Column> columnsSet;

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
	 * XXX - adds Column's of columnsSet to columns
	 */
	public void init() {
		for(Column column : columnsSet) {
			this.addColumn(column);
			System.out.println(column.getName());
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
	 * @return columns
	 */
	public Set<Column> getColumnsSet() {
		return this.columnsSet;
	}

	/**
	 * @param columnsSet 
	 * @param columns the columns to set
	 */
	public void setColumnsSet(Set<Column> columnsSet) {
		this.columnsSet = columnsSet;
		
	}
}
