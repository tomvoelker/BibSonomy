package org.bibsonomy.database.managers.chain;

/**
 * {@link FirstChainElement} implementation for spring bean config
 * 
 * @author dzo
 * @version $Id$
 * @param <L> 
 * @param <P> 
 */
public class Chain<L, P> implements FirstChainElement<L, P> {

	private ChainElement<L, P> firstElement;
	
	@Override
	public ChainElement<L, P> getFirstElement() {
		return this.firstElement;
	}

	/**
	 * @param firstElement the firstElement to set
	 */
	public void setFirstElement(final ChainElement<L, P> firstElement) {
		this.firstElement = firstElement;
	}

}
