package org.bibsonomy.webapp.controller.opensocial;

import org.apache.shindig.config.ContainerConfig;
import org.bibsonomy.model.User;
import org.bibsonomy.opensocial.security.SecurityTokenUtil;
import org.bibsonomy.webapp.command.opensocial.OpenSocialCommand;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import com.google.inject.Inject;

/**
 * Initial gadget container page for testing the open social interface
 * 
 * @author fei
 * @version $Id$
 */
public class ContainerPageController extends SingleResourceListControllerWithTags implements MinimalisticController<OpenSocialCommand> {
	
	@Inject // FIXME: inject via spring!
	ContainerConfig config;
	
	@Override
	public View workOn(final OpenSocialCommand command) {
		final User loginUser = command.getContext().getLoginUser();
		// TODO: handle user not logged in?
		try {
			final String token = SecurityTokenUtil.getSecurityToken(loginUser, command.getGadgetUrl());
			command.setSecurityToken(token);
		} catch (final Exception ex) {
		}
		return Views.GADGETCONTAINER;
	}
	
	@Override
	public OpenSocialCommand instantiateCommand() {
		return new OpenSocialCommand();
	}
}
