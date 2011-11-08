package org.bibsonomy.lucene.index.analyzer;

import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * analyzer for normalizing diacritics (e.g. &auml; to a)
 * 
 * TODO: implement stopwords
 * TODO: implement reusableTokenStream
 * 
 * @author fei
 * @version $Id$
 *
 */
public final class WhiteSpaceLowerCaseFilteringAnalyzer extends Analyzer {
	/** set of stop words to filter out of queries */
	private Set<String> stopSet;
	
	/**
	 * constructor
	 */
	public WhiteSpaceLowerCaseFilteringAnalyzer() {
		stopSet = new TreeSet<String>();
	}
	
	/** 
	 * Constructs a {@link StandardTokenizer} 
	 * filtered by 
	 * 		a {@link StandardFilter}, 
	 * 		a {@link LowerCaseFilter} and 
	 *      a {@link StopFilter}. 
	 */
	@Override
	public TokenStream tokenStream(final String fieldName, final Reader reader) { 
		return new LowerCaseFilter(Version.LUCENE_30, new WhitespaceTokenizer(Version.LUCENE_30, reader));
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
