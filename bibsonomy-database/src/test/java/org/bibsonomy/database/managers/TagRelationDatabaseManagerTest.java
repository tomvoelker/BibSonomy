package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class TagRelationDatabaseManagerTest extends AbstractDatabaseManagerTest {
	private static GeneralDatabaseManager generalDb;
	private static BibTexDatabaseManager bibTexDb;
	private static TagRelationDatabaseManager tagRelDb;
	
	/**
	 * inits manager
	 */
	@BeforeClass
	public static void setupManagers() {
		generalDb = GeneralDatabaseManager.getInstance();
		bibTexDb = BibTexDatabaseManager.getInstance();
		tagRelDb = TagRelationDatabaseManager.getInstance();
	}
	
	private Tag tag;
	private Tag subTag;
	private Tag superTag;
	
	/**
	 * tests up the tags
	 */
	@Before
	public void setUpTags() {
		this.tag = new Tag(this.getClass().getSimpleName());
		this.subTag = new Tag(this.getClass().getSimpleName() + "-sub");
		this.superTag = new Tag(this.getClass().getSimpleName() + "-super");
		this.tag.setSubTags(Arrays.asList(this.subTag));
		this.subTag.setSuperTags(Arrays.asList(this.tag));
		this.tag.setSuperTags(Arrays.asList(this.superTag));
		this.superTag.setSubTags(Arrays.asList(this.tag));
	}
	
	/**
	 * tests insertRelations (new relations)
	 */
	@Test
	public void testInsertNewRelations() {
		final Integer newId1 = generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		tagRelDb.insertRelations(tag, "test-user", this.dbSession);
		final Integer newId2 = generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		assertEquals(newId1 + 3, newId2);
	}

	/**
	 * tests insertRelations (existing relations)
	 */
	@Ignore
	@Test
	public void testInsertExistingRelations() {
		final Integer newId1 = generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
		tagRelDb.insertRelations(tag, "test-user", this.dbSession);
		tagRelDb.insertRelations(tag, "test-user", this.dbSession);
		final Integer newId2 = generalDb.getNewContentId(ConstantID.IDS_TAGREL_ID, this.dbSession);
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
		tagRelDb.deleteRelation("researcher", "shannon", "jaeschke", this.dbSession);
		final int countAfter = bibTexDb.getPostsByConceptForUser("jaeschke", "jaeschke", visibleGroupIDs, tagIndex, false, 100, 0, null, this.dbSession).size();
		assertTrue(countBefore > countAfter);

		assertTrue(this.pluginMock.isOnTagRelationDelete());
	}

	/**
	 * get picked concepts for User
	 */
	@Test
	public void getPickedConceptsForUser() {
		final List<Tag> relations = tagRelDb.getPickedConceptsForUser("testuser3", this.dbSession);
		// testuser3 has four concepts but only three are picked
		//("programming" and "Programming" are counted as two different concepts in this query!)
		assertEquals(3, relations.size());
	}
 
	/**
	 * retrieve all concepts for a user
	 */
	@Test
	public void getAllConceptsForUser() {
		final TagRelationParam param = LogicInterfaceHelper.buildParam(TagRelationParam.class, GroupingEntity.USER, "testuser1", null, null, null, 0, Integer.MAX_VALUE, null, null, new User());
		final List<Tag> relations = tagRelDb.getAllConceptsForUser(param, this.dbSession);
		// testuser1 has four concepts 
		//("linux" and "Linux" are counted as two different concepts in this query!)
		assertEquals(4, relations.size());
	}

	/**
	 * retrieve all global concepts including subTags and subTagCount
	 */
	@Test
	public void testGetAllConcepts() {
		final List<Tag> concepts = tagRelDb.getAllConcepts(this.dbSession);
		// there are 3 concepts from users listed in table 'user': 
		// "linux", "programming", and "suchmaschine"
		// (capital letters are not taken into account in this query) 
		assertEquals(3, concepts.size());
		// the concept "programming" has the subTag "C" from two different users and from one spammer
		assertEquals(2, concepts.get(1).getSubTags().get(1).getUsercount());
	}

	/**
	 * Retrieves a global concept by name
	 */
	@Test
	public void testGetGlobalConceptByName() {
		final Tag concept = tagRelDb.getGlobalConceptByName("programming", this.dbSession);
		assertEquals(4, concept.getSubTags().size());
	}

	/**
	 * Tests if updating a tagtagrelation works properly
	 *
	 */
	//TODO does this test changes the testdb also for other tests ?
	@Test
	public void testUpdateTagRelations() {
		Tag a = new Tag(".net");
		Tag b = new Tag("java");
		User user = new User("testuser1");
		TagRelationParam before = new TagRelationParam();
		before.setLowerTagName(".net");
		before.setUpperTagName("programming");
		before.setOwnerUserName("testuser1");
		

		TagRelationParam after = new TagRelationParam();
		after.setLowerTagName("java");
		after.setUpperTagName("programming");
		after.setOwnerUserName("testuser1");
		
		assertTrue(tagRelDb.isRelationPresent(before, this.dbSession));
		assertTrue(tagRelDb.isRelationPresent(after, this.dbSession));
		
		tagRelDb.updateTagRelations(user, a, b, this.dbSession);
		
		assertFalse(tagRelDb.isRelationPresent(before, this.dbSession));
		assertTrue(tagRelDb.isRelationPresent(after, this.dbSession));
		

		a = new Tag("java");
		b = new Tag("haskel");
		user = new User("testuser1");
		before = new TagRelationParam();
		before.setLowerTagName("java");
		before.setUpperTagName("programming");
		before.setOwnerUserName("testuser1");
		

		TagRelationParam otherUser = new TagRelationParam();
		otherUser.setLowerTagName("java");
		otherUser.setUpperTagName("programming");
		otherUser.setOwnerUserName("testuser2");
		

		after = new TagRelationParam();
		after.setLowerTagName("haskel");
		after.setUpperTagName("programming");
		after.setOwnerUserName("testuser1");
		
		assertTrue(tagRelDb.isRelationPresent(before, this.dbSession));
		assertFalse(tagRelDb.isRelationPresent(after, this.dbSession));
		assertTrue(tagRelDb.isRelationPresent(otherUser, this.dbSession));
		
		tagRelDb.updateTagRelations(user, a, b, this.dbSession);
		
		assertFalse(tagRelDb.isRelationPresent(before, this.dbSession));
		assertTrue(tagRelDb.isRelationPresent(after, this.dbSession));
		assertTrue(tagRelDb.isRelationPresent(otherUser, this.dbSession));
		
		
	}
}