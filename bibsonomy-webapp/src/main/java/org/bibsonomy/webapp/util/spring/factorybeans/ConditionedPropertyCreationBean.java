package org.bibsonomy.webapp.util.spring.factorybeans;

/**
 * @author jensi
 */
import org.bibsonomy.webapp.util.spring.condition.Condition;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author jensi
 *
 * @param <T> type of the conditioned property
 */
public abstract class ConditionedPropertyCreationBean<T> implements InitializingBean {

	private Condition condition;
	private T obj;
	
	/**
	 * @return an object depending on the condition evaluation
	 */
	public T getConditionedProperty() {
		return obj;
	}
	
	protected abstract T produceSucessBean();
	protected abstract T produceFailureBean();

	@Override
	public void afterPropertiesSet() throws Exception {
		obj = (condition.eval()) ? produceSucessBean() :  produceFailureBean();
	}

	/**
	 * @return the condition
	 */
	public Condition getCondition() {
		return this.condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
}