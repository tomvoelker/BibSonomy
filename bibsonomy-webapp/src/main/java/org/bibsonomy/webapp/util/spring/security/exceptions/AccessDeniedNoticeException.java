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
package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.security.access.AccessDeniedException;

/**
 * @author dzo
 */
public class AccessDeniedNoticeException extends AccessDeniedException {
	private static final long serialVersionUID = 8538409864663313358L;
	
	private final String notice;
	
	/**
	 * @param msg the message to set
	 * @param notice the notice to set
	 */
	public AccessDeniedNoticeException(final String msg, final String notice) {
		super(msg);
		this.notice = notice;
	}

	/**
	 * @return the notice
	 */
	public String getNotice() {
		return notice;
	}
}
