/*
 * Created on 19.01.2006
 */
package recommender.db.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public abstract class DatabaseCommand extends Database.DatabaseOperation {
	private static final Logger log = Logger.getLogger(DatabaseCommand.class);
	private final boolean preparable;
	
	public DatabaseCommand(boolean preparable) {
		this.preparable = preparable;
	}
	
	public DatabaseCommand() {
		this.preparable = true;
	}
	
	final void execute(final Connection conn) throws SQLException {
		String sql = getSQL();
		if (sql != null) {
			Statement stmnt = null;
			try {
				
				if (isPreparable() == true) {
					log.debug("trying to prepare");
					PreparedStatement ps = conn.prepareStatement(sql);
					setParams(ps);
					stmnt = ps;
					log.debug("prepared");
					ps.execute();
				} else {
					stmnt = conn.createStatement();
					stmnt.execute(sql);
				}
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage() + " in execution of " + this.getClass().getName(),e);
			} finally {
				if (stmnt != null) {
					stmnt.close();
					stmnt = null;
				}
			}
		}
	}
	

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
	}
	
	protected boolean isPreparable() {
		return true;
	}
}
