/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for a given key.
 * 
 * @author Florian Bachmann
 */
public class GetBibtexByKey extends ResourceChainElement<BibTex, BibTexParam> {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return ((BibTexDatabaseManager) this.databaseManager).getPostsByBibTeXKey(param.getUserName(), param.getBibtexKey(), param.getRequestedUserName(), param.getGroupId(), param.getGroups(), param.getLimit(), param.getOffset(), param.getSystemTags(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getBibtexKey()) && (param.getNumSimpleConcepts() == 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED, Order.FOLKRANK));
	}
}