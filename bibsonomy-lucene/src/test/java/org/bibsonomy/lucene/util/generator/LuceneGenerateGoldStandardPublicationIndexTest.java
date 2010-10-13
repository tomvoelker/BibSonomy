package org.bibsonomy.lucene.util.generator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.bibsonomy.lucene.LuceneTest;
import org.bibsonomy.lucene.index.LuceneResourceIndex;
import org.bibsonomy.lucene.index.manager.LuceneGoldStandardPublicationManager;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.model.GoldStandardPublication;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id: LuceneGenerateGoldStandardPublicationIndexTest.java,v 1.1
 *          2010-07-16 12:12:01 nosebrain Exp $
 */
public class LuceneGenerateGoldStandardPublicationIndexTest extends LuceneTest {
    
    private static LuceneGoldStandardPublicationManager manager;

    @BeforeClass
    public static void initLucene() throws Exception {
	JNDITestDatabaseBinder.bind();
	LuceneBase.initRuntimeConfiguration();
	
	manager = LuceneGoldStandardPublicationManager.getInstance();

	// delete old indices
	final String path = LuceneBase.getIndexBasePath() + "lucene_GoldStandardPublication-";
	final List<LuceneResourceIndex<GoldStandardPublication>> resourceIndices = manager.getResourceIndeces();
	for (final LuceneResourceIndex<GoldStandardPublication> index : resourceIndices) {
	    final File file = new File(path + index.getIndexId());
	    LuceneTest.deleteFile(file);
	}
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void generateIndex() throws Exception {
	final LuceneGenerateGoldStandardPublicationIndex generator = LuceneGenerateGoldStandardPublicationIndex.getInstance();
	generator.generateIndex();

	assertEquals(2, generator.indexWriter.numDocs());
	generator.shutdown();
    }
    
    @AfterClass
    public static void resetIndex() {
	for (final LuceneResourceIndex<GoldStandardPublication> index : manager.getResourceIndeces()) {
	    index.reset();
	}
    }
}
