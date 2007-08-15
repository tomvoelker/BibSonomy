/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tags;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import recommender.db.Helper;
import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;

public class GetTagIds extends DatabaseQuery<Map<String,Integer>> {
	private String tagNamesCSV;
	
	public GetTagIds(Iterable<String> tagNames) {
		super(rsHandler);
		this.tagNamesCSV = Helper.buildQuotedCSVList(tagNames);
	}

	@Override
	protected String getSQL() {
		return "SELECT tag_name,tag_id FROM tags WHERE tag_name IN (" + tagNamesCSV + ")";
	}
	
	public static ResultSetHandler<Map<String,Integer>> rsHandler = new ResultSetHandler<Map<String,Integer>>() {
		public Map<String,Integer> handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				Map<String,Integer> rVal = new HashMap<String,Integer>();
				do {
					rVal.put(rs.getString(1),rs.getInt(2));
				} while (rs.next());
				return rVal;
			}
			return null;
		}
	};
}
