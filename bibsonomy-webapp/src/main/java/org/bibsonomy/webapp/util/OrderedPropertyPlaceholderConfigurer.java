package org.bibsonomy.webapp.util;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * {@link AbstractApplicationContext#invokeBeanFactoryPostProcessors} method
 * doesn't care about the order of {@link PriorityOrdered} {@link BeanFactoryPostProcessor}s
 * while initializing them (also ignores the depends-on config). Thus the
 * §{configLocation} place holder isn't replaced because the responsible
 * {@link PropertyPlaceholderConfigurer} wasn't called afore. A workaround for
 * this problem is this class. Due to the fact that this class is only an
 * {@link Ordered} {@link BeanFactoryPostProcessor} it is initialized after
 * all {@link PriorityOrdered} post processors. {@link #postProcessBeanFactory(ConfigurableListableBeanFactory)}
 * creates the {@link PropertyPlaceholderConfigurer} with the provided properties
 * which prepares all ${ place holders
 * 
 * source: http://forum.springsource.org/showthread.php?89623-PropertyPlaceholderConfigurer-problems
 *  
 * @author dzo
 * @author Rob Winch
 * 
 * @version $Id$
 */
public class OrderedPropertyPlaceholderConfigurer implements Ordered, BeanFactoryPostProcessor {

	private int order;
	private Properties properties;	
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(final Properties properties) {
		this.properties = properties;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(final int order) {
		this.order = order;
	}
	
	@Override
	public int getOrder() {
		return this.order;
	}
	
	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		/*
		 * add the property placeholder configurer
		 */
		final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		configurer.setProperties(this.properties);
		configurer.postProcessBeanFactory(beanFactory);
	}

}
