/*
 * Created on 20.01.2006
 */
package recommender.db.operations.mostsimtags;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;
import recommender.model.SimilarityCategory;
import recommender.model.SimpleTagSimilarity;
import recommender.model.TagSimilarity;

/**
 * Liefert die Einträge aus den Listen der stärksten ausgehenden Ähnlichkeiten von Tags
 * 
 * @author Jens Illig
 */
public class GetMostSimTags extends DatabaseQuery<TagSimilarity> {
	private static final Logger log = Logger.getLogger(GetMostSimTags.class);
	private String tagNamesCSV;
	private SimilarityCategory c;
	private final Integer tagId;
	private final boolean tmp;
	
	/** 
	 * Listen aller Tags in dieser Kategorie geordnet nach ihrer tagid holen
	 * @param c
	 */
	public GetMostSimTags(SimilarityCategory c, boolean tmp) {
		super(new RSHandler(null));
		this.c = c;
		this.tagId = null;
		this.tmp = tmp;
	}
	
	public GetMostSimTags(final Integer tagId, SimilarityCategory c, boolean tmp) {
		super(new RSHandler(tagId));
		this.c = c;
		this.tagId = tagId;
		this.tmp = tmp;
	}

	@Override
	protected String getSQL() {
		switch(c) {
		case CONTENT:
		case USER:
		case OVERALL:
		case COMBIVECTOROVERALL:
			return (tagId != null) ? "SELECT sim_tag_id, sim FROM " + ((tmp == true) ? "Tmp" : "") + c.getMostSimTableName() + " WHERE tag_id=?" : "SELECT tag_id, sim_tag_id, sim FROM " + ((tmp == true) ? "Tmp" : "") + c.getMostSimTableName() + " ORDER BY tag_id";
		}
		log.fatal("unknown category '" + c + "'");
		throw new IllegalStateException("unknown category '" + c + "'");
	}
	
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		if (tagId != null) {
			stmnt.setInt(1,tagId);
		}
	}
	
	public static class RSHandler implements ResultSetHandler<TagSimilarity> {
		private final Integer tagId;
		
		public RSHandler(final Integer tagId) {
			this.tagId = tagId;
		}
		
		public TagSimilarity handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				if (tagId != null) {
					return new SimpleTagSimilarity(rs.getInt(1), tagId, rs.getDouble(2));
				} else {
					return new SimpleTagSimilarity(rs.getInt(2), rs.getInt(1), rs.getDouble(3));
				}
			}
			return null;
		}
	};
}
