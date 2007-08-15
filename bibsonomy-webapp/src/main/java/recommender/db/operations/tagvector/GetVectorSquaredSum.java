/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;


/**
 * Liefert die zur Zwei-Norm nötige quadratische Summe eines TagVectors
 * 
 * @author Jens Illig
 */
public class GetVectorSquaredSum extends DatabaseQuery<GetVectorSquaredSum.SquaredSum> {
	private static final int RESULT_INDEX_2NORM = 2;
	private static final int RESULT_INDEX_TAGID = 1;
	private static final Logger log = Logger.getLogger(GetVectorSquaredSum.class);
	
	private final Integer tagId;
	private final AbstractGetVectorEntries.Category c;
	
	public GetVectorSquaredSum(AbstractGetVectorEntries.Category c, Integer tagId) {
		super(rsHandler);
		this.tagId = tagId;
		this.c = c;
	}
	
	public GetVectorSquaredSum(AbstractGetVectorEntries.Category c) {
		super(rsHandler);
		this.tagId = null;
		this.c = c;
	}
	
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		if (tagId != null) {
			stmnt.setInt(1,tagId);
		}
	}
	
	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return (tagId != null) ? "SELECT tag_id, sum(ctr*ctr) FROM TagContent WHERE tag_id=? GROUP BY tag_id" : "SELECT tag_id, sum(ctr*ctr) FROM TagContent GROUP BY tag_id";
		case USER:
			return (tagId != null) ? "SELECT tag_id, sum(ctr*ctr) FROM TagUser WHERE tag_id=? GROUP BY tag_id" : "SELECT tag_id, sum(ctr*ctr) FROM TagUser GROUP BY tag_id";
		}
		log.fatal("unknown category");
		throw new IllegalStateException("unknown category");
	}
	
	public static ResultSetHandler<SquaredSum> rsHandler = new ResultSetHandler<SquaredSum>() {
		public SquaredSum handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return new SquaredSum(rs.getInt(RESULT_INDEX_TAGID), rs.getLong(RESULT_INDEX_2NORM));
			}
			return null;
		}
	};

	public static class SquaredSum {
		private long squaredSum;
		private int tagId;
		
		public SquaredSum(int tagId, long squaredSum) {
			this.squaredSum = squaredSum;
			this.tagId = tagId;
		}
		
		public long getSquaredSum() {
			return squaredSum;
		}
		
		public int getTagId() {
			return tagId;
		}
		
	}
	
	protected Integer getTagId() {
		return tagId;
	}
	
	public static Map<Integer,Long> buildMap(Iterable<SquaredSum> it) {
		Map<Integer,Long> m = new HashMap<Integer,Long>();
		for (SquaredSum sqs : it) {
			m.put(sqs.getTagId(),sqs.getSquaredSum());
		}
		return m;
	}
}
