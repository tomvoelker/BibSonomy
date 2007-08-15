/*
 * Created on 21.01.2006
 */
package recommender.db.operations.mostsimtags;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;
import recommender.model.HalfTagSimilarity;
import recommender.model.SimilarityCategory;

public class InsertMultipleMostSimTagsEntries extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(InsertMultipleMostSimTagsEntries.class);
	
	private final SimilarityCategory c;
	private final int tagId;
	private final Iterable<? extends HalfTagSimilarity> sims;
	private final boolean tmp;
	
	public InsertMultipleMostSimTagsEntries(final SimilarityCategory c, final int tagId, Iterable<? extends HalfTagSimilarity> sims, boolean tmp) {
		super(false);
		this.c = c;
		this.tagId = tagId;
		this.sims = sims;
		this.tmp = tmp;
	}
	
	@Override
	protected String getSQL() {
		StringBuilder sb = new StringBuilder(1300); // 30*40 + 100
		sb.append("INSERT INTO ");
		if (tmp == true) {
			sb.append("Tmp");
		}
		sb.append(c.getMostSimTableName());
		sb.append(" (tag_id,sim_tag_id,sim) VALUES");
		final String start = "(" + tagId + ",";
		for (HalfTagSimilarity sim : sims) {
			sb.append(start);
			sb.append(sim.getLeftTagID());
			sb.append(",");
			sb.append(sim.getSimilarity());
			sb.append("),");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
}
