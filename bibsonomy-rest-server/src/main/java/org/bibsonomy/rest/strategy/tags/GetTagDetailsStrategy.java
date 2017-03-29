/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.strategy.tags;


import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetTagDetailsStrategy extends Strategy {
	private final Tag tag;
	private final String tagName;

	/**
	 * @param context
	 * @param tag
	 */
	public GetTagDetailsStrategy(final Context context, final String tag) {
		super(context);
		this.tagName = tag;
		this.tag = context.getLogic().getTagDetails(tagName);
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		if (this.tag == null) {
			throw new NoSuchResourceException("The requested tag '" + this.tagName + "' does not exist.");
		}
		// delegate to the renderer
		this.getRenderer().serializeTag(writer, this.tag, new ViewModel());
	}

	@Override
	public String getContentType() {
		return "tag";
	}
}