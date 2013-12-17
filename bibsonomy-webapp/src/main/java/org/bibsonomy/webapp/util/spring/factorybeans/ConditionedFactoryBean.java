package org.bibsonomy.webapp.util.spring.factorybeans;

import org.bibsonomy.webapp.util.spring.condition.Condition;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} that checks whether a {@link Condition}. If so, it
 * returns the bean produced by {@link #produceSucessBean()} and the one produced by {@link #produceFailureBean()} otherwise.
 * The abstract methods are injected by spring. 
 * 
 * @author jensi
  * @param <T>  type of the object to be instantiated
 */
public abstract class ConditionedFactoryBean<T> extends ConditionedPropertyCreationBean<T> implements FactoryBean<T> {
	@Override
	public Class<?> getObjectType() {
		return null;
	}
	
	@Override
	public T getObject() throws Exception {
		return getConditionedProperty();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
