/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tags;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import recommender.db.backend.DatabaseCommand;

public class SaveUserFreq extends DatabaseCommand {
	private final int tagId;
	private final int userFreq;
	
	public SaveUserFreq(final int tagId, final int userFreq) {
		this.tagId = tagId;
		this.userFreq = userFreq;
	}

	@Override
	protected String getSQL() {
		return "UPDATE tags SET user_freq=? WHERE tag_id=?";
	}

	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setInt( 1, userFreq );
		stmnt.setInt( 2, tagId );
	}
}
