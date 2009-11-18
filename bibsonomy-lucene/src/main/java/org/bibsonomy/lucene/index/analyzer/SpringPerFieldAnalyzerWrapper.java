package org.bibsonomy.lucene.index.analyzer;

import java.io.Reader;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringPerFieldAnalyzerWrapper extends Analyzer {
	private static final Log log = LogFactory.getLog(SpringPerFieldAnalyzerWrapper.class);

	/** spring bean factory for initializing instances */
	private static BeanFactory beanFactory;
	
	/** we delegate to this analyzer */
	private PerFieldAnalyzerWrapper analyzer;

	/**
	 * static initialization
	 */
	static {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		        new String[] {"LuceneIndexConfig.xml"});

		beanFactory = context;
	}
	
	@SuppressWarnings("unchecked")
	public SpringPerFieldAnalyzerWrapper() {
		Analyzer defaultAnalyzer = (Analyzer)beanFactory.getBean("luceneDefaultAnalyzer");
		this.analyzer = new PerFieldAnalyzerWrapper(defaultAnalyzer);
		
		Map<String,Object> fieldMap = (Map<String,Object>)beanFactory.getBean("lucenePostFieldAnalyzer");
		
		for( String fieldName : fieldMap.keySet() ) {
			analyzer.addAnalyzer(fieldName, (Analyzer)fieldMap.get(fieldName));
		}
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return this.analyzer.tokenStream(fieldName, reader);
	}
	
}
