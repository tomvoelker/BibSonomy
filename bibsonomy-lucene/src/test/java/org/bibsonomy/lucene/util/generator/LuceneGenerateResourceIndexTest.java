package org.bibsonomy.lucene.util.generator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bibsonomy.database.testutil.JNDIBinder;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.manager.LuceneGoldStandardManager;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGenerateResourceIndexTest {
    
    private static LuceneGoldStandardManager<GoldStandardPublication> manager;

    @SuppressWarnings("unchecked")
	@BeforeClass
    public static void initLucene() throws Exception {
    	JNDIBinder.bind();
		manager = (LuceneGoldStandardManager<GoldStandardPublication>) LuceneSpringContextWrapper.getBeanFactory().getBean("luceneGoldStandardPublicationManager");

		// initialize test database
		TestDatabaseLoader.getInstance().load();
		
		// delete old indices
		final List<LuceneResourceIndex<GoldStandardPublication>> resourceIndices = manager.getResourceIndeces();
		for (final LuceneResourceIndex<GoldStandardPublication> index : resourceIndices) {
			index.deleteIndex();
		}
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void generateIndex() throws Exception {
    	manager.generateIndex(false);

		assertEquals(2, manager.getStatistics().getNumDocs());
    }
    
    @AfterClass
    public static void resetIndex() {
    	for (final LuceneResourceIndex<GoldStandardPublication> index : manager.getResourceIndeces()) {
    		index.reset();
		}
    }
}
