package org.bibsonomy.lucene.index.analyzer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.bibsonomy.lucene.index.LuceneFieldNames;
import org.bibsonomy.lucene.util.LuceneBase;

/**
 * this field wrapps lucene's PerFieldAnalyzerWrapper for making it
 * configurable via spring
 * 
 * @author fei
 * @version $Id$
 */
public final class SpringPerFieldAnalyzerWrapper extends Analyzer {	
	/** map configuring the index */
	private Map<String,Map<String,Object>> propertyMap;

	/** map configuring the fieldwrapper */
	private Map<String, Object> fieldMap;
	
	/** default analyzer */
	private Analyzer defaultAnalyzer;
	
	/** full text search analyzer */
	private Analyzer fullTextSearchAnalyzer;

	/** we delegate to this analyzer */
	private PerFieldAnalyzerWrapper analyzer;
	
	/**
	 * initialize internal data structures
	 */
	private void init() {
		// initialize tokenizer if all necessary properties are set
		if ((this.defaultAnalyzer != null) && (this.fieldMap != null)) {
			this.analyzer = new PerFieldAnalyzerWrapper(getDefaultAnalyzer());
			
			for (final String fieldName : fieldMap.keySet()) {
				analyzer.addAnalyzer(fieldName, (Analyzer)fieldMap.get(fieldName));
			}
		}
	}

	@Override
	public TokenStream tokenStream(final String fieldName, final Reader reader) {
		return this.analyzer.tokenStream(fieldName, reader);
	}
	
	/**
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(final Map<String, Object> fieldMap) {
		this.fieldMap = fieldMap;
		init();
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
		init();
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
