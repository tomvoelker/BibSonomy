/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.chain.concept.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.concept.ConceptChainElement;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.model.Tag;

/**
 * @author Stefan Stützer
 */
public class GetPickedConceptsForUser extends ConceptChainElement {

	@Override
	protected List<Tag> handle(final TagRelationParam param, final DBSession session) {		
		return this.db.getPickedConceptsForUser(param.getRequestedUserName(), session);
	}

	@Override
	protected boolean canHandle(final TagRelationParam param) {
		return (param.getGrouping() == GroupingEntity.USER &&
				param.getConceptStatus() == ConceptStatus.PICKED &&
				present(param.getRequestedUserName()));
	}
}