/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseCommand;

public class DeleteModifiedTagsInUse extends DatabaseCommand {
	private static final Logger log = Logger.getLogger(DeleteModifiedTagsInUse.class);
	private final AbstractGetVectorEntries.Category c;
	private final boolean implied;
	
	public DeleteModifiedTagsInUse(AbstractGetVectorEntries.Category c, final boolean implied) {
		this.c = c;
		this.implied = implied;
	}

	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return "DELETE FROM ContentModifiedTags WHERE in_use=?";
		case USER:
			return "DELETE FROM UserModifiedTags WHERE in_use=?";
		}
		log.fatal("unknown category " + c);
		return null;
	}

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setInt(1, (implied == true) ? 2 : 1);
	}
}
