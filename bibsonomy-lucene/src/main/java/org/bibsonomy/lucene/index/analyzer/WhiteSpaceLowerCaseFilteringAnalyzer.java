package org.bibsonomy.lucene.index.analyzer;

import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

/**
 * analyzer for normalizing diacritics (e.g. &auml; to a)
 * 
 * TODO: implement stopwords
 * TODO: implement reusableTokenStream
 * 
 * @author fei
 *
 */
public final class WhiteSpaceLowerCaseFilteringAnalyzer extends Analyzer {
	
	private static final Version VERSION_LUCENE = Version.LUCENE_4_9;
	
	/** set of stop words to filter out of queries */
	private Set<String> stopSet;
	
	/**
	 * constructor
	 */
	public WhiteSpaceLowerCaseFilteringAnalyzer() {
		stopSet = new TreeSet<String>();
	}

	/**
	 * Constructs a {@link TokenStreamComponents} 
	 * filtered by 
	 * 		a {@link StandardFilter}, 
	 * 		a {@link LowerCaseFilter} and 
	 *      a {@link StopFilter}. 
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer tokenizer = new WhitespaceTokenizer(VERSION_LUCENE, reader);
		TokenFilter filter = new LowerCaseFilter(VERSION_LUCENE, tokenizer);
		return new TokenStreamComponents(tokenizer, filter);
	}

	/**
	 * @return the stopSet
	 */
	public Set<String> getStopSet() {
		return stopSet;
	}

	/**
	 * @param stopSet the stopSet to set
	 */
	public void setStopSet(final Set<String> stopSet) {
		this.stopSet = stopSet;
	}

}
