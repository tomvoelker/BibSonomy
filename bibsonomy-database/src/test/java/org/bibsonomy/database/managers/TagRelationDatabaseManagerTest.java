package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.testutil.DatabasePluginMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class TagRelationDatabaseManagerTest extends AbstractDatabaseManagerTest {

	private static final Log log = LogFactory.getLog(TagRelationDatabaseManagerTest.class);
	private Tag tag;
	private Tag subTag;
	private Tag superTag;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.tag = new Tag(this.getClass().getName());
		this.subTag = new Tag(this.getClass().getName() + "-sub");
		this.superTag = new Tag(this.getClass().getName() + "-super");
		this.tag.setSubTags(Arrays.asList(new Tag[] { this.subTag }));
		this.subTag.setSuperTags(Arrays.asList(new Tag[] { this.tag }));
		this.tag.setSuperTags(Arrays.asList(new Tag[] { this.superTag }));
		this.superTag.setSubTags(Arrays.asList(new Tag[] { this.tag }));
	}

	/**
	 * tests insertRelations (new relations)
	 */
	@Test
	public void testInsertNewRelations() {
		final Integer newId1 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		this.tagRelDb.insertRelations(tag, "test-user", this.dbSession);
		final Integer newId2 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		assertEquals(newId1 + 3, newId2);
	}

	/**
	 * tests insertRelations (existing relations)
	 */
	@Ignore
	public void testInsertExistingRelations() {
		final Integer newId1 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		this.tagRelDb.insertRelations(tag, "test-user", this.dbSession);
		this.tagRelDb.insertRelations(tag, "test-user", this.dbSession);
		final Integer newId2 = this.generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		assertEquals(newId1 + 3, newId2);
	}

	/**
	 * tests deleteRelation
	 */
	@Ignore
	public void testDeleteRelation() {
		// FIXME: this boilerplate code could be removed with a DI-framework
		// (i.e. next three lines)
		final DatabasePluginMock plugin = new DatabasePluginMock();
		DatabasePluginRegistry.getInstance().clearPlugins();
		DatabasePluginRegistry.getInstance().add(plugin);
		assertFalse(plugin.isOnTagRelationDelete());
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		final ArrayList<Integer> visibleGroupIDs = new ArrayList<Integer>();
		tagIndex.add(new TagIndex("researcher", 1));
		
		final int countBefore = bibTexDb.getPostsByConceptForUser("jaeschke", "jaeschke", visibleGroupIDs, tagIndex, false, 100, 0, null, this.dbSession).size();
		this.tagRelDb.deleteRelation("researcher", "shannon", "jaeschke", this.dbSession);
		final int countAfter = bibTexDb.getPostsByConceptForUser("jaeschke", "jaeschke", visibleGroupIDs, tagIndex, false, 100, 0, null, this.dbSession).size();
		log.debug("before: " + countBefore);
		log.debug("after: " + countAfter);
		assertTrue(countBefore > countAfter);

		assertTrue(plugin.isOnTagRelationDelete());
	}

	/**
	 * get picked concepts for User
	 */
	@Ignore
	public void getPickedConceptsForUser() {
		final List<Tag> relations = this.tagRelDb.getPickedConceptsForUser("hotho", this.dbSession);
		// hotho has six concepts
		assertEquals(6, relations.size());
	}

	/**
	 * retrieve all concepts for a user
	 */
	@Ignore
	public void getAllConceptsForUser() {
		final TagRelationParam param = LogicInterfaceHelper.buildParam(TagRelationParam.class, GroupingEntity.USER, "hotho", null, null, null, 0, Integer.MAX_VALUE, null, null, new User());
		final List<Tag> relations = this.tagRelDb.getAllConceptsForUser(param, this.dbSession);
		// hotho has six concepts
		assertEquals(6, relations.size());
	}

	/**
	 * Retrive all global concepts
	 */
	@Ignore
	public void testGetAllConcepts() {
		final List<Tag> concepts = this.tagRelDb.getAllConcepts(this.dbSession);
		assertEquals(50, concepts.size());
	}

	/**
	 * Retrives a global cocept by name
	 */
	@Ignore
	public void testGetGlobalConceptByName() {
		final Tag concept = this.tagRelDb.getGlobalConceptByName("programming", this.dbSession);
		assertEquals(9, concept.getSubTags().size());
	}
}