/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.users;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author  Christian Kramer
 */
public class DeleteDocumentStrategy extends AbstractDeleteStrategy {
	
	private final Document document;
	private final String resourceHash;
	
	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public DeleteDocumentStrategy(final Context context, final String userName, final String resourceHash, final String fileName) {
		super(context);
		this.document = new Document();
		this.document.setUserName(userName);
		this.document.setFileName(fileName);
		this.resourceHash = resourceHash;
	}

	@Override
	protected boolean delete() throws InternServerException {
		this.getLogic().deleteDocument(this.document, this.resourceHash);
		// no exception -> assume success
		
		// FIXME: delete document from the disk!?
		
		return true;
	}
}