/*
 * Created on 20.01.2006
 */
package recommender.db.operations;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;

/**
 * Liefert tagids, welche ein(<=content_id) user innerhalb eines bestimmten zeitraums an einen content geschrieben hat
 * 
 * @author Jens Illig
 */
public class GetTaggingsOnContentIdInTimeSpan extends DatabaseQuery<Integer> {
	private final int contentId;
	private final Date date;
	private final Date date2;
	
	public GetTaggingsOnContentIdInTimeSpan(final int contentId, final Date date, final Date date2, Set<Integer> intersectionFilter) {
		super(new RSHandler(intersectionFilter));
		this.date = date;
		this.date2 = date2;
		this.contentId = contentId;
	}

	@Override
	protected String getSQL() {
		return "SELECT t.tag_id FROM tas, tags t WHERE tas.tag_name=t.tag_name AND content_id=? AND date>?" + ((date2 != null) ? " AND date<?" : "" );
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setInt(1,contentId);
		stmnt.setDate(2,date);
		if (date2 != null) {
			stmnt.setDate(3,date2);
		}
	}
	
	public static class RSHandler implements ResultSetHandler<Integer> {
		private final Set<Integer> intersectFilter; 
		
		public RSHandler(Set<Integer> intersectFilter) {
			this.intersectFilter = intersectFilter;
		}
		
		public Integer handle(ResultSet rs) throws SQLException {
			Integer rVal = null;
			while (rs.next() == true) {
				rVal = rs.getInt(1);
				if (intersectFilter.contains(rVal)) {
					return rVal;
				}
			}
			return null;
		}
	};
}
