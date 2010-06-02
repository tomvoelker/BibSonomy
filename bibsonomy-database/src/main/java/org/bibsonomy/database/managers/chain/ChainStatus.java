package org.bibsonomy.database.managers.chain;

import org.bibsonomy.database.params.GenericParam;

/**
 * Only interesting for testcases. Usage pattern:
 * <ul>
 * <li>add a new instance of this class as a parameter to the perform method of
 * a chain element</li>
 * <li>once the perform method returns, check {@link #getChainElement()} for
 * the element from the chain that handled the request</li>
 * </ul>
 * 
 * If we don't like this class someday <em>the</em> chain could return the
 * appropriate element instead of the actual result, i.e. not a List of
 * something (e.g. Post<? extends Resource>) but an instance that extends
 * {@link ChainElement}. This way the caller would know the callee (by checking
 * it's instance) and could call the handle method himself. The latter wouldn't
 * be too bad either and this class and the extra method
 * {@link ChainElement#perform(GenericParam, org.bibsonomy.database.common.DBSession, ChainStatus)}
 * would be obsolete.<br/>
 * 
 * Another way would be an aspect (e.g. with AspectJ) with a pointcut for every
 * call to
 * {@link ChainElement#perform(GenericParam, org.bibsonomy.database.common.DBSession)}
 * that memorizes the class which executes its
 * {@link ChainElement#handle(GenericParam, org.bibsonomy.database.common.DBSession)}
 * method. After that one could <em>ask</em> the aspect for the result. This
 * would be very clean because we wouldn't have to change the <em>real</em>
 * code: the aspect for the tests would do it.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class ChainStatus {

	private ChainElement<?, ? extends GenericParam> chainElement;

	/**
	 * Constructor
	 */
	public ChainStatus() {
		this.chainElement = null;
	}

	/**
	 * Returns the chain element that <em>handled</em> the request
	 * 
	 * @return chain element
	 */
	public ChainElement<?, ? extends GenericParam> getChainElement() {
		return this.chainElement;
	}

	/**
	 * @param chainElement
	 */
	public void setChainElement(ChainElement<?, ? extends GenericParam> chainElement) {
		this.chainElement = chainElement;
	}
}