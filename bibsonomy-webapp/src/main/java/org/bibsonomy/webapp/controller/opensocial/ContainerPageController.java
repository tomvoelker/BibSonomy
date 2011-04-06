package org.bibsonomy.webapp.controller.opensocial;

import org.apache.shindig.config.ContainerConfig;
import org.bibsonomy.model.User;
import org.bibsonomy.opensocial.security.SecurityTokenUtil;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.opensocial.OpenSocialCommand;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import com.google.inject.Inject;

/**
 * Initial gadget container page for testing the open social interface
 * @author fei
 */
public class ContainerPageController extends SingleResourceListControllerWithTags implements MinimalisticController<OpenSocialCommand> {
	@Inject
	ContainerConfig config;
	
	@SuppressWarnings("deprecation")
	@Override
	public View workOn(OpenSocialCommand command) {
		User loginUser = AuthenticationUtils.getUser();
		//String st;
		try {
			String token = SecurityTokenUtil.getSecurityToken(loginUser, command.getGadgetUrl());
			command.setSecurityToken(token);
		} catch (Exception ex) {
		}
		return Views.GADGETCONTAINER;
	}
	
	@Override
	public OpenSocialCommand instantiateCommand() {
		return new OpenSocialCommand();
	}

}
