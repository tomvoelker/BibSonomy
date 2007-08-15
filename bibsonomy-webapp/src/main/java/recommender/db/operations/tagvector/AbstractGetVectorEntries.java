/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;
import recommender.model.SimilarityCategory;
import recommender.model.TagVector;
import recommender.model.TagVectorEntry;


/**
 * Liefert TagVector.Entry Objekte.zu einem bestimmten oder allen Tags aus der Datenbank,
 * welche Teilmenge davon bestimmt die Subklasse.
 * 
 * @author Jens Illig
 */
public abstract class AbstractGetVectorEntries extends DatabaseQuery<TagVector.Entry> {
	protected static final int RESULT_INDEX_TAGID = 1;
	protected static final int RESULT_INDEX_KEY = 2;
	protected static final int RESULT_INDEX_COUNTER = 3;
	
	public static enum Category {
		CONTENT("TagContent"),
		USER("TagUser");
		
		private final String vectorTableName;
		private static final Logger log = Logger.getLogger(Category.class);
		
		Category(final String vectorTableName) {
			this.vectorTableName = vectorTableName;
		}
		
		public static Category forSimilarityCategory(SimilarityCategory c) {
			if (c == SimilarityCategory.CONTENT) {
				return CONTENT;
			}
			if (c == SimilarityCategory.USER) {
				return USER;
			}
			log.fatal("unknown category " + c);
			return null;
		}
		
		public String getVectorTableName() {
			return vectorTableName;
		}
	}
	
	private final Integer tagId;
	
	public AbstractGetVectorEntries(Integer tagId) {
		super(rsHandler);
		this.tagId = tagId;
	}
	
	public AbstractGetVectorEntries() {
		super(rsHandler);
		this.tagId = null;
	}
	
	protected void setParams(PreparedStatement stmnt) throws SQLException {
		if (tagId != null) {
			stmnt.setInt(1,tagId);
		}
	}
	
	public static ResultSetHandler<TagVector.Entry> rsHandler = new ResultSetHandler<TagVector.Entry>() {
		public TagVector.Entry handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return new TagVectorEntry(rs.getInt(RESULT_INDEX_TAGID), rs.getString(RESULT_INDEX_KEY), rs.getInt(RESULT_INDEX_COUNTER));
			}
			return null;
		}
	};

	protected Integer getTagId() {
		return tagId;
	}
	
}
