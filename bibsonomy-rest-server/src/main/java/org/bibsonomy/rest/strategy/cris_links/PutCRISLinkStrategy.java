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

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;
import java.util.stream.Collectors;

/**
 * @author pda
 */
public class PutCRISLinkStrategy extends AbstractUpdateStrategy {
	/**
	 * @param context
	 */
	public PutCRISLinkStrategy(Context context) {
		super(context);
	}

	@Override
	protected void render(Writer writer, String linkId) {
		this.getRenderer().serializeCRISLinkId(writer, linkId);
	}

	@Override
	protected String update() {
		final CRISLink crisLink = this.getRenderer().parseCRISLink(this.doc);
		final JobResult jobResult = this.getLogic().updateCRISLink(crisLink);
		if (Status.FAIL.equals(jobResult.getStatus())) {
			throw new BadRequestOrResponseException(jobResult.getErrors().stream().
							map(ErrorMessage::getDefaultMessage).collect(Collectors.joining(",")));
		}
		return crisLink.getSource().getLinkableId() + "-" + crisLink.getTarget().getLinkableId();
	}
}
