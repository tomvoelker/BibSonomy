/*
 * Created on 21.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;
import recommender.model.SimilarityCategory;

public class UpdateMostSimTagsEntry extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(UpdateMostSimTagsEntry.class);
	
	private final SimilarityCategory c;
	private final int tagId, simTagId;
	private final double sim;
	private final boolean tmp;
	
	public UpdateMostSimTagsEntry(final SimilarityCategory c, final int tagId, final int simTagId, final double sim, final boolean tmp) {
		this.c = c;
		this.tagId = tagId;
		this.simTagId = simTagId;
		this.sim = sim;
		this.tmp = tmp;
	}
	
	@Override
	protected String getSQL() {
		switch(c) {
		case CONTENT:
		case USER:
		case OVERALL:
		case COMBIVECTOROVERALL:
			return "UPDATE " + ((tmp == true) ? "Tmp" : "") + c.getMostSimTableName() + " SET sim=? WHERE tag_id=? AND sim_tag_id=?";
		}
		log.fatal("unknown category '" + c + "'");
		throw new IllegalStateException("unknown category '" + c + "'");
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setDouble(1,sim);
		stmnt.setInt(2,tagId);
		stmnt.setInt(3,simTagId);
	}

}
