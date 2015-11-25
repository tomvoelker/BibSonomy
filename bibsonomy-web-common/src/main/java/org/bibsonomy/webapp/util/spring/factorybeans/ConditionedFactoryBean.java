/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

	protected T produceFailureBean() {
		return null;
	}
}
