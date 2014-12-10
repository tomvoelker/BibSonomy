/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.delete;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * @author wla
 */
public class UnpickClipboardQuery extends AbstractQuery<Integer> {

	private boolean clearAll;
	private String resourceHash;
	private String userName;

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String url;
		if (clearAll) {
			url = this.getUrlRenderer().createHrefForClipboard(this.userName, Boolean.valueOf(clearAll));
		} else {
			url = this.getUrlRenderer().createHrefForClipboadEntry(this.userName, this.resourceHash);
		}
		this.downloadedDocument = performRequest(HttpMethod.DELETE, url, null);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.AbstractQuery#getResultInternal()
	 */
	@Override
	protected Integer getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
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
