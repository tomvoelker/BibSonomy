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
package org.bibsonomy.rest.strategy;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;

/**
 * @author Dominik Benz
 */
public abstract class AbstractUpdateStrategy extends Strategy {
	protected final Reader doc;
	
	/**
	 * @param context
	 */
	public AbstractUpdateStrategy(final Context context) {
		super(context);
		this.doc = context.getDocument();
	}

	@Override
	public final void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		final String resourceID = update();		
		render(writer, resourceID);
	}

	/**
	 * @param writer
	 * @param resourceID
	 */
	protected abstract void render(Writer writer, String resourceID);

	/**
	 * @return the resourceID of the updated resource
	 */
	protected abstract String update();
}