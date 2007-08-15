/*
 * Created on 21.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;
import recommender.model.SimilarityCategory;

public class DeleteSingleMostSimTagsEntry extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(DeleteSingleMostSimTagsEntry.class);
	
	private final SimilarityCategory c;
	private final int tagId;
	private final int simTagId;
	private final boolean tmp;
	
	public DeleteSingleMostSimTagsEntry(final SimilarityCategory c, boolean tmp, final int tagId, final int simTagId) {
		this.c = c;
		this.tagId = tagId;
		this.simTagId = simTagId;
		this.tmp = tmp;
	}
	
	@Override
	protected String getSQL() {
		if (tmp == true) {
			switch (c) {
			case CONTENT:
				return "DELETE FROM TmpMostSimTagsByContent WHERE tag_id=? AND sim_tag_id=?";
			case USER:
				return "DELETE FROM TmpMostSimTagsByUser WHERE tag_id=? AND sim_tag_id=?";
			case OVERALL:
				return "DELETE FROM TmpMostSimTagsOverall WHERE tag_id=? AND sim_tag_id=?";
			case COMBIVECTOROVERALL:
				return "DELETE FROM TmpMostSimTagsCombiVectorOverall WHERE tag_id=? AND sim_tag_id=?";
			}
		} else {
			switch (c) {
			case CONTENT:
				return "DELETE FROM MostSimTagsByContent WHERE tag_id=? AND sim_tag_id=?";
			case USER:
				return "DELETE FROM MostSimTagsByUser WHERE tag_id=? AND sim_tag_id=?";
			case OVERALL:
				return "DELETE FROM MostSimTagsOverall WHERE tag_id=? AND sim_tag_id=?";
			case COMBIVECTOROVERALL:
				return "DELETE FROM MostSimTagsCombiVectorOverall WHERE tag_id=? AND sim_tag_id=?";
			}
		}
		log.fatal("unknown category '" + c + "'");
		throw new IllegalStateException("unknown category '" + c + "'");
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setInt(1,tagId);
		stmnt.setInt(2,simTagId);
	}

}
