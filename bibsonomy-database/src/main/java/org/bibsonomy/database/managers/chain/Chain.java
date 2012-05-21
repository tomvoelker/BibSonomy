package org.bibsonomy.database.managers.chain;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.common.DBSession;

/**
 * chain implementation
 * 
 * @author dzo
 * @version $Id$
 * @param <L> 
 * @param <P> 
 */
public class Chain<L, P> implements ChainPerform<P, L> {
	private static final Log log = LogFactory.getLog(Chain.class);
	
	private List<ChainElement<L, P>> elements;

	@Override
	public L perform(final P param, final DBSession session) {
		final ChainElement<L, P> chainElement = this.getChainElement(param);
		if (present(chainElement)) {
			log.debug("Handling Chain element: " + chainElement.getClass().getSimpleName());
			return chainElement.handle(param, session);
		}
		
		throw new RuntimeException("Can't handle request for param object: " + param.toString());
	}
	
	/**
	 * XXX: only public for the tests
	 * @param param
	 * @return the {@link ChainElement} that can handle the param
	 */
	public ChainElement<L, P> getChainElement(final P param) {
		for (final ChainElement<L, P> element : this.elements) {
			if (element.canHandle(param)) {
				return element;
			}
		}
		
		return null;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(final List<ChainElement<L, P>> elements) {
		this.elements = elements;
	}
}
