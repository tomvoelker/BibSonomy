/*
 * Created on 21.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;
import recommender.model.HalfTagSimilarity;
import recommender.model.SimilarityCategory;
import recommender.model.TagSimilarity;

public class InsertMostSimTagsEntry extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(InsertMostSimTagsEntry.class);
	
	private final SimilarityCategory c;
	private final int tagId, simTagId;
	private final double sim;
	
	public InsertMostSimTagsEntry(final SimilarityCategory c, final int tagId, final int simTagId, final double sim) {
		this.c = c;
		this.tagId = tagId;
		this.simTagId = simTagId;
		this.sim = sim;
	}
	
	public InsertMostSimTagsEntry(final SimilarityCategory c, final TagSimilarity sim) {
		this(c,sim.getRightTagID(),sim.getLeftTagID(),sim.getSimilarity());
	}
	
	public InsertMostSimTagsEntry(final SimilarityCategory c, final int tagId, final HalfTagSimilarity sim) {
		this(c,tagId,sim.getLeftTagID(),sim.getSimilarity());
	}
	
	@Override
	protected String getSQL() {
		switch(c) {
		case CONTENT:
		case USER:
		case OVERALL:
		case COMBIVECTOROVERALL:
			return "INSERT INTO " + c.getMostSimTableName() + " (tag_id,sim_tag_id,sim) VALUES(?,?,?)";
		}
		log.fatal("unknown category '" + c + "'");
		throw new IllegalStateException("unknown category '" + c + "'");
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setInt(1,tagId);
		stmnt.setInt(2,simTagId);
		stmnt.setDouble(3,sim);
	}

}
