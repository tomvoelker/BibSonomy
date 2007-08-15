package helpers.database;

import java.sql.SQLException;
import java.util.LinkedList;

import resources.Bibtex;


public class DBExtendedFieldGetManager extends DBManager {
	
	
	public static LinkedList<String> getExtendedFields (String currUser, String hash) {
		DBContext c = new DBContext();
		try {
			if (c.init()) { // initialize database
			
				c.stmt = c.conn.prepareStatement("SELECT d.*, m.* " +
						"FROM extended_fields_map m " +
						"  JOIN extended_fields_data d ON d.key_id=m.key_id " +
						"  JOIN bibtex b ON b.content_id=d.content_id " +
						"WHERE b.user_name = ? AND b.simhash" + Bibtex.INTRA_HASH + " = ? " +
						"ORDER BY m.order"); 

				c.stmt.setString(1, currUser);
				c.stmt.setString(2, hash);

				c.rst = c.stmt.executeQuery();
				
				LinkedList<String> list = new LinkedList<String>();
				
				int counter = 2;
				while (c.rst.next()) {
					while (counter < c.rst.getInt("order")) {
						list.add("");
						counter++;
					}
					
					list.add(c.rst.getString("value"));
					counter++;
				}
				while (counter < 11) {
					counter++;
					list.add("");
				}
				return list;
				
			}
		} catch (SQLException e) {
		} finally {
			c.close(); // close database connection
		}
		return null;
	}
	
}
