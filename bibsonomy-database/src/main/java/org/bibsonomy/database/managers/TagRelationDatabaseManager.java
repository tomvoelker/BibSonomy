package org.bibsonomy.database.managers;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.beans.TagRelationParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Used to deal with tag concepts in the database.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class TagRelationDatabaseManager extends AbstractDatabaseManager {
	private static final Logger log = Logger.getLogger(TagRelationDatabaseManager.class);
	private final static TagRelationDatabaseManager singleton = new TagRelationDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final DatabasePluginRegistry plugins;
	
	private static enum Relation {
		SUPER,
		SUB
	};

	private TagRelationDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	public static TagRelationDatabaseManager getInstance() {
		return singleton;
	}

	public void insertRelations(final Tag tag, final String userName, final DBSession session) {
		addRel(tag.getName(), tag.getSuperTags(), userName, Relation.SUPER, session);
		addRel(tag.getName(), tag.getSubTags(), userName, Relation.SUB, session);
	}

	private void addRel(String centerTagName, final List<Tag> relatedTags, final String userName, final Relation rel, final DBSession session) {
		if ((relatedTags == null) || (relatedTags.size() == 0)) {
			return;
		}
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setCreationDate(new Date());
		if (rel == Relation.SUPER) {
			trp.setLowerTagName(centerTagName);
		} else if (rel == Relation.SUB) {
			trp.setUpperTagName(centerTagName);
		} else {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "unknown " + Relation.class.getName() + " '" + rel.toString() + "'");
		}
		for (final Tag t : relatedTags) {
			if (rel == Relation.SUPER) {
				trp.setUpperTagName(t.getName());
			} else if (rel == Relation.SUB) {
				trp.setLowerTagName(t.getName());
			}
			this.insertIfNotPresent(trp, session);
		}
	}

	private void insertIfNotPresent(final TagRelationParam trp, final DBSession session) {
		session.beginTransaction();
		try {
			insert("insertTagRelationIfNotPresent", trp, true, session);
			generalDb.updateIds( ConstantID.IDS_TAGREL_ID, session);
		} catch (Exception e) {
			log.debug(e.getMessage(),e);
		} finally {
			session.commitTransaction();
			session.endTransaction();
		}
	}
	
	public void deleteRelation(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		plugins.onTagRelationDelete(upperTagName, lowerTagName, userName, session);
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setLowerTagName(lowerTagName);
		trp.setUpperTagName(upperTagName);
		delete("deleteTagRelation", trp, session);
	}

	/*public List<Tag> getSubtagsOfTag(final Tag tag, final DBSession session) {
		return this.queryForList("getSubtagsOfTag", tag, Tag.class, session);
	}

	public List<Tag> getSupertagsOfTag(final Tag tag, final DBSession session) {
		return this.queryForList("getSupertagsOfTag", tag, Tag.class, session);
	}*/
}