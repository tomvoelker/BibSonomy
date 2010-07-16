package org.bibsonomy.lucene.util.generator;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.lucene.LuceneTest;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGenerateGoldStandardPublicationIndexTest extends LuceneTest {
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void generateIndex() throws Exception {
		final LuceneGenerateGoldStandardPublicationIndex generator = LuceneGenerateGoldStandardPublicationIndex.getInstance();
		generator.generateIndex();
		
		assertEquals(2, generator.indexWriter.maxDoc());
		generator.shutdown();
	}
}
