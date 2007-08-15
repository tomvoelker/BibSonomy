/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Liefert die TagVector.Entry Objekte.zu allen Tags welche als geändert und "in_use" markiert wurden.
 * 
 * @author Jens Illig
 */
public class GetModifiedVectorEntries extends AbstractGetVectorEntries {
	private static final Logger log = Logger.getLogger(GetVectorEntries.class);
	private Category c;
	private final boolean implied;
	private final boolean inUse;
	
	public GetModifiedVectorEntries(Category c, final boolean inUse, final boolean implied) {
		this.c = c;
		this.implied = implied;
		this.inUse = inUse;
	}

	@Override
	protected String getSQL() {
		switch (c) {
		case CONTENT:
			return "SELECT tc.* FROM TagContent tc, ContentModifiedTags t WHERE t.tag_id=tc.tag_id AND t.in_use=?";
		case USER:
			return "SELECT tu.* FROM TagUser tu, UserModifiedTags t WHERE t.tag_id=tu.tag_id AND t.in_use=?";
		}
		log.fatal("unknown category");
		throw new IllegalStateException("unknown category");
	}

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setInt( 1, (inUse == true) ? ((implied == true) ? 2 : 1) : 0);
	}
	
	
}
