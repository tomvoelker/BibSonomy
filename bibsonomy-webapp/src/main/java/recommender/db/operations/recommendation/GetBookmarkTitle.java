/*
 * Created on 02.04.2006
 */
package recommender.db.operations.recommendation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;

/**
 * Nur zu Evaluationszwecken: Titel zu einer content id holen.
 * 
 * @author Jens Illig
 */
public class GetBookmarkTitle extends DatabaseQuery<String> {
	private final int contentId;

	public GetBookmarkTitle(final int contentId) {
		super(new RSHandler());
		this.contentId = contentId;
	}
	
	@Override
	protected String getSQL() {
		return "SELECT book_description FROM bookmark WHERE content_id=?";
	}

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setInt(1,contentId);
	}

	public static class RSHandler implements ResultSetHandler<String> {
		public String handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return null;
			}
		}
	}
}
