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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.util.LuceneBase;

/**
 * this field wrapps lucene's PerFieldAnalyzerWrapper for making it
 * configurable via spring
 * 
 * @author fei
 */
public final class SpringPerFieldAnalyzerWrapper extends AnalyzerWrapper {	
	/** map configuring the index */
	private Map<String,Map<String,Object>> propertyMap;

	/** map configuring the fieldwrapper */
	private Map<String, Object> fieldMap;
	
	/** default analyzer */
	private Analyzer defaultAnalyzer;
	
	/** full text search analyzer */
	private Analyzer fullTextSearchAnalyzer;
	
	private SpringPerFieldAnalyzerWrapper() {
		super(Analyzer.PER_FIELD_REUSE_STRATEGY);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.AnalyzerWrapper#getWrappedAnalyzer(java.lang.String)
	 */
	@Override
	protected Analyzer getWrappedAnalyzer(String fieldName) {
		Analyzer analyzer = (Analyzer) fieldMap.get(fieldName);
		if (analyzer != null) {
			return analyzer;
		}
		return defaultAnalyzer;
	}
	
	/**
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(final Map<String, Object> fieldMap) {
		this.fieldMap = fieldMap;
	}

	/**
	 * @return the fieldMap
	 */
	public Map<String, Object> getFieldMap() {
		return fieldMap;
	}

	/**
	 * @param defaultAnalyzer the defaultAnalyzer to set
	 */
	public void setDefaultAnalyzer(final Analyzer defaultAnalyzer) {
		this.defaultAnalyzer = defaultAnalyzer;
	}

	/**
	 * @return defaultAnalyzer
	 */
	public Analyzer getDefaultAnalyzer() {
		return defaultAnalyzer;
	}

	/**
	 * @param propertyMap the propertyMap to set
	 */
	public void setPropertyMap(final Map<String,Map<String,Object>> propertyMap) {
		this.propertyMap = propertyMap;
		
		// update the fieldmap
		this.fieldMap = new HashMap<String, Object>();
		
		// TODO: use value entrySet iterator
		for (final String propertyName : propertyMap.keySet()) {
			final String fieldName = (String) propertyMap.get(propertyName).get(LuceneBase.CFG_LUCENENAME);
			final Analyzer fieldAnalyzer = (Analyzer) propertyMap.get(propertyName).get(LuceneBase.CFG_ANALYZER);
			if (present(fieldAnalyzer)) {
				this.fieldMap.put(fieldName, fieldAnalyzer);
			}
		}
		
		// set full text search analyzer
		if (this.fullTextSearchAnalyzer != null) {
			fieldMap.put(LuceneFieldNames.MERGED_FIELDS, this.fullTextSearchAnalyzer);
		}
	}

	/**
	 * @return the propertyMap
	 */
	public Map<String,Map<String,Object>> getPropertyMap() {
		return propertyMap;
	}

	/**
	 * @param fullTextSearchAnalyzer the fullTextSearchAnalyzer
	 */
	public void setFullTextSearchAnalyzer(final Analyzer fullTextSearchAnalyzer) {
		this.fullTextSearchAnalyzer = fullTextSearchAnalyzer;
		// update fieldmap
		if (this.fieldMap != null) {
			fieldMap.put(LuceneFieldNames.MERGED_FIELDS, this.fullTextSearchAnalyzer);
		}
	}
	
	/**
	 * 
	 * @return the fullTextSearchAnalyzer
	 */
	public Analyzer getFullTextSearchAnalyzer() {
		return fullTextSearchAnalyzer;
	}
	
}
