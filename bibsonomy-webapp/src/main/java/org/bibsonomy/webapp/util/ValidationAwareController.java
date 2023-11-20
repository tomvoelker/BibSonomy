/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * extended MinimalisticControllers interface which can be optionally implemented
 * to tell about whether validation is required or not
 * 
 * @param <T> type of the command object
 * @author Jens Illig
 */
public interface ValidationAwareController<T extends ContextCommand> extends MinimalisticController<T> {
	
	/**
	 * @param command a command object initialized by the framework based on
	 *                the parameters of some request-event like a http-request
	 * @return decision whether validation for this request is required or not
	 */
	public boolean isValidationRequired(T command);
	
	/**
	 * @return the validator to use for validation
	 */
	public Validator<T> getValidator();
}
