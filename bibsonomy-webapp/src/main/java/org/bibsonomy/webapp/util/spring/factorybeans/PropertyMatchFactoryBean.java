package org.bibsonomy.webapp.util.spring.factorybeans;

import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} that checks whether a checkProperty String contains an expected value in a comma-separated list. If so, it returns {@link #successInstance}, otherwise {@link #failInstance}.
 * @author jensi
 * @version $Id$
 * @param <T>  type of the object to be instantiated
 */
public class PropertyMatchFactoryBean<T> implements FactoryBean<T> {
	private String checkProperty;
	private String expected;
	private T successInstance;
	private T failInstance;
	private boolean singleton = true;
	
	@Override
	public T getObject() throws Exception {
		if (checkCheckProperty() == true) {
			return successInstance;
		}
		return failInstance;
	}

	protected boolean checkCheckProperty() {
		for (String s : checkProperty.split(",")) {
			if (s.trim().equals(expected) == true) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	/**
	 * @return the checkProperty
	 */
	public String getCheckProperty() {
		return this.checkProperty;
	}

	/**
	 * @param checkProperty the checkProperty to set
	 */
	public void setCheckProperty(String checkProperty) {
		this.checkProperty = checkProperty;
	}

	/**
	 * @param singleton the singleton to set
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	/**
	 * @return the expected
	 */
	public String getExpected() {
		return this.expected;
	}

	/**
	 * @param expected the expected to set
	 */
	public void setExpected(String expected) {
		this.expected = expected;
	}

	/**
	 * @return the successInstance
	 */
	public T getSuccessInstance() {
		return this.successInstance;
	}

	/**
	 * @param successInstance the successInstance to set
	 */
	public void setSuccessInstance(T successInstance) {
		this.successInstance = successInstance;
	}

	/**
	 * @return the failInstance
	 */
	public T getFailInstance() {
		return this.failInstance;
	}

	/**
	 * @param failInstance the failInstance to set
	 */
	public void setFailInstance(T failInstance) {
		this.failInstance = failInstance;
	}

}
