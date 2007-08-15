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

public class GetTagNames extends DatabaseQuery<Map<Integer,String>> {
	private String tagIdsCSV;
	
	public GetTagNames(Iterable<Integer> tagIds) {
		super(rsHandler);
		this.tagIdsCSV = Helper.buildCSVList(tagIds);
	}

	@Override
	protected String getSQL() {
		return "SELECT tag_name,tag_id FROM tags WHERE tag_id IN (" + tagIdsCSV + ")";
	}
	
	public static ResultSetHandler<Map<Integer,String>> rsHandler = new ResultSetHandler<Map<Integer,String>>() {
		public Map<Integer,String> handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				Map<Integer,String> rVal = new HashMap<Integer,String>();
				do {
					rVal.put(rs.getInt(2), rs.getString(1));
				} while (rs.next());
				return rVal;
			}
			return null;
		}
	};
}
