/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import org.apache.lucene.util.Version;

/**
 * analyzer for normalizing diacritics (e.g. &auml; to a)
 * 
 * @author fei
 */
public final class DiacriticsLowerCaseFilteringAnalyzer extends Analyzer {
	
	private static final Version VERSION_LUCENE = Version.LUCENE_48;
	
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
	public Set<String> getStopSet() {
		return stopSet;
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
		Tokenizer tokenizer = new StandardTokenizer(VERSION_LUCENE, reader); 
		TokenFilter filter = new StandardFilter(VERSION_LUCENE, tokenizer); 
		filter = new LowerCaseFilter(VERSION_LUCENE, filter); 
		filter = new StopFilter(VERSION_LUCENE, filter, StopFilter.makeStopSet(VERSION_LUCENE, stopSet.toArray(new String[0])));
		filter = new ASCIIFoldingFilter(filter); 
		return new TokenStreamComponents(tokenizer, filter); 
	}
}
