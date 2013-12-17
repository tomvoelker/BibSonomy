/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author wla
  */
public class UnpickClipboardQuery extends AbstractQuery<Integer> {

	private boolean clearAll;
	private String resourceHash;
	private String userName;

	@Override
	protected Integer doExecute() throws ErrorPerformingRequestException {
		final StringBuilder urlBuilder = new StringBuilder(RESTConfig.USERS_URL + "/" + userName + "/" + RESTConfig.CLIPBOARD_SUBSTRING);
		if (clearAll) {
			urlBuilder.append("?clear=true");
		} else {
			urlBuilder.append("/" + resourceHash);
		}
		performRequest(HttpMethod.DELETE, urlBuilder.toString(), null);
		return 0;
	}

	/**
	 * @param clearAll
	 *            the clearAll to set
	 */
	public void setClearAll(final boolean clearAll) {
		this.clearAll = clearAll;
	}

	/**
	 * @param resourceHash
	 *            the resourceHash to set
	 */
	public void setResourceHash(final String resourceHash) {
		this.resourceHash = resourceHash;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

}
