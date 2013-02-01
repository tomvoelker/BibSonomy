package org.bibsonomy.webapp.util.spring.condition;

import org.bibsonomy.webapp.util.spring.factorybeans.ConditionedPropertyCreationBean;


/**
 * Some kind of abstract condition. It has been created to allow arbitrary spring-defined conditions for {@link ConditionedPropertyCreationBean}
 * 
 * @author jensi
 * @version $Id$
 */
public interface Condition {
	/**
	 * @return whether the condition is true or not
	 */
	public boolean eval();
}
