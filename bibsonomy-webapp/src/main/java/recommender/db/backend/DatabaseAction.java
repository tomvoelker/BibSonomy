/*
 * Created on 20.01.2006
 */
package recommender.db.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DatabaseAction<T> extends Database.DatabaseOperation {
	private T rVal;
	
	@Override
	protected String getSQL() {
		return null;
	}

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
	}

	@Override
	void execute(Connection conn) throws SQLException {
		rVal = action();
	}
	
	protected abstract T action();

	public T getReturnValue() {
		return rVal;
	}
}
