package helpers.database;

import java.sql.*;

public class DBIdManager {
	
	PreparedStatement select_id = null;
	PreparedStatement update_id = null;
	ResultSet rst = null;
	
	public void prepareStatements (Connection conn, int name) throws SQLException {
		select_id = conn.prepareStatement("SELECT value FROM ids WHERE name = " + name + " FOR UPDATE"); 
		update_id = conn.prepareStatement("UPDATE ids SET value=value+1 WHERE name = " + name);
	}

	public int getNewId () throws SQLException {
		rst = select_id.executeQuery();
		if (rst.next()) {
			update_id.executeUpdate();
			return rst.getInt("value");
		} else {
			throw new SQLException ("Could not get ID");
		}
	}
	
	public void closeStatements () {
		if(select_id != null) {try {select_id.close();} catch (SQLException e) {} select_id = null;}
		if(update_id != null) {try {update_id.close();} catch (SQLException e) {} update_id = null;}
		if(rst       != null) {try {rst.close();      } catch (SQLException e) {} rst       = null;}
	}
}