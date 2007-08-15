/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tags;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;
import recommender.model.SimilarityCategory;

public class GetWaitingSims extends DatabaseQuery<Map<Integer,Double>> {
	private static final Logger log = Logger.getLogger(GetWaitingSims.class);
	private final SimilarityCategory c;
	private final boolean onlyFromTmpMostSimilar;
	
	public GetWaitingSims(final SimilarityCategory c, final boolean onlyFromTmpMostSimilar) {
		super(rsHandler);
		this.c = c;
		this.onlyFromTmpMostSimilar = onlyFromTmpMostSimilar;
	}

	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return (onlyFromTmpMostSimilar == true) ? "SELECT t.waiting_content_sim,t.tag_id FROM tags t, (SELECT tag_id FROM TmpMostSimTagsByContent GROUP BY tag_id) tmp WHERE tmp.tag_id=t.tag_id" : "SELECT waiting_content_sim,tag_id FROM tags";
		}
		log.fatal("unknown category " + c);
		return null;
	}
	
	public static ResultSetHandler<Map<Integer,Double>> rsHandler = new ResultSetHandler<Map<Integer,Double>>() {
		public Map<Integer,Double> handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				Map<Integer,Double> rVal = new HashMap<Integer,Double>();
				do {
					rVal.put(rs.getInt(2), rs.getDouble(1));
				} while (rs.next());
				return rVal;
			}
			return null;
		}
	};
}
