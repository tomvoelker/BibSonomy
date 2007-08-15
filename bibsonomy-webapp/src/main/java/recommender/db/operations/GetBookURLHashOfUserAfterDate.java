/*
 * Created on 20.01.2006
 */
package recommender.db.operations;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;
import recommender.model.SimpleMapEntry;

/**
 * Liefert hashwert und content_id aller Contents, die ein bestimmter user in einem bestimmten ZeitIntervall gepostet hat
 * 
 * @author Jens Illig
 */
public class GetBookURLHashOfUserAfterDate extends DatabaseQuery<Map.Entry<String,Integer>> {
	private final String userName;
	private final Date date;
	private final Date date2;
	
	public GetBookURLHashOfUserAfterDate(final String userName, final Date date, final Date date2) {
		super(new RSHandler());
		this.date = date;
		this.date2 = date2;
		this.userName = userName;
	}

	@Override
	protected String getSQL() {
		return "select book_url_hash, content_id from bookmark where user_name=? AND date>?" + ((date2 != null) ? " AND date<?" : "" );
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setString(1,userName);
		stmnt.setDate(2,date);
		if (date2 != null) {
			stmnt.setDate(3,date2);
		}
	}
	
	public static class RSHandler implements ResultSetHandler<Map.Entry<String,Integer>> {
		
		public Map.Entry<String,Integer> handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return new SimpleMapEntry<String,Integer>(rs.getString(1),rs.getInt(2));
			}
			return null;
		}
	};
}
