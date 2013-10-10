package org.bibsonomy.services.information;

import static org.bibsonomy.util.ValidationUtils.present;

import org.antlr.stringtemplate.StringTemplate;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;

/**
 * @author dzo
 * @version $Id$
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
		Group groupDetails = this.logic.getGroupDetails(userToInform.getName());
		if (!present(groupDetails)) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " can only be used in system tags interacting with groups");
		}
		return groupDetails.getPublicationReportingSettings().getReportingMailAddress();
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
