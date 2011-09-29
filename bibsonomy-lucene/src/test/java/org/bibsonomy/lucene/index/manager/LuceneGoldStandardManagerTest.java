package org.bibsonomy.lucene.index.manager;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGoldStandardManagerTest extends AbstractDatabaseManagerTest {
    private static LuceneGoldStandardManager<GoldStandardPublication> manager;
    private static LuceneResourceSearch<GoldStandardPublication> searcher;
    private static GoldStandardPublicationDatabaseManager goldStandardManager;

    private static final String INTER_HASH = "097248439469d8f5a1e7fad6b02cbfcd";

    private static final Set<String> allowedGroups = Collections.singleton(GroupID.PUBLIC.name().toLowerCase());
    private static final List<Integer> allowedGroupIds = Collections.singletonList(GroupID.PUBLIC.getId());
    
    /**
     * generates the gold standard publication index
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void initLucene() throws Exception {		
		// delete old indices
		manager = (LuceneGoldStandardManager<GoldStandardPublication>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneGoldStandardPublicationManager");
		searcher = manager.getSearcher();
	
		goldStandardManager = GoldStandardPublicationDatabaseManager.getInstance();
    }
    
    @Before
    public void resetDatabaseAndIndex() {
    	final List<LuceneResourceIndex<GoldStandardPublication>> resourceIndices = manager.getResourceIndeces();
		for (final LuceneResourceIndex<?> index : resourceIndices) {
		    index.deleteIndex();
		}
		/*
		 * reset database
		 */
		initDatabase();
		
		// create index
		manager.generateIndex(false);
    }
    
    @Test
    public void testInsert() {	
		int docCountBefore = manager.getResourceIndeces().get(0).getStatistics().getNumDocs();
		final Post<GoldStandardPublication> post = ModelUtils.generatePost(GoldStandardPublication.class);
		final GoldStandardPublication pub = post.getResource();
		pub.setTitle("Chuck Norris");
		pub.recalculateHashes();
		goldStandardManager.createPost(post, this.dbSession);
		
		updateIndex();
		
		assertEquals(docCountBefore + 1,  manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
		final ResultList<Post<GoldStandardPublication>> posts = searcher.getPosts("", "", "", allowedGroups, "Chuck*", "","", new LinkedList<String>(), null, null, null, 10, 0);
		assertEquals(1, posts.size());
		
		updateIndex();
		
		docCountBefore = manager.getResourceIndeces().get(0).getStatistics().getNumDocs();
		
		// insert a new 
		final Post<GoldStandardPublication> post2 = ModelUtils.generatePost(GoldStandardPublication.class);
		pub.setTitle("On the Scalability of Multidimensional Databases");
		pub.recalculateHashes();
		goldStandardManager.createPost(post2, this.dbSession);
		
		updateIndex();
		assertEquals(docCountBefore + 1,  manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
    }

    /**
     *  FIXME: fails too often, please fix!
     */
    @Test
    @Ignore
    public void testUpdate() throws PersonListParserException {
		final int docCountBefore = manager.getResourceIndeces().get(0).getStatistics().getNumDocs();
	
		final Post<GoldStandardPublication> post = goldStandardManager.getPostDetails("", INTER_HASH, "", allowedGroupIds, this.dbSession);
		post.getResource().setAuthor(PersonNameUtils.discoverPersonNames("luceneTest")); // changes the interhash!!
	
		goldStandardManager.updatePost(post, INTER_HASH, PostUpdateOperation.UPDATE_ALL, this.dbSession, new User("testuser1"));
	
		post.getResource().recalculateHashes();
		final String newInterHash = post.getResource().getInterHash();
		// update index
		updateIndex();
		assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
		ResultList<Post<GoldStandardPublication>> posts = searcher.getPosts("", "", "", allowedGroups, "", "","lucene*", new LinkedList<String>(), null, null, null, 10, 0);
		assertEquals(1, posts.size());
	
		post.getResource().setAbstract("Lorem ipsum dolor logos mundus novus");
		goldStandardManager.updatePost(post, newInterHash, PostUpdateOperation.UPDATE_ALL, this.dbSession, new User("testuser1"));
	
		// update index
		updateIndex();
		assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
		posts = searcher.getPosts("", "", "", allowedGroups, "", "","lucene*", new LinkedList<String>(), null, null, null, 10, 0);
		assertEquals(1, posts.size());
	
		// update index second call
		updateIndex();
	
		assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
		
		// check if the new post is in the lucene index
		posts = searcher.getPosts("", "", "", allowedGroups, "", "","lucene*", new LinkedList<String>(), null, null, null, 10, 0);
		assertEquals(1, posts.size());
		
		updateIndex();
		updateIndex();
		assertEquals(docCountBefore, manager.getResourceIndeces().get(0).getStatistics().getNumDocs());
    }

    private static void updateIndex() {
    	// simulate a restart of the system
	    for (final LuceneResourceIndex<GoldStandardPublication> luceneResourceIndex : manager.getResourceIndeces()) {
			luceneResourceIndex.setLastLogDate(null);
			luceneResourceIndex.setLastTasId(null);
		}
    	
	    // update all indeces
		for (int i = 0; i < manager.getResourceIndeces().size(); i++) {
		    manager.updateAndReloadIndex();
		}
    }

    @AfterClass
    public static void resetIndexReader() {
    	manager.resetIndexReader();
    }
}
