/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import recommender.db.backend.DatabaseAction;
import recommender.db.backend.DatabaseCommand;
import recommender.db.backend.DatabaseQuery;
import recommender.db.backend.ResultSetHandler;


/**
 * Zählt die Verwendungsanzahlen eines Tags bzgl. Nutzer und Inhalt hoch
 * 
 * @author Jens Illig
 */
public class MarkModified extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(MarkModified.class);
	private final Collection<Integer> tagIds;
	private final AbstractGetVectorEntries.Category c;
	private final boolean implied;
	
	public MarkModified(final Collection<Integer> tagIds, final AbstractGetVectorEntries.Category c, final boolean implied) {
		this.tagIds = tagIds;
		this.c = c;
		this.implied = implied;
	}
	
	private boolean isAlreadyMarked(int tagId) {
		GetModifiedTagIds gmtis = new GetModifiedTagIds(tagId,implied,implied);
		try {
			runDBOperation(gmtis);
			return gmtis.hasNext();
		} finally {
			gmtis.close();
		}
	}
	
	@Override
	protected Object action() {
		for (int tagId : tagIds) {
			if (isAlreadyMarked(tagId) == false) {
				runDBOperation(new SetModified(tagId,c,implied));
			}
		}		
		return null;
	}
	
	public static class GetModifiedTagIds extends DatabaseQuery<Integer> {
		private final Integer tagId;
		private final boolean implied;
		private final boolean inUse;
		
		public GetModifiedTagIds(final Integer tagId, final boolean inUse, final boolean implied) {
			super(rsHandler);
			this.tagId = tagId;
			this.implied = implied;
			this.inUse = inUse;
		}
		
		public GetModifiedTagIds(final boolean inUse, final boolean implied) {
			this(null,inUse,implied);
		}

		@Override
		protected String getSQL() {
			if (tagId != null) {
				return "SELECT tag_id FROM ContentModifiedTags WHERE in_use=? AND tag_id=?";
			} else {
				return "SELECT tag_id FROM ContentModifiedTags WHERE in_use=?";
			}
		}

		@Override
		protected void setParams(PreparedStatement stmnt) throws SQLException {
			stmnt.setInt(1, (inUse == true) ? ((implied == true) ? 2 : 1) : 0);
			if (tagId != null) {
				stmnt.setInt(2,tagId);
			}
		}
		
		public static ResultSetHandler<Integer> rsHandler = new ResultSetHandler<Integer>() {
			public Integer handle(ResultSet rs) throws SQLException {
				if (rs.next()) {
					return rs.getInt(1);
				}
				return null;
			}
		};
	}
	
	public static class SetModified extends DatabaseCommand {
		private static final Logger log = Logger.getLogger(SetModified.class);
		private final int tagId;
		private final AbstractGetVectorEntries.Category c;
		private final boolean implied;
		
		public SetModified(int tagId, AbstractGetVectorEntries.Category c, boolean implied) {
			this.c = c;
			this.tagId = tagId;
			this.implied = implied;
		}

		@Override
		protected String getSQL() {
			switch (c) {
			case CONTENT:
				return "INSERT INTO ContentModifiedTags (tag_id,in_use) VALUES (?,?)";
			case USER:
				return "INSERT INTO UserModifiedTags (tag_id,in_use) VALUES (?,?)";
			}
			log.fatal("unknown category " + c);
			return null;
		}

		@Override
		protected void setParams(PreparedStatement stmnt) throws SQLException {
			stmnt.setInt(1,tagId);
			stmnt.setInt(2, (implied == true) ? 2 : 0);
		}
	}
}
