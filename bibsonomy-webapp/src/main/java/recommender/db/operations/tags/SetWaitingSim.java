/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tags;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;
import recommender.model.SimilarityCategory;

public class SetWaitingSim extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(SetWaitingSim.class);
	private final int tagId;
	private final double sim;
	private final SimilarityCategory c;
	
	public SetWaitingSim(final SimilarityCategory c, final int tagId, final double sim) {
		this.tagId = tagId;
		this.c = c;
		this.sim = sim;
	}

	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return "UPDATE tags SET waiting_content_sim=? WHERE tag_id=?";
		}
		log.fatal("unknown category " + c);
		return null;
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setDouble( 1, sim );
		stmnt.setInt( 2, tagId );
	}
}
