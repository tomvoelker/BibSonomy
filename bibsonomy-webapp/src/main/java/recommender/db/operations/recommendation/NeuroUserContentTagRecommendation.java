/*
 * Created on 20.01.2006
 */
package recommender.db.operations.recommendation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.Logger;

import recommender.db.Helper;
import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;
import recommender.logic.termprocessing.TermProcessingIterator;
import recommender.model.RecommendedTag;


/**
 * Versucht die zum Content passenden Tags, welche der Benutzer schonmal verwendet hat, zu finden und aufzuwerten.
 * 
 * @author Jens Illig
 */
public class NeuroUserContentTagRecommendation extends DatabaseQuery<RecommendedTag> {
	private static final Logger log = Logger.getLogger(NeuroUserContentTagRecommendation.class);
	private final String userName;
	private final String hash;
	private final String titleOrMetaTagCSV;
	private final int results;
	
	public NeuroUserContentTagRecommendation(final String userName, final String hash, final String titleOrMetaTagCSV, int results) {
		super(new RSHandler(), ((titleOrMetaTagCSV == null) || (titleOrMetaTagCSV.length() == 0)) );
		this.hash = hash;
		this.userName = userName;
		this.titleOrMetaTagCSV = titleOrMetaTagCSV;
		this.results = results;
	}
	
	public NeuroUserContentTagRecommendation(final String userName, final String hash, int results) {
		this(userName, hash, null, results);
	}

	public static class RSHandler implements ResultSetHandler<RecommendedTag> {
		public RecommendedTag handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return new RecommendedTag(rs.getInt(1), rs.getString(2), rs.getDouble(3));
			} else {
				return null;
			}
		}
	}
	
	public static Iterator<String> buildTagExtractionIterator(final String title) {
		final Scanner s = new Scanner(title);
		s.useDelimiter("([\\|/\\\\ \t;!,\\-:\\)\\(\\]\\[\\}\\{]+)|(\\.[\\t ]+)");
		return new TermProcessingIterator(s);
	}
	
	public static String buildTagCSVFromString(final String title) {
		return Helper.buildQuotedCSVList(new Iterable<String>() {
			public Iterator<String> iterator() {
				return buildTagExtractionIterator(title);
			}
		});
	}
	
	public static String buildTagCSVFromIterator(final Iterator<String> it) {
		return Helper.buildQuotedCSVList(new Iterable<String>() {
			public Iterator<String> iterator() {
				return it;
			}
		});
	} 

	@Override
	protected String getSQL() {
		if ((titleOrMetaTagCSV == null) || (titleOrMetaTagCSV.length() == 0)) {
			return "SELECT t.tag_id, t.tag_name, tmp2.score FROM (" +
				"select tmp.tag_id,sum(tmp.score) score FROM (" + 
					"(" +
						"select c.tag_id, c.ctr*(CASE WHEN u.ctr IS NULL THEN 0.5 ELSE u.ctr END) as score FROM (" +
							"SELECT tag_id,ctr FROM TagContent WHERE hash=? ORDER BY tag_id" + 
						") c LEFT OUTER JOIN (" + 
							"SELECT tag_id,ctr FROM TagUser WHERE user_name=? ORDER BY tag_id" + 
						") u ON c.tag_id=u.tag_id" + 
					") UNION ALL (" + 
						"select m.tag_id,sum(c.ctr*u.ctr*m.sim) as score from MostSimTagsByContent m, (" + 
							"SELECT tag_id,ctr FROM TagContent tc WHERE hash=? ORDER BY ctr DESC LIMIT 8" + 
						") c, TagUser u WHERE c.tag_id=m.sim_tag_id AND m.tag_id=u.tag_id AND u.user_name=? GROUP BY m.tag_id" +
					")" + 
				") tmp GROUP BY tag_id order by score desc limit ?" +
			") tmp2, tags t WHERE tmp2.tag_id=t.tag_id";
		} else {
			if (hash.indexOf('\'') > -1) {
				log.error("found incorect key '" + hash + "'");
				return null;
			}
			final String escapedUserName = Helper.escape(userName);
			return "SELECT t.tag_id, t.tag_name, tmp2.score FROM (" +
				"select tmp.tag_id,sum(tmp.score) score FROM (" + 
					"(" +
						"select c.tag_id, c.ctr*(CASE WHEN u.ctr IS NULL THEN 0.5 ELSE u.ctr END) as score FROM (" +
							"SELECT tag_id, sum(ctr) as ctr FROM (" +
								"(" +
									"SELECT tag_id,ctr FROM TagContent WHERE hash='" + hash + "' " +
								") UNION ALL (" +
									"SELECT tag_id,0.05 as ctr FROM tags WHERE tag_name IN (" + titleOrMetaTagCSV + ")" +
								")" +
							") ct GROUP BY tag_id" +
						") c LEFT OUTER JOIN (" + 
							"SELECT tag_id,ctr FROM TagUser WHERE user_name='" + escapedUserName + "' ORDER BY tag_id" + 
						") u ON c.tag_id=u.tag_id" + 
					") UNION ALL (" + 
						"select m.tag_id,sum(c.ctr*u.ctr*m.sim) as score from MostSimTagsByContent m, (" + 
							"SELECT tag_id, sum(ctr) as ctr FROM (" +
								"(" +
									"SELECT tag_id,ctr FROM TagContent WHERE hash='" + hash + "' " +
								") UNION ALL (" +
									"SELECT tag_id,0.5 as ctr FROM tags WHERE tag_name IN (" + titleOrMetaTagCSV + ")" +
								")" +
							") ct GROUP BY tag_id ORDER BY ctr DESC LIMIT 8" + 
						") c, TagUser u WHERE c.tag_id=m.sim_tag_id AND m.tag_id=u.tag_id AND u.user_name='" + escapedUserName + "' GROUP BY m.tag_id" +
					")" + 
				") tmp GROUP BY tag_id order by score desc limit " + results +
			") tmp2, tags t WHERE tmp2.tag_id=t.tag_id";
		}
	}

	@Override
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		if (hash != null) {
			stmnt.setString(1,hash);
			stmnt.setString(3,hash);
		} else {
			stmnt.setNull(1,Types.CHAR);
			stmnt.setNull(3,Types.CHAR);
		}
		stmnt.setString(2,userName);
		stmnt.setString(4,userName);
		stmnt.setInt(5,results);
	}
}
