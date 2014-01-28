package org.bibsonomy.webapp.util.spring.condition;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author jensi
 */
public class EvalOnceCondition implements Condition, InitializingBean {
	private Condition delegate;
	private boolean value;
	
	@Override
	public boolean eval() {
		return value;
	}

	/**
	 * @return the delegate
	 */
	public Condition getDelegate() {
		return this.delegate;
	}

	/**
	 * @param delegate the delegate to set
	 */
	public void setDelegate(Condition delegate) {
		this.delegate = delegate;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		value = delegate.eval();
	}

}
