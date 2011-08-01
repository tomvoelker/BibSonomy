package org.bibsonomy.webapp.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;

/**
 * {@link AbstractApplicationContext#refresh()} sets the configured
 * {@link ConversionService} at the end of the method. Thus we can not use the
 * service for properties of {@link BeanFactoryPostProcessor}s,
 * {@link BeanPostProcessor}s and {@link MessageSource}s.
 * 
 * @author dzo
 * @version $Id$
 */
public class ConversionServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

	private int order = Ordered.HIGHEST_PRECEDENCE;
	private ConversionService conversionService;

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.setConversionService(this.conversionService);
	}
	
	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(final int order) {
		this.order = order;
	}

	/**
	 * @param conversionService the conversionService to set
	 */
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	
}
