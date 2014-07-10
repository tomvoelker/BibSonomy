package org.bibsonomy.entity.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import javax.sql.DataSource;

import no.priv.garshol.duke.Column;
import no.priv.garshol.duke.DukeException;
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
	 * adds Column's of columnsSet to columns
	 */
	public void init() {
		for(Column column : columnsSet) {
			this.addColumn(column);
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
	
	/**
	 * Overrides close() method to fix AbstractMethod- and IllegalAccessError by not checking for isClosed() on ResultSet and
	 * Statement, cause not all implementations of them provide a (proper) isClosed() Method.
	 *
	 * @author MarcelM
	 */
	public class JDBCIterator extends no.priv.garshol.duke.datasources.JDBCDataSource.JDBCIterator {
		
		private ResultSet rs;
		
		/**
		 * @param rs
		 * @throws SQLException
		 */
		public JDBCIterator(ResultSet rs) throws SQLException {
			super(rs);
			this.rs = rs;
		}
		
		/**
		 * This method differs from no.priv.garshol.duke.datasources.JDBCDataSource.JDBCIterator.close()
		 * in that it doesn't check for .isClosed() on ResultSet and Statement.
		 */
		@Override
		public void close() {
			Statement stmt;
			try {
				//According to JDBCUtils: "can't call rs.getStatement() after rs is closed, so must do it now"
				stmt = rs.getStatement();
				//We just close without further checking
				rs.close();
			} catch (SQLException e) {
				throw new DukeException(e);
			}

			//Close the statement
			if (stmt != null) {
				try {
					Connection conn = stmt.getConnection();
					stmt.close();
					if (conn != null && !conn.isClosed())
						conn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
}
