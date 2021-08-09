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
package org.bibsonomy.rest.strategy.cris_links;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.ValidationUtils;

/**
 * TODO: allow deletion of arbitrary cris links
 * @author pda
 */
public class DeleteCRISLinkStrategy extends AbstractDeleteStrategy {
	private final String sourceId;
	private final String targetId;

	/**
	 * @param context
	 */
	public DeleteCRISLinkStrategy(Context context) {
		super(context);
		this.sourceId = context.getStringAttribute("sourceId", null);
		this.targetId = context.getStringAttribute("targetId", null);
	}

	@Override
	protected boolean delete() {
		final Person target = getLogic().getPersonById(PersonIdType.PERSON_ID, targetId);
		if (!ValidationUtils.present(target)) {
			return false;
		}
		final Project source = getLogic().getProjectDetails(sourceId);
		if (!ValidationUtils.present(source)) {
			return false;
		}
		return getLogic().deleteCRISLink(source, target).getStatus() == Status.OK;
	}
}
