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
package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * Handle a concept creation request
 * 
 * @author Stefan Stützer
 */
public class PostUserConceptStrategy extends AbstractCreateStrategy {
	private final String userName;

	/**
	 * @param context - the context
	 * @param userName - the owner of the new concept
	 */
	public PostUserConceptStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected String create() {
		final Tag concept = this.getRenderer().parseTag(this.doc);
		return this.getLogic().createConcept(concept, GroupingEntity.USER, this.userName);
	}

	@Override
	protected void render(final Writer writer, final String resourceHash) {
		this.getRenderer().serializeResourceHash(writer, resourceHash);
	}
}