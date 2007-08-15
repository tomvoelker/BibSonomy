/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;

/**
 * Liefert Alle tagids, die schonmal von einem User verwendet wurden
 * 
 * @author Jens Illig
 */
public class GetUsedTagIds extends DatabaseQuery<Integer> {
	private static final Logger log = Logger.getLogger(GetUsedTagIds.class);
	private String key = null; 
	
	public GetUsedTagIds() {
		super(rsHandler);
	}

	@Override
	protected String getSQL() {
		return "SELECT DISTINCT tag_id FROM TagUser";
	}
	
	public static Set<Integer> buildSet(Iterable<Integer> it) {
		Set<Integer> rVal = new HashSet<Integer>();
		for (Integer i : it) {
			rVal.add(i);
		}
		return rVal;
	}
	
	public static ResultSetHandler<Integer> rsHandler = new ResultSetHandler<Integer>() {
		public Integer handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return rs.getInt(1);
			}
			return null;
		}
	};
}
