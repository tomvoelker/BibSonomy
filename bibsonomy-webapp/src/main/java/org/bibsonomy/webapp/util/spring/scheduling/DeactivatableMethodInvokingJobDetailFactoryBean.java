package org.bibsonomy.webapp.util.spring.scheduling;

import java.lang.reflect.InvocationTargetException;

import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/**
 * a deactivatable {@link MethodInvokingJobDetailFactoryBean}
 * 
 * @author dzo
 */
public class DeactivatableMethodInvokingJobDetailFactoryBean extends MethodInvokingJobDetailFactoryBean {

	private boolean enabled;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.util.MethodInvoker#invoke()
	 */
	@Override
	public Object invoke() throws InvocationTargetException, IllegalAccessException {
		if (!this.enabled) {
			return null;
		}

		return super.invoke();
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
