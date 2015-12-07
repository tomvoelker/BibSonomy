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

import java.io.Writer;

import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * Handle a concept update request
 * 
 * @author Stefan Stützer
 */
public class PutUserConceptStrategy extends AbstractUpdateStrategy {

	private final String userName;
	
	/**
	 * @param context - the context
	 * @param userName - the owner of the concept
	 */
	public PutUserConceptStrategy(Context context, String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected String update() {
		final Tag concept = this.getRenderer().parseTag(this.doc);
		return this.getLogic().updateConcept(concept, GroupingEntity.USER, this.userName, ConceptUpdateOperation.UPDATE);		
	}

	@Override
	protected void render(Writer writer, String resourceID) {
		this.getRenderer().serializeResourceHash(writer, resourceID);
	}
}