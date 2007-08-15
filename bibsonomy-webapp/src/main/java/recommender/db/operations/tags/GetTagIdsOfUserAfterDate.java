/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tags;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;
import recommender.model.SimpleMapEntry;

/**
 * Liefert alle tag_ids und deren Verwendungshäufigkeit von taggings, die ein bestimmter User nach einem bestimmten Datum gepostet hat
 * 
 * @author Jens Illig
 */
public class GetTagIdsOfUserAfterDate extends DatabaseQuery<Map.Entry<Map<Integer,Integer>,Integer>> {
	private final String userName;
	private final Date date;
	private final Date date2;
	
	public GetTagIdsOfUserAfterDate(final String userName, final Date date, final Date date2, Set<Integer> excludeIds, Set<Integer> intersectIds) {
		super(new RSHandler(excludeIds, intersectIds));
		this.date = date;
		this.date2 = date2;
		this.userName = userName;
	}
	
	public GetTagIdsOfUserAfterDate(final String userName, final Date date, Set<Integer> excludeIds, Set<Integer> intersectIds) {
		this(userName,date,null,excludeIds,intersectIds);
	}

	@Override
	protected String getSQL() {
		return "select tags.tag_id,ctr from (select tag_name,count(*) as ctr from tas where user_name=? AND date>?" + ((date2 != null) ? " AND date<?" : "" ) + " GROUP BY tag_name ORDER BY tag_name) tas,tags WHERE tas.tag_name=tags.tag_name";
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setString(1,userName);
		stmnt.setDate(2,date);
		if (date2 != null) {
			stmnt.setDate(3,date2);
		}
	}
	
	public static class RSHandler implements ResultSetHandler<Map.Entry<Map<Integer,Integer>,Integer>> {
		private final Set<Integer> excludeIds;
		private final Set<Integer> intersectIds;
		
		public RSHandler(Set<Integer> excludeIds, Set<Integer> intersectIds) {
			this.excludeIds = excludeIds;
			this.intersectIds = intersectIds;
		}
		
		public Map.Entry<Map<Integer,Integer>,Integer> handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				int counter = 0;
				Map<Integer,Integer> rVal = new HashMap<Integer,Integer>();
				do {
					Integer id = rs.getInt(1);
					if ((excludeIds.contains(id) == false) && (intersectIds.contains(id))) {
						int val = rs.getInt(2);
						counter += val;
						rVal.put(id,val);
					}
				} while (rs.next());
				return new SimpleMapEntry<Map<Integer,Integer>,Integer>(rVal,counter);
			}
			return null;
		}
	};
}
