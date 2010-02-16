package org.bibsonomy.database.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.common.exceptions.database.DatabaseException;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
@Ignore //TODO: remove me when sql schema was updated
public class GoldStandardPublicationDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	private static final String WRONG_INTERHASH = "interhashorintrahashorhashor";
	private static final String INTERHASH_GOLD_1 = "d9eea4aa159d70ecfabafa0c91bbc9f0";
	private static final String INTERHASH_GOLD_2 = "ac6aa3ccb181e61801cefbc1401d409a";
	
	private GoldStandardPublicationDatabaseManager goldPubManager;
	
	/**
	 * sets the gold standard publication database manager
	 */
	@Before
	public void setGoldStandardPublicationManager() {
		this.goldPubManager = GoldStandardPublicationDatabaseManager.getInstance();
	}
	
	/**
	 * tests {@link GoldStandardPublicationDatabaseManager#createPost(Post, org.bibsonomy.database.util.DBSession)}
	 */
	@Test
	public void testCreatePost() {
		final String interhash = createGoldStandardPublication();
		
		this.deletePost(interhash);
	}

	/**
	 * @return
	 */
	private String createGoldStandardPublication() {
		assertFalse(this.pluginMock.isOnGoldStandardPublicationCreate());
		
		// create post
		final Post<GoldStandardPublication> post = this.generateGoldPublication();
		assertTrue(this.goldPubManager.createPost(post, this.dbSession));
		
		final String interhash = post.getResource().getInterHash();
		assertNotNull(this.goldPubManager.getPostDetails("", interhash, "", null, this.dbSession).getResource());
		
		assertTrue(this.pluginMock.isOnGoldStandardPublicationCreate());
		return interhash;
	}
	
	/**
	 * tests {@link GoldStandardPublicationDatabaseManager#createPost(Post, org.bibsonomy.database.util.DBSession)}
	 */
	@Test
	public void testUpdatePost() {
		assertFalse(this.pluginMock.isOnGoldStandardPublicationUpdate());
		assertFalse(this.pluginMock.isOnGoldStandardPublicationCreate());
		
		// create post
		final Post<GoldStandardPublication> post = this.generateGoldPublication();
		this.goldPubManager.createPost(post, this.dbSession);
		
		// test listeners
		assertTrue(this.pluginMock.isOnGoldStandardPublicationCreate());
		assertFalse(this.pluginMock.isOnGoldStandardPublicationUpdate());
		this.pluginMock.reset();
		
		// fetch post
		final GoldStandardPublication goldStandard = post.getResource();
		String interhash = goldStandard.getInterHash();
		assertNotNull(this.goldPubManager.getPostDetails("", interhash, "", null, this.dbSession).getResource());
		
		// change a value and update the gold standard
		final String newYear = "2010";
		goldStandard.setYear(newYear);
		goldStandard.recalculateHashes();
		
		this.goldPubManager.updatePost(post, interhash, null, this.dbSession);
		
		// test listeners
		assertFalse(this.pluginMock.isOnGoldStandardPublicationCreate());
		assertTrue(this.pluginMock.isOnGoldStandardPublicationUpdate());
		
		interhash = goldStandard.getInterHash();
		
		// delete gold standard
		final Post<GoldStandardPublication> postDetails = this.goldPubManager.getPostDetails("testuser1", interhash, "", null, this.dbSession);
		assertEquals(newYear, postDetails.getResource().getYear());
		
		this.deletePost(interhash);
	}
	
	/**
	 * tests getPostDetails including references
	 */
	@Test
	public void testReferences() {
		final Post<GoldStandardPublication> post = this.goldPubManager.getPostDetails("", INTERHASH_GOLD_1, "", null, this.dbSession);
		final Set<BibTex> references = post.getResource().getReferences();
		assertEquals(1, references.size());
		final BibTex ref1 = references.iterator().next();
		assertEquals(INTERHASH_GOLD_2, ref1.getInterHash());
	}
	
	/**
	 * tests if duplications can be created
	 */
	@Test(expected = DatabaseException.class)
	public void testCreateDuplicate() {
		final Post<GoldStandardPublication> post = this.goldPubManager.getPostDetails("", INTERHASH_GOLD_1, "", null, this.dbSession);
		this.goldPubManager.createPost(post, this.dbSession);
	}
	
	@Test(expected = DatabaseException.class)
	public void testUpdateUnkownPost() {
		final Post<GoldStandardPublication> post = this.goldPubManager.getPostDetails("", INTERHASH_GOLD_1, "", null, this.dbSession);
		this.goldPubManager.updatePost(post, WRONG_INTERHASH, null, this.dbSession);
	}
	
	@Test(expected = DatabaseException.class)
	public void testUpdatePostToPostInDB() {
		final Post<GoldStandardPublication> post = this.goldPubManager.getPostDetails("", INTERHASH_GOLD_1, "", null, this.dbSession);
		this.goldPubManager.updatePost(post, INTERHASH_GOLD_2, null, this.dbSession);
	}	
	
	/**
	 * tests {@link GoldStandardDatabaseManager#addReferencesToPost(Post, Set, org.bibsonomy.database.util.DBSession)} and
	 * {@link GoldStandardDatabaseManager#removeReferencesFromPost(String, String, Set, org.bibsonomy.database.util.DBSession)}
	 */
	@Test
	public void testAddRemoveReferences() {
		final String interhash = this.createGoldStandardPublication();
		
		final Set<GoldStandardPublication> referencesToAdd = new HashSet<GoldStandardPublication>();
		referencesToAdd.add(this.goldPubManager.getPostDetails("", interhash, "", null, this.dbSession).getResource());
		assertTrue(this.goldPubManager.addReferencesToPost("", INTERHASH_GOLD_1, referencesToAdd, this.dbSession));
		
		final Post<GoldStandardPublication> post = this.goldPubManager.getPostDetails("", INTERHASH_GOLD_1, "", null, this.dbSession);
		assertEquals(1 + 1, post.getResource().getReferences().size());
		
		assertTrue(this.goldPubManager.removeReferencesFromPost("", INTERHASH_GOLD_1, referencesToAdd, this.dbSession));
		
		final Post<GoldStandardPublication> postAfterRemove = this.goldPubManager.getPostDetails("", INTERHASH_GOLD_1, "", null, this.dbSession);
		assertEquals(1, postAfterRemove.getResource().getReferences().size());
		
		this.deletePost(interhash);
		
	}
	
	/**
	 * tests if the plugin updates the hashes after updating the references and if the plugin deletes the references when post was deleted 
	 */
	@Test
	public void testUpdateReferencePlugin() {
		final Post<GoldStandardPublication> post = this.goldPubManager.getPostDetails("", INTERHASH_GOLD_1, "", null, this.dbSession);
		
		final GoldStandardPublication standard = post.getResource();
		assertEquals(1, standard.getReferences().size());
		
		standard.setYear("2010");
		standard.recalculateHashes();
		this.goldPubManager.updatePost(post, INTERHASH_GOLD_1, null, this.dbSession);
		
		assertTrue(this.pluginMock.isOnGoldStandardPublicationUpdate());
		
		final String newInterHash = standard.getInterHash();
		final Post<GoldStandardPublication> afterUpdate = this.goldPubManager.getPostDetails("", newInterHash, "", null, this.dbSession);
		assertEquals(1, afterUpdate.getResource().getReferences().size());
	}
	
	/**
	 * tests {@link GoldStandardDatabaseManager#getPosts(org.bibsonomy.database.params.GenericParam, org.bibsonomy.database.util.DBSession)}
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testChain() {
		this.goldPubManager.getPosts(new BibTexParam(), this.dbSession);
	}
	
	private void deletePost(final String interhash) {
		this.pluginMock.reset();
		assertFalse(this.pluginMock.isOnGoldStandardPublicationDelete());
		
		// delete post
		this.goldPubManager.deletePost("", interhash, this.dbSession);
		assertNull(this.goldPubManager.getPostDetails("", interhash, "", null, this.dbSession));
		
		assertTrue(this.pluginMock.isOnGoldStandardPublicationDelete());
	}

	private Post<GoldStandardPublication> generateGoldPublication() {
		final Post<GoldStandardPublication> post = new Post<GoldStandardPublication>();

		// groups
		final Group group = new Group();
		group.setDescription(null);
		group.setName("public");
		post.getGroups().add(group);
		
		post.setContentId(null);
		post.setDescription("trallalla");
		post.setDate(new Date());
		post.setUser(ModelUtils.getUser());
		post.setResource(ModelUtils.getGoldStandardPublication());

		return post;
	}
}