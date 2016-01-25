/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 13.07.2007
 */
package org.bibsonomy.rest.remotecall;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

/**
 * this class is used to test if the system delegates the right login-data
 * to the (this) {@link LogicInterfaceFactory} and for injecting a
 * {@link LogicInterface}-mock-implementation into the system. 
 * 
 * @author Jens Illig
 */
public class MockLogicFactory implements LogicInterfaceFactory {
	private static String requestedLoginName = null;
	private static String requestedApiKey = null;
	private static LogicInterface logic;
	
	protected static void init(LogicInterface li) {
		requestedLoginName = null;
		requestedApiKey = null;
		logic = li;
	}
	
	@Override
	public LogicInterface getLogicAccess(String loginName, String apiKey) {
		requestedLoginName = loginName;
		requestedApiKey = apiKey;
		return logic;
	}

	protected static String getRequestedApiKey() {
		return requestedApiKey;
	}

	protected static String getRequestedLoginName() {
		return requestedLoginName;
	}

}
