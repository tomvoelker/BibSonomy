package org.bibsonomy.lucene.index.manager;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManagerTest;
import org.bibsonomy.lucene.LuceneTest;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.search.LuceneSearchGoldStandardPublications;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.generator.LuceneGenerateGoldStandardPublicationIndex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardPublicationManagerTest extends AbstractDatabaseManagerTest {
    private static LuceneGoldStandardPublicationManager manager;
    private static GoldStandardPublicationDatabaseManager goldStandardManager;

    private static final String INTER_HASH = "d9eea4aa159d70ecfabafa0c91bbc9f0";

    private static final  Set<String> allowedGroups = Collections.singleton(GroupID.PUBLIC.name().toLowerCase());
    
    /**
     * generates the gold standard publication index
     * 
     * @throws Exception
     */
    @Before
    public void initLucene() throws Exception {
	JNDITestDatabaseBinder.bind();
	LuceneBase.initRuntimeConfiguration();
	
	// delete old indices
	final String path = LuceneBase.getIndexBasePath() + "lucene_GoldStandardPublication-";
	manager = LuceneGoldStandardPublicationManager.getInstance();
	final List<LuceneResourceIndex<GoldStandardPublication>> resourceIndices = manager.getResourceIndeces();
	
	for (final LuceneResourceIndex<GoldStandardPublication> index : resourceIndices) {
	    final File folder = new File(path + index.getIndexId());
	    LuceneTest.deleteFile(folder);
	    /*
	     * XXX: can't check if the folder was deleted successfully
	     * deleting folders on nfs network drives fails 
	     * (.nfs files are locked)
	     */
	    /*final boolean exists = folder.exists();
	    final boolean delete = LuceneTest.deleteFile(folder);
	    assertTrue(!exists || delete);*/
	    
	}

	goldStandardManager = GoldStandardPublicationDatabaseManager.getInstance();
	
	/*
	 * reset database
	 */
	initDatabase();
	
	// create index
	final LuceneGenerateGoldStandardPublicationIndex generator = LuceneGenerateGoldStandardPublicationIndex.getInstance();
	generator.generateIndex();
	generator.shutdown();
	
	// now reset the index; was disable while spring initializing
	for (final LuceneResourceIndex<GoldStandardPublication> index : manager.getResourceIndeces()) {
	    index.reset();
	}

	GoldStandardPublicationDatabaseManagerTest.initDatabase();
    }
    
    @Test
    public void testInsert() {	
	final int docCountBefore = manager.getResourceIndeces().get(0).getStatistics().getNumDocs();
	final Post<GoldStandardPublication> post = ModelUtils.generatePost(GoldStandardPublication.class);
	final GoldStandardPublication pub = post.getResource();
	pub.setTitle("Chuck Norris");
	pub.recalculateHashes();
	goldStandardManager.createPost(post, this.dbSession);
	
	updateIndex();
	
	assertEquals(docCountBefore + 1,  manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
	final ResultList<Post<GoldStandardPublication>> posts = LuceneSearchGoldStandardPublications.getInstance().getPosts("", "", "", allowedGroups, "Chuck*", "","", new LinkedList<String>(), null, null, null, 10, 0);
	assertEquals(1, posts.size());
    }

    @Test
    public void testUpdate() {
	final int docCountBefore = manager.getResourceIndeces().get(0).getStatistics().getNumDocs();

	final Post<GoldStandardPublication> post = goldStandardManager.getPostDetails("", INTER_HASH, "", new LinkedList<Integer>(), this.dbSession);
	post.getResource().setAuthor("luceneTest"); // changes the interhash!!

	goldStandardManager.updatePost(post, INTER_HASH, PostUpdateOperation.UPDATE_ALL, this.dbSession);

	post.getResource().recalculateHashes();
	final String newInterHash = post.getResource().getInterHash();
	// update index
	updateIndex();
	assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
	ResultList<Post<GoldStandardPublication>> posts = LuceneSearchGoldStandardPublications.getInstance().getPosts("", "", "", allowedGroups, "", "","lucene*", new LinkedList<String>(), null, null, null, 10, 0);
	assertEquals(1, posts.size());

	post.getResource().setAbstract("Lorem ipsum dolor logos mundus novus");
	goldStandardManager.updatePost(post, newInterHash, PostUpdateOperation.UPDATE_ALL, this.dbSession);

	// update index
	updateIndex();
	assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
	posts = LuceneSearchGoldStandardPublications.getInstance().getPosts("", "", "", allowedGroups, "", "","lucene*", new LinkedList<String>(), null, null, null, 10, 0);
	assertEquals(1, posts.size());

	// update index second call
	updateIndex();

	assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
	
	// check if the new post is in the lucene index
	posts = LuceneSearchGoldStandardPublications.getInstance().getPosts("", "", "", allowedGroups, "", "","lucene*", new LinkedList<String>(), null, null, null, 10, 0);
	assertEquals(1, posts.size());
	
	updateIndex();
	updateIndex();
	assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
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
}
