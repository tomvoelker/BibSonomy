/*
 * Created on 20.01.2006
 */
package recommender.db.operations.tagvector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import recommender.db.Helper;
import recommender.db.backend.DatabaseAction;
import recommender.db.backend.DatabaseCommand;
import resources.Resource;


/**
 * Zählt die Verwendungsanzahlen eines Tags bzgl. Nutzer und Inhalt hoch
 * 
 * @author Jens Illig
 */
public class DecTagVectorSpace extends DatabaseAction<Object> {
	private static final Logger log = Logger.getLogger(DecTagVectorSpace.class);
	private final Collection<Integer> tagIds;
	private final Resource res;
	
	public DecTagVectorSpace(final Collection<Integer> tagIds, final Resource res) {
		this.tagIds = tagIds;
		this.res = res;
	}
	
	public static class DecTagVectorEntry extends DatabaseCommand {
		private static final Logger log = Logger.getLogger(DecTagVectorEntry.class);
		private final String tagIdList;
		private final String key;
		private final AbstractGetVectorEntries.Category c;
		
		public DecTagVectorEntry(final String tagIdList, final String key, final AbstractGetVectorEntries.Category c) {
			super(false);
			this.tagIdList = tagIdList;
			this.key = key;
			this.c = c;
		}
	
		@Override
		protected String getSQL() {
			if (key.indexOf('\'') > -1) {
				log.error("found incorect key '" + key + "'");
				return null;
			}
			switch (c) {
			case USER:
				return "UPDATE TagUser SET ctr=ctr-1 WHERE user_name='" + key + "' AND tag_id IN (" + tagIdList + ")";
			case CONTENT:
				return "UPDATE TagContent SET ctr=ctr-1 WHERE hash='" + key + "' AND tag_id IN (" + tagIdList + ")";
			}
			log.fatal("unknown category " + c);
			return null;
		}
	}

	public static class CleanTagVectors extends DatabaseCommand {
		private static final Logger log = Logger.getLogger(DecTagVectorEntry.class);
		private final String key;
		private final AbstractGetVectorEntries.Category c;
		
		public CleanTagVectors(final String key, final AbstractGetVectorEntries.Category c) {
			this.key = key;
			this.c = c;
		}
	
		@Override
		protected String getSQL() {
			if (key.indexOf('\'') > -1) {
				log.error("found incorect key '" + key + "'");
				return null;
			}
			switch (c) {
			case USER:
				return  "DELETE FROM TagUser WHERE user_name=? AND ctr=0";
			case CONTENT:
				return "DELETE FROM TagContent WHERE hash=? AND ctr=0";
			}
			log.fatal("unknown category " + c);
			return null;
		}

		@Override
		protected void setParams(PreparedStatement stmnt) throws SQLException {
			stmnt.setString(1,key);
		}
		
	}
	
	@Override
	protected Object action() {
		final String tagIdList = Helper.buildCSVList(tagIds);
		final String hash = res.getContentType() + res.getOldHash();
		final String escapedUserName = Helper.escape(res.getUser());
		
		runDBOperation(new DecTagVectorEntry(tagIdList,hash,AbstractGetVectorEntries.Category.CONTENT));
		runDBOperation(new CleanTagVectors(hash,AbstractGetVectorEntries.Category.CONTENT));
		runDBOperation(new DecTagVectorEntry(tagIdList,escapedUserName,AbstractGetVectorEntries.Category.USER));
		runDBOperation(new CleanTagVectors(res.getUser(),AbstractGetVectorEntries.Category.USER));
		
		return null;
	}
}
