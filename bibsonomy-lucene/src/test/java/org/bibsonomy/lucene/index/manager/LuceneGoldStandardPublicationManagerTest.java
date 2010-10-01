package org.bibsonomy.lucene.index.manager;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.lucene.search.LuceneSearchGoldStandardPublications;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.generator.LuceneGenerateGoldStandardPublicationIndex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardPublicationManagerTest extends AbstractDatabaseManagerTest {
	private static LuceneGoldStandardPublicationManager manager;
	private static GoldStandardPublicationDatabaseManager goldStandardManager;

	private static final String INTER_HASH = "d9eea4aa159d70ecfabafa0c91bbc9f0";

	/**
	 * generates the gold standard publication index
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void initLucene() throws Exception {
		JNDITestDatabaseBinder.bind();
		LuceneBase.initRuntimeConfiguration();
		
		goldStandardManager = GoldStandardPublicationDatabaseManager.getInstance();
		manager = LuceneGoldStandardPublicationManager.getInstance();

		// create index
		final LuceneGenerateGoldStandardPublicationIndex generator = LuceneGenerateGoldStandardPublicationIndex.getInstance();
		generator.generateIndex();
		generator.shutdown();	
	}

	@Test
	@Ignore // TODO 
	public void testUpdate() throws IOException {
		// TODODZ: goldstandards are always public
		final Set<String> allowedGroups = new TreeSet<String>();
		allowedGroups.add(GroupID.PUBLIC.name());

		final Post<GoldStandardPublication> post = goldStandardManager.getPostDetails("", INTER_HASH, "", new LinkedList<Integer>(), this.dbSession);
		post.getResource().setAuthor("luceneTest");

		goldStandardManager.updatePost(post, INTER_HASH, PostUpdateOperation.UPDATE_ALL, this.dbSession);

		post.getResource().recalculateHashes();
		final String newInterHash = post.getResource().getInterHash();

		// update index
		updateIndex();

		post.getResource().setAbstract("Lorem ipsum dolor logos mundus novus");
		goldStandardManager.updatePost(post, newInterHash, PostUpdateOperation.UPDATE_ALL, this.dbSession);

		// update index
		updateIndex();

		// update index second call
		updateIndex();

		assertEquals(2, manager.getResourceIndeces().get(0).getNumberOfStoredDocuments());

		final ResultList<Post<GoldStandardPublication>> posts = LuceneSearchGoldStandardPublications.getInstance().getPosts("", "", "", allowedGroups, "", "", "luceneTest", new LinkedList<String>(), null, null, null, 10, 0);
		assertEquals(1, posts.size());
	}

	private static void updateIndex() {		
		for (int i = 0; i < LuceneBase.getRedundantCnt(); i++) {
			manager.updateAndReloadIndex();
		}
	}

	@AfterClass
	public static void resetIndexReader() {
		manager.resetIndexReader();
	}

	@Test
	@Ignore // TODO
	public void testInsert() {

	}
}
