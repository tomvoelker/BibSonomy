package org.bibsonomy.util.filter.posts;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.bibsonomy.util.filter.posts.parser.FilterRuleLexer;
import org.bibsonomy.util.filter.posts.parser.FilterRuleParser;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class PostFilterFactory {

	/**
	 * Creates a new {@link PostFilter} from a definition adhering to the 
	 * post filter grammar. 
	 * 
	 * @param filterDefinition
	 * @return
	 * @throws RecognitionException
	 */
	public PostFilter getPostFilterFromStringDefinition(final String filterDefinition) throws RecognitionException {
		final CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(new FilterRuleLexer(new ANTLRStringStream(filterDefinition)));
		final FilterRuleParser parser = new FilterRuleParser(tokens);
		/*
		 * parse filter definition
		 */
		parser.filter();
		/*
		 * return result
		 */
		return parser.getPostFilter();
	}


	/**
	 * Creates a new {@link PostFilter} by using a Spring bean definition XML file.
	 * TODO: The name of the bean must be 'postFilter'. 
	 * 
	 * @param xmlBeanDefinition
	 * @return
	 */
	public PostFilter getPostFilterFromBeanDefinitionInClasspath(final String xmlBeanDefinition) {
		final BeanFactory factory = new XmlBeanFactory(new ClassPathResource(xmlBeanDefinition));

		final Object bean = factory.getBean("postFilter", PostFilter.class);
		if (bean instanceof PostFilter) {
			return (PostFilter) bean;
		}
		return null;
	}

}

