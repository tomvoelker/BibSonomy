/*
 * Created on 21.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;
import recommender.model.SimilarityCategory;

public class DeleteMostSimTags extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(DeleteMostSimTags.class);
	
	private final SimilarityCategory c;
	private final Integer tagId;
	private final boolean tmp;
	
	public DeleteMostSimTags(final SimilarityCategory c, final Integer tagId, boolean tmp) {
		this.c = c;
		this.tagId = tagId;
		this.tmp = tmp;
	}
	
	@Override
	protected String getSQL() {
		final String tableName = (tmp == true) ? ("Tmp" + c.getMostSimTableName()) : c.getMostSimTableName();
		return (tagId != null) ? ("DELETE FROM " + tableName + " WHERE tag_id=?") : ("DELETE FROM " + tableName);
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		if (tagId != null) {
			stmnt.setInt(1,tagId);
		}
	}

}
