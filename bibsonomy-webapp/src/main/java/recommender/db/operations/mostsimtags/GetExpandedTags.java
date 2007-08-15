/*
 * Created on 20.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.model.SimilarityCategory;

/**
 * Liefert zu den von einem User oder für einen Content verwendeten Tags die Liste der dazu aufsummiert ähnlichsten Tags
 * 
 * @author Jens Illig
 */
public class GetExpandedTags extends DatabaseQuery<Map<Integer,Double>> {
	private static final Logger log = Logger.getLogger(GetExpandedTags.class);
	private final String srcKey;
	private final SimilarityCategory expandCategory;
	private final AbstractGetVectorEntries.Category srcCategory;
	private final Integer tagId;
	
	public GetExpandedTags(SimilarityCategory expandCategory, AbstractGetVectorEntries.Category srcCategory, String srcKey, Set<Integer> excludeTagIds) {
		super(new RSHandler(excludeTagIds));
		this.expandCategory = expandCategory;
		this.srcCategory = srcCategory;
		this.srcKey = srcKey;
		this.tagId = null;
	}

	@Override
	protected String getSQL() {
		if (srcCategory == AbstractGetVectorEntries.Category.USER) {
			return "SELECT sim_tag_id,m.sim*tmp.ctr as sim FROM " + expandCategory.getMostSimTableName() + " m, (SELECT tag_id,user_name,ctr FROM " + srcCategory.getVectorTableName() + " WHERE user_name=? ORDER BY tag_id) tmp WHERE m.tag_id=tmp.tag_id";
		} else if (srcCategory == AbstractGetVectorEntries.Category.CONTENT) {
			return "SELECT sim_tag_id,m.sim*tmp.ctr as sim FROM " + expandCategory.getMostSimTableName() + " m, (SELECT tag_id,hash,ctr FROM " + srcCategory.getVectorTableName() + " WHERE hash=? ORDER BY tag_id) tmp WHERE m.tag_id=tmp.tag_id";
		}
		log.fatal("unknown category");
		return null;
	}
	
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setString(1,srcKey);
	}
	
	public static class RSHandler implements ResultSetHandler<Map<Integer,Double>> {
		private final Map<Integer,Double> simAggregationMap = new HashMap<Integer,Double>();
		private final Set<Integer> excludeIds;
		
		public RSHandler(Set<Integer> excludeIds) {
			this.excludeIds = excludeIds;
		}
		
		public Map<Integer,Double> handle(ResultSet rs) throws SQLException {
			int id;
			while (rs.next()) {
				id = rs.getInt(1);
				if ((excludeIds == null) || (excludeIds.contains(id) == false)) {
					Double d = simAggregationMap.get(id);
					if (d == null) {
						d = rs.getDouble(2);
					} else {
						d += rs.getDouble(2);
					}
					simAggregationMap.put(id,d);
				}
			}
			if (simAggregationMap.size() > 0) {
				return simAggregationMap;
			} else {
				return null;
			}
		}
	};
	
	public static final Comparator<Map.Entry<Integer,Double>> comparator = new Comparator<Map.Entry<Integer,Double>>() {

		public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
			int rVal = o1.getValue().compareTo(o2.getValue());
			return (rVal != 0) ? rVal : System.identityHashCode(o1) - System.identityHashCode(o2);
		}
		
	};
	
	public static Set<Map.Entry<Integer,Double>> getOrderedSet(Map<Integer,Double> m) {
		Set<Map.Entry<Integer,Double>> rVal = new TreeSet<Map.Entry<Integer,Double>>(GetExpandedTags.comparator);
		rVal.addAll(m.entrySet());
		return rVal;
	}
}
