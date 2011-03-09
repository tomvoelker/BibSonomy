package org.bibsonomy.webapp.controller.opensocial;

import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.opensocial.OpenSocialCommand;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Initial gadget container page for testing the open social interface
 * @author fei
 */
public class ContainerPageController extends SingleResourceListControllerWithTags implements MinimalisticController<OpenSocialCommand> {
	
	@SuppressWarnings("deprecation")
	@Override
	public View workOn(OpenSocialCommand command) {
		User loginUser = AuthenticationUtils.getUser();
		/*
		try {
			String token = SecurityTokenUtil.getSecurityToken(loginUser, "http://www.google.com/ig/modules/horoscope.xml");
			command.setSecurityToken(token);
		} catch (Exception ex) {
		}
		*/
		return Views.GADGETCONTAINER;
	}
	
	@Override
	public OpenSocialCommand instantiateCommand() {
		return new OpenSocialCommand();
	}

}
