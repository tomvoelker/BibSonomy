/*
 * Created on 02.04.2006
 */
package recommender.db.operations.recommendation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import recommender.db.backend.DatabaseQuery;
import recommender.model.RecommendedTag;

public class GetMostUsedTagsOfUser extends DatabaseQuery<RecommendedTag> {
	private final String userName;

	public GetMostUsedTagsOfUser(final String userName) {
		super(new NeuroUserContentTagRecommendation.RSHandler());
		this.userName = userName;
	}
	
	@Override
	protected String getSQL() {
		return "SELECT t.tag_id, t.tag_name, tmp.ctr FROM (" +
				"SELECT tag_id, ctr FROM TagUser WHERE user_name=? ORDER BY ctr DESC LIMIT 8" +
			") tmp, tags t WHERE t.tag_id=tmp.tag_id";
	}

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		stmnt.setString(1,userName);
	}

}
