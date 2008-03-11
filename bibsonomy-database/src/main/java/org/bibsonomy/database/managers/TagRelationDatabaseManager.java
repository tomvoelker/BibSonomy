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
	}

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

	private void addRel(final String centerTagName, final List<Tag> relatedTags, final String userName, final Relation rel, final DBSession session) {
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
		for (final Tag tag : relatedTags) {
			if (rel == Relation.SUPER) {
				trp.setUpperTagName(tag.getName());
			} else if (rel == Relation.SUB) {
				trp.setLowerTagName(tag.getName());
			}
			if (!isRelationPresent(trp, session)) {
				this.insertIfNotPresent(trp, session);
			}
		}
	}

	private void insertIfNotPresent(final TagRelationParam trp, final DBSession session) {
		session.beginTransaction();
		try {
			insert("insertTagRelationIfNotPresent", trp, true, session);
			this.generalDb.updateIds(ConstantID.IDS_TAGREL_ID, session);
		} catch (final Exception ex) {
			log.debug(ex.getMessage(), ex);
		} finally {
			session.commitTransaction();
			session.endTransaction();
		}
	}
	
	/**
	 * Deletes a whole concept
	 * @param upperTagName - the concept name
	 * @param userName - the owner of the concept
	 * @param session - the session
	 */
	public void deleteConcept(final String upperTagName, final String userName, final DBSession session) {
		this.plugins.onConceptDelete(upperTagName, userName, session);
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setUpperTagName(upperTagName);
		delete("deleteConcept", trp, session);
	}
	
	/**
	 * Deletes a single relation
	 * @param upperTagName - supertag name
	 * @param lowerTagName -  subtag name
	 * @param userName - the owners name
	 * @param session - the session
	 */
	public void deleteRelation(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		this.plugins.onTagRelationDelete(upperTagName, lowerTagName, userName, session);
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setLowerTagName(lowerTagName);
		trp.setUpperTagName(upperTagName);
		delete("deleteTagRelation", trp, session);
	}	
	
	/**
	 * @param userName
	 * @return
	 */
	public List<Tag> getAllConceptsForUser(final String userName, final DBSession session) {
		return queryForList("getAllConceptsForUser", userName, session);
	}
	
	/**
	 * @param userName
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Tag> getConceptsForuser(final String userName, final int start, final int end, final DBSession session) {
		return null;
	}
	
	/**
	 * Retrieve concepts for users as well as groups
	 * @param conceptName the conceptname
	 * @param userName the users or groups name
	 * @param session
	 * @return
	 */
	public Tag getConceptForUser(final String conceptName, final String userName, final DBSession session) {
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setUpperTagName(conceptName);
		return this.queryForObject("getConceptForUser",trp, Tag.class, session);
	}
	
	/**
	 * @param userName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> getPickedConceptsForUser(final String userName, final DBSession session) {
		return queryForList("getPickedConceptsForUser", userName, session);
	}
	
	/**
	 * @param session - the db session
	 * @return all concepts (50 most popular)
	 */
	public List<Tag> getAllConcepts(final DBSession session) {
		return queryForList("getAllConcepts", null, session);
	}	
	
	/**
	 * @param session - the db session
	 * @return a global concept by name
	 */
	public Tag getGlobalConceptByName(final String conceptName, final DBSession session) {
		return queryForObject("getGlobalConceptByName", conceptName, Tag.class, session);
	}
	
	
	/**
	 * Checks if the given relation already exists for the user
	 * @param param -  TagRelationParam
	 * @param session -  the session
	 * @return <code>true</code> if relation already exists else <code>false</code>
	 */
	public boolean isRelationPresent(TagRelationParam param, final DBSession session) {
		String relationID = queryForObject("getRelationID", param, String.class, session);
		
		if (relationID == null)
			return false;		
		return true;
	}
	
}