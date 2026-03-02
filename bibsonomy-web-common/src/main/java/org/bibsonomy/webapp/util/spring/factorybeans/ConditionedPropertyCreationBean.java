/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.spring.factorybeans;

/**
 * @author jensi
 */
import org.bibsonomy.webapp.util.spring.condition.Condition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;

/**
 * @author jensi
 *
 * @param <T> type of the conditioned property
 */
public class ConditionedPropertyCreationBean<T> implements InitializingBean {

	private Condition condition;
	private ObjectFactory<T> successBeanFactory;
	private ObjectFactory<T> failureBeanFactory;
	private T obj;
	
	/**
	 * @return an object depending on the condition evaluation
	 */
	public T getConditionedProperty() {
		return obj;
	}
	
	protected T produceSucessBean() {
		if (this.successBeanFactory == null) {
			throw new IllegalStateException("successBeanFactory must be configured");
		}
		return this.successBeanFactory.getObject();
	}

	protected T produceFailureBean() {
		if (this.failureBeanFactory == null) {
			return null;
		}
		return this.failureBeanFactory.getObject();
	}

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

	/**
	 * @param successBeanFactory factory for creating the success bean
	 */
	public void setSuccessBeanFactory(final ObjectFactory<T> successBeanFactory) {
		this.successBeanFactory = successBeanFactory;
	}

	/**
	 * @param failureBeanFactory factory for creating the failure bean
	 */
	public void setFailureBeanFactory(final ObjectFactory<T> failureBeanFactory) {
		this.failureBeanFactory = failureBeanFactory;
	}
}
