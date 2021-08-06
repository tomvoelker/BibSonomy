/**
 * BibSonomy-Rest-Server - The REST-server.
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
import org.bibsonomy.common.exceptions.ObjectMovedException;
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
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ObjectMovedException, ObjectNotFoundException {
		this.getLogic().deleteClipboardItems(createPost(resourceHash, userName), clearClipboard);
		this.getRenderer().serializeOK(this.writer);
	}

}
