/*
 * Created on 19.01.2006
 */
package recommender.db.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

public abstract class DatabaseQuery<T> extends Database.DatabaseOperation implements Iterable<T>, Iterator<T> {
	private static final Logger log = Logger.getLogger(DatabaseQuery.class);
	private ResultSet rs;
	private ResultSetHandler<T> rsHandler;
	private T next;
	private Statement stmnt;
	private final boolean prepareable;
	
	public DatabaseQuery(ResultSetHandler<T> rsHandler) {
		this(rsHandler, true);
	}
	
	public DatabaseQuery(ResultSetHandler<T> rsHandler, final boolean prepareable) {
		this.rsHandler = rsHandler;
		this.prepareable = prepareable;
	}
	
	final void execute(final Connection conn) throws SQLException {
		String sql = getSQL();
		if (sql != null) {
			if (prepareable == true) {
				log.debug("trying to prepare");
				PreparedStatement ps = conn.prepareStatement(getSQL());
				log.debug("prepared");
				setParams(ps);
				this.rs = ps.executeQuery();
				stmnt = ps;
			} else {
				stmnt = conn.createStatement();
				this.rs = stmnt.executeQuery(sql);
			}
			this.next = this.rsHandler.handle(rs);
		} else {
			next = null;
		}
	}

	public boolean hasNext() {
		return (next != null);
	}

	public T next() {
		if (next == null) {
			throw new NoSuchElementException();
		}
		T rVal = next;
		try {
			next = rsHandler.handle(rs);
			if (next == null) {
				close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return rVal;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<T> iterator() {
		return this;
	}
	
	public void close() {
		if (rs != null) {
			try {
				try {
					rs.close(); // nach JDBC-API-Doc nicht nötig, aber sicher ist sicher
				} finally {
					stmnt.close();
				}
			} catch (SQLException e) {
				log.warn("unable to close statement: ",e);
			}
			rs = null;
			stmnt = null;
		}
	}
	
	public void finalize() throws Throwable {
		close();
		super.finalize();
	}
	

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
	}
}
