package org.bibsonomy.lucene.index.analyzer;

import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
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
 */
public class DiacriticsLowerCaseFilteringAnalyzer extends Analyzer {
	/** set of stop words to filter out of queries */
	private Set<String> stopSet;
	
	/**
	 * constructor
	 */
	public DiacriticsLowerCaseFilteringAnalyzer() {
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
	public TokenStream tokenStream(String fieldName, Reader reader) { 
		TokenStream result = new StandardTokenizer(Version.LUCENE_24, reader); 
		result = new StandardFilter(result); 
		result = new LowerCaseFilter(result); 
		result = new StopFilter(true, result, getStopSet()); 
		result = new ASCIIFoldingFilter(result); 
		return result; 
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
	public void setStopSet(Set<String> stopSet) {
		this.stopSet = stopSet;
	}
}
