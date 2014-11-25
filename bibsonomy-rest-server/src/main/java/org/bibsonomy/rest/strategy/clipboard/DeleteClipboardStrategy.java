/**
 * BibSonomy-Rest-Server - The REST-server.
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
package org.bibsonomy.rest.strategy.clipboard;

import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author wla
 */
public class DeleteClipboardStrategy extends PostClipboardStrategy {

	private final boolean clearClipboard;

	/**
	 * 
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param clearClipboard 
	 */
	public DeleteClipboardStrategy(Context context, String userName, String resourceHash, boolean clearClipboard) {
		super(context, userName, resourceHash);
		this.clearClipboard = clearClipboard;
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ObjectNotFoundException {
		this.getLogic().deleteBasketItems(createPost(resourceHash, userName), clearClipboard);
		this.getRenderer().serializeOK(this.writer);
	}

}
