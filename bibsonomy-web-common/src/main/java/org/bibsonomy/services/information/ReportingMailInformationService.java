/**
 * BibSonomy-Web-Common - Common things for web
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
package org.bibsonomy.services.information;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Locale;

import org.antlr.stringtemplate.StringTemplate;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * @author dzo
 */
public class ReportingMailInformationService extends MailInformationService {

	private String projectHome;
	
	@Override
	protected void setAttributes(StringTemplate stringTemplate, User userToInform, Post<? extends Resource> post) {
		super.setAttributes(stringTemplate, userToInform, post);
		// TODO: used to generate a link use URLGenerator instead
		stringTemplate.setAttribute("projectHome", this.projectHome);
	}
	
	@Override
	protected String getMailAddress(User userToInform) {
		final Group groupDetails = getGroup(userToInform.getName());
		return groupDetails.getPublicationReportingSettings().getReportingMailAddress();
	}

	private Group getGroup(String username) {
		Group groupDetails = this.logic.getGroupDetails(username);
		if (!present(groupDetails)) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " can only be used in system tags interacting with groups");
		}
		return groupDetails;
	}
	
	@Override
	protected String getTemplate(String username, Locale locale) {
		final Group group = getGroup(username);
		return group.getPublicationReportingSettings().getReportingMailTemplate();
	}
	
	@Override
	protected boolean userWantsToBeInformed(User userToInform) {
		return present(this.getMailAddress(userToInform));
	}

	/**
	 * @param projectHome the projectHome to set
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}
}
