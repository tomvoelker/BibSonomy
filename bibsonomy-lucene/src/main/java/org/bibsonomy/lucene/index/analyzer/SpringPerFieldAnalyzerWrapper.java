package org.bibsonomy.lucene.index.analyzer;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.util.ValidationUtils;
import org.springframework.beans.factory.BeanFactory;

/**
 * this field wrapps lucene's PerFieldAnalyzerWrapper for making it
 * configurable via spring
 * 
 * @author fei
 *
 */
public class SpringPerFieldAnalyzerWrapper extends Analyzer {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(SpringPerFieldAnalyzerWrapper.class);

	/** singleton pattern's instance reference */
	private static SpringPerFieldAnalyzerWrapper instance;

	/** bean factory */
	private static BeanFactory beanFactory;
	
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
	 * static initialization
	 */
	static {
		beanFactory = LuceneSpringContextWrapper.getBeanFactory();
	}
	
	private SpringPerFieldAnalyzerWrapper() {
		this.setFullTextSearchAnalyzer(null);
		this.defaultAnalyzer = null;
		this.fieldMap = null;
	}
	
	/**
	 * singleton pattern's pre-initialization instantiation method  
	 * 
	 * @return
	 */
	public static SpringPerFieldAnalyzerWrapper getInstance() {
		if( instance==null ) {
			instance = (SpringPerFieldAnalyzerWrapper)beanFactory.getBean("luceneFieldWrapperAnalyzer");
		}
		return instance;
	}

	/**
	 * singleton pattern's pre-initialization instantiation method  
	 * 
	 * @return
	 */
	public static SpringPerFieldAnalyzerWrapper getPreInitInstance() {
		if( instance==null ) {
			instance = new SpringPerFieldAnalyzerWrapper();
		}
		return instance;
	}
	
	/**
	 * initialize internal data structures
	 */
	private void init() {
		// initialize tokenizer if all necessary properties are set
		if( (this.defaultAnalyzer!=null) && (this.fieldMap!=null) ) {
			this.analyzer = new PerFieldAnalyzerWrapper(getDefaultAnalyzer());
			
			for( String fieldName : fieldMap.keySet() ) {
				analyzer.addAnalyzer(fieldName, (Analyzer)fieldMap.get(fieldName));
			}
		}
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return this.analyzer.tokenStream(fieldName, reader);
	}

	public void setFieldMap(Map<String, Object> fieldMap) {
		this.fieldMap = fieldMap;
		init();
	}

	public Map<String, Object> getFieldMap() {
		return fieldMap;
	}

	public void setDefaultAnalyzer(Analyzer defaultAnalyzer) {
		this.defaultAnalyzer = defaultAnalyzer;
		init();
	}

	public Analyzer getDefaultAnalyzer() {
		return defaultAnalyzer;
	}

	public void setPropertyMap(Map<String,Map<String,Object>> propertyMap) {
		this.propertyMap = propertyMap;
		
		// update the fieldmap
		this.fieldMap = new HashMap<String,Object>();
		
		for( String propertyName : propertyMap.keySet() ) {
			String fieldName       = (String)propertyMap.get(propertyName).get(LuceneBase.CFG_LUCENENAME);
			Analyzer fieldAnalyzer = (Analyzer)propertyMap.get(propertyName).get(LuceneBase.CFG_ANALYZER);
			if( ValidationUtils.present(fieldAnalyzer) )
				this.fieldMap.put(fieldName, fieldAnalyzer);
		}
		
		// set full text search analyzer
		if( this.fullTextSearchAnalyzer!=null )
			fieldMap.put(LuceneBase.FLD_MERGEDFIELDS, this.fullTextSearchAnalyzer);
	}

	public Map<String,Map<String,Object>> getPropertyMap() {
		return propertyMap;
	}

	public void setFullTextSearchAnalyzer(Analyzer fullTextSearchAnalyzer) {
		this.fullTextSearchAnalyzer = fullTextSearchAnalyzer;
		// update fieldmap
		if( this.fieldMap!=null ) {
			fieldMap.put(LuceneBase.FLD_MERGEDFIELDS, this.fullTextSearchAnalyzer);
		}
	}

	public Analyzer getFullTextSearchAnalyzer() {
		return fullTextSearchAnalyzer;
	}
	
}
