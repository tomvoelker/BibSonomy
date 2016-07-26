/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.logic.util;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

/**
 * read only aware {@link LogicInterfaceFactory}
 * @author dzo
 */
public class ReadOnlyAwareLogicInterfaceFactory implements LogicInterfaceFactory {
	
	private final boolean readOnly;
	private final LogicInterfaceFactory logicInterfaceFactory;
	
	/**
	 * @param logicInterfaceFactory
	 * @param readOnly
	 */
	public ReadOnlyAwareLogicInterfaceFactory(LogicInterfaceFactory logicInterfaceFactory, boolean readOnly) {
		super();
		this.readOnly = readOnly;
		this.logicInterfaceFactory = logicInterfaceFactory;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterfaceFactory#getLogicAccess(java.lang.String, java.lang.String)
	 */
	@Override
	public LogicInterface getLogicAccess(String loginName, String apiKey) {
		final LogicInterface logic = this.logicInterfaceFactory.getLogicAccess(loginName, apiKey);
		return ReadOnlyLogic.maskLogic(logic, this.readOnly);
	}

}
