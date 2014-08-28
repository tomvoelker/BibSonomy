package org.bibsonomy.lucene.index.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

/**
 * @author fei
 */
public final class SimpleKeywordAnalyzer extends Analyzer {

	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.Analyzer#createComponents(java.lang.String, java.io.Reader)
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer tokenizer = new CharTokenizer(Version.LUCENE_48, reader) {
			@Override
			protected boolean isTokenChar(int c) {
				return true;
			}
		};
		return new TokenStreamComponents(tokenizer);
	}
	
}
