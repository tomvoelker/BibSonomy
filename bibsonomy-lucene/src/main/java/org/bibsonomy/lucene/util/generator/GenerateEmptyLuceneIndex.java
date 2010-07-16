package org.bibsonomy.lucene.util.generator;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.model.Resource;

/**
 * Generates empty index files for lucene. The path of the index files is configured 
 * in 'lucene.properties'
 *  
 * @author sst
 * @author fei
 * @version $Id$
 */
public class GenerateEmptyLuceneIndex {
    
    static {
	JNDITestDatabaseBinder.bind();
    }
    
    private final static List<LuceneGenerateResourceIndex<? extends Resource>> GENERATORS = Arrays.<LuceneGenerateResourceIndex<?>>asList(LuceneGenerateBibTexIndex.getInstance(), LuceneGenerateGoldStandardPublicationIndex.getInstance(), LuceneGenerateBookmarkIndex.getInstance());

    /**
     * generates a empty index foreach known index
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {	
	for (final LuceneGenerateResourceIndex<? extends Resource> generator : GENERATORS) {
	    generator.createEmptyIndex();
	    generator.shutdown();
	}
    }
	
}
