package org.bibsonomy.database.managers;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.concept.ConceptChain;
import org.bibsonomy.database.params.TagRelationParam;
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

	private static final Log log = LogFactory.getLog(TagRelationDatabaseManager.class);

	private final static TagRelationDatabaseManager singleton = new TagRelationDatabaseManager();
	private final GeneralDatabaseManager generalDb;
	private final DatabasePluginRegistry plugins;
	private static final ConceptChain chain = new ConceptChain();

	/**
	 * Defines relations for tags or concepts.
	 */
	private static enum Relation {
		/** Super tag */
		SUPER,
		/** Sub tag */
		SUB
	}

	private TagRelationDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	/**
	 * @return a singleton instance of the TagRelationDatabaseManager
	 */
	public static TagRelationDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Performs the chain
	 * 
	 * @param param
	 * @param session
	 * @return list of concepts
	 */
	public List<Tag> getConcepts(final TagRelationParam param, DBSession session) {
		return chain.getFirstElement().perform(param, session);
	}
	
	/**
	 * Inserts a relation.
	 * 
	 * @param tag
	 * @param userName
	 * @param session
	 */
	public void insertRelations(final Tag tag, final String userName, final DBSession session) {
		this.addRel(tag.getName(), tag.getSuperTags(), userName, Relation.SUPER, session);
		this.addRel(tag.getName(), tag.getSubTags(), userName, Relation.SUB, session);
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
			this.insert("insertTagRelationIfNotPresent", trp, true, session);
			this.generalDb.updateIds(ConstantID.IDS_TAGREL_ID, session);
		} catch (final Exception ex) {
			// TODO: improve me...
			log.warn(ex.getMessage(), ex);
		} finally {
			session.commitTransaction();
			session.endTransaction();
		}
	}

	/**
	 * Deletes a whole concept
	 * 
	 * @param upperTagName -
	 *            the concept name
	 * @param userName -
	 *            the owner of the concept
	 * @param session -
	 *            the session
	 */
	public void deleteConcept(final String upperTagName, final String userName, final DBSession session) {
		this.plugins.onConceptDelete(upperTagName, userName, session);
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setUpperTagName(upperTagName);
		this.delete("deleteConcept", trp, session);
	}

	/**
	 * Deletes a single relation
	 * 
	 * @param upperTagName -
	 *            supertag name
	 * @param lowerTagName -
	 *            subtag name
	 * @param userName -
	 *            the owners name
	 * @param session -
	 *            the session
	 */
	public void deleteRelation(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		this.plugins.onTagRelationDelete(upperTagName, lowerTagName, userName, session);
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setLowerTagName(lowerTagName);
		trp.setUpperTagName(upperTagName);
		this.delete("deleteTagRelation", trp, session);
	}


	/**
	 * @param param
	 * @param session
	 * @return the concepts list of a user
	 */
	public List<Tag> getAllConceptsForUser(final TagRelationParam param, final DBSession session) {
		return this.queryForList("getAllConceptsForUser", param, Tag.class, session);
	}

	/**
	 * Retrieve concepts for users as well as groups
	 * 
	 * @param conceptName
	 *            the conceptname
	 * @param userName
	 *            the users or groups name
	 * @param session
	 * @return concepts for given user or group
	 */
	public Tag getConceptForUser(final String conceptName, final String userName, final DBSession session) {
		final TagRelationParam trp = new TagRelationParam();
		trp.setOwnerUserName(userName);
		trp.setUpperTagName(conceptName);
		return this.queryForObject("getConceptForUser", trp, Tag.class, session);
	}

	/**
	 * Returns the picked concepts for the given user.
	 * 
	 * @param userName
	 * @param session
	 * @return picked concepts for the given user
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> getPickedConceptsForUser(final String userName, final DBSession session) {
		return this.queryForList("getPickedConceptsForUser", userName, session);
	}

	/**
	 * @param session -
	 *            the db session
	 * @return all concepts (50 most popular)
	 */
	public List<Tag> getAllConcepts(final DBSession session) {
		return this.queryForList("getAllConcepts", null, Tag.class, session);
	}

	/**
	 * @param conceptName
	 * @param session -
	 *            the db session
	 * @return a global concept by name
	 */
	public Tag getGlobalConceptByName(final String conceptName, final DBSession session) {
		return this.queryForObject("getGlobalConceptByName", conceptName, Tag.class, session);
	}

	/**
	 * Checks if the given relation already exists for the user
	 * 
	 * @param param -
	 *            TagRelationParam
	 * @param session -
	 *            the session
	 * @return <code>true</code> if relation already exists else
	 *         <code>false</code>
	 */
	public boolean isRelationPresent(final TagRelationParam param, final DBSession session) {
		final String relationID = queryForObject("getRelationID", param, String.class, session);
		if (relationID == null) return false;
		return true;
	}
	
	/**
	 * Sets the concept with the given uppertag to picked
	 * 
	 * @param param
	 * @param session
	 */
	public void pickConcept(final Tag concept, final String ownerUserName, final DBSession session){
		TagRelationParam param = new TagRelationParam();
		param.setUpperTagName(concept.getName());
		param.setOwnerUserName(ownerUserName);
		
		this.update("pickConcept", param, session);
	}
	
	/**
	 * Sets the concept with the given uppertag to unpicked
	 * 
	 * @param param
	 * @param session
	 */
	public void unpickConcept(final Tag concept, final String ownerUserName, final DBSession session){
		TagRelationParam param = new TagRelationParam();
		param.setUpperTagName(concept.getName());
		param.setOwnerUserName(ownerUserName);
		
		this.update("unpickConcept", param, session);
	}
	
	/**
	 * Sets all concepts to unpicked
	 * 
	 * @param param
	 * @param session
	 */
	public void unpickAllConcepts(final String ownerUserName, final DBSession session){
		this.update("unpickAllConcepts", ownerUserName, session);
	}
	
	/**
	 * Sets all concepts to picked
	 * 
	 * @param param
	 * @param session
	 */
	public void pickAllConcepts(final String ownerUserName, final DBSession session){
		this.update("pickAllConcepts", ownerUserName, session);
	}
}