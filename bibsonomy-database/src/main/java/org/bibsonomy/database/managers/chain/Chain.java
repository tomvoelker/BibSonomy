/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
