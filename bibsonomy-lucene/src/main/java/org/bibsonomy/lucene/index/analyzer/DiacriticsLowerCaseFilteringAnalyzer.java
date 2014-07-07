package org.bibsonomy.lucene.index.analyzer;

import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

/**
 * analyzer for normalizing diacritics (e.g. &auml; to a)
 * 
 * TODO: implement stopwords
 * TODO: implement reusableTokenStream
 * 
 * @author fei
 */
public final class DiacriticsLowerCaseFilteringAnalyzer extends Analyzer {
	/** set of stop words to filter out of queries */
	private Set<String> stopSet;
	
	/**
	 * constructor
	 */
	public DiacriticsLowerCaseFilteringAnalyzer() {
		stopSet = new TreeSet<String>();
	}

	/**
	 * @return the stopSet
	 */
	public CharArraySet getStopSet() {
		/*
		 * FIXME
		 */
		return CharArraySet.copy(Version.LUCENE_30, stopSet);
	}

	/**
	 * @param stopSet the stopSet to set
	 */
	public void setStopSet(Set<String> stopSet) {
		this.stopSet = stopSet;
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
		Tokenizer tokenizer = new StandardTokenizer(Version.LUCENE_30, reader); 
		TokenFilter filter = new StandardFilter(Version.LUCENE_30, tokenizer); 
		filter = new LowerCaseFilter(Version.LUCENE_30, tokenizer); 
		filter = new StopFilter(Version.LUCENE_30, tokenizer, getStopSet());
		((StopFilter) filter).setEnablePositionIncrements(true);
		filter = new ASCIIFoldingFilter(tokenizer); 
		return new TokenStreamComponents(tokenizer, filter);
	}
}
