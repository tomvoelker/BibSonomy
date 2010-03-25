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
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
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
		this.tag = new Tag(this.getClass().getSimpleName());
		this.subTag = new Tag(this.getClass().getSimpleName() + "-sub");
		this.superTag = new Tag(this.getClass().getSimpleName() + "-super");
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
	@Test
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
	@Test
	public void testDeleteRelation() {
		assertFalse(this.pluginMock.isOnTagRelationDelete());
		final List<TagIndex> tagIndex = new ArrayList<TagIndex>();
		final List<Integer> visibleGroupIDs = new ArrayList<Integer>();
		tagIndex.add(new TagIndex("researcher", 1));
		
		final int countBefore = bibTexDb.getPostsByConceptForUser("jaeschke", "jaeschke", visibleGroupIDs, tagIndex, false, 100, 0, null, this.dbSession).size();
		this.tagRelDb.deleteRelation("researcher", "shannon", "jaeschke", this.dbSession);
		final int countAfter = bibTexDb.getPostsByConceptForUser("jaeschke", "jaeschke", visibleGroupIDs, tagIndex, false, 100, 0, null, this.dbSession).size();
		log.debug("before: " + countBefore);
		log.debug("after: " + countAfter);
		assertTrue(countBefore > countAfter);

		assertTrue(this.pluginMock.isOnTagRelationDelete());
	}

	/**
	 * get picked concepts for User
	 */
	@Test
	public void getPickedConceptsForUser() {
		final List<Tag> relations = this.tagRelDb.getPickedConceptsForUser("testuser3", this.dbSession);
		// testuser3 has three concepts but only two are picked
		assertEquals(2, relations.size());
	}

	/**
	 * retrieve all concepts for a user
	 */
	@Test
	public void getAllConceptsForUser() {
		final TagRelationParam param = LogicInterfaceHelper.buildParam(TagRelationParam.class, GroupingEntity.USER, "testuser1", null, null, null, 0, Integer.MAX_VALUE, null, null, new User());
		final List<Tag> relations = this.tagRelDb.getAllConceptsForUser(param, this.dbSession);
		// testuser1 has three concepts
		assertEquals(3, relations.size());
	}

	/**
	 * retrieve all global concepts including subTags and subTagCount
	 */
	@Test
	public void testGetAllConcepts() {
		final List<Tag> concepts = this.tagRelDb.getAllConcepts(this.dbSession);
		// there are 3 concepts from users listed in table 'user': 
		// "linux", "programming", and "suchmaschine"
		assertEquals(3, concepts.size());
		// the concept "programming" has the subTag "C" from two different users and from one spammer
		assertEquals(2, concepts.get(1).getSubTags().get(1).getUsercount());
	}

	/**
	 * Retrieves a global concept by name
	 */
	@Test
	public void testGetGlobalConceptByName() {
		final Tag concept = this.tagRelDb.getGlobalConceptByName("programming", this.dbSession);
		assertEquals(4, concept.getSubTags().size());
	}
}