/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.util;

import org.bibsonomy.webapp.command.ContextCommand;


/**
 * A minialistic controller that knows nothing about being invoked
 * by a servlet request or a testcase or whatever. It only communicates
 * via the command object (argument of {@link #workOn(ContextCommand)} and result
 * of {@link #instantiateCommand()}).
 * 
 * @param <T> type of the command object
 * 
 * @author Jens Illig
 */
public interface MinimalisticController<T extends ContextCommand> {
	/**
	 * @return a command object to be filled by the framework
	 */
	public T instantiateCommand();
	
	/**
	 * @param command a command object initialized by the framework based on
	 *                the parameters of som request-event like a http-request
	 * @return some symbol that describes the next state of the
	 *         application (the view)
	 */
	public View workOn(T command);
}
