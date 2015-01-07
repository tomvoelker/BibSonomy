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
package org.bibsonomy.rest.strategy.posts.community.references;

import java.io.Reader;
import java.util.Set;

import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author dzo
 */
public class DeleteRelationsStrategy extends AbstractDeleteStrategy {

	private final String hash;
	private final Reader doc;
	private final GoldStandardRelation relation;

	/**
	 * @param context
	 * @param hash
	 * @param relation
	 */
	public DeleteRelationsStrategy(final Context context, final String hash, final GoldStandardRelation relation) {
		super(context);

		this.hash = hash;
		this.doc = context.getDocument();
		this.relation =  relation;
	}

	@Override
	protected boolean delete() {
		final Set<String> references = this.getRenderer().parseReferences(this.doc);
		this.getLogic().deleteRelations(this.hash, references, relation);

		// no exception => delete successful
		return true;
	}

}
