package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.webapp.command.actions.GroupJoinUserCommand;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.util.captcha.CaptchaResponse;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author schwass
 * @version $Id$
 */
public class GroupJoinUserController extends GroupActionsController<GroupJoinUserCommand> implements RequestAware {
	
	private Captcha captcha;
	private RequestLogic requestLogic;

	/** Give this controller an instance of {@link Captcha}.
	 * 
	 * @param captcha
	 */
	@Required
	public void setCaptcha(Captcha captcha) {
		this.captcha = captcha;
	}

	/** The logic needed to access the request
	 * @param requestLogic 
	 */
	@Required
	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public GroupJoinUserCommand instantiateCommand() {
		return new GroupJoinUserCommand();
	}

	@Override
	View workOnSpecial(GroupJoinUserCommand command) {
		checkCaptcha(command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), requestLogic.getHostInetAddress());

		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			/*
			 * Generate HTML to show captcha.
			 */
			command.setCaptchaHTML(captcha.createCaptchaHtml(requestLogic.getLocale()));
			return Views.GROUP_JOIN_USER;
		}
		
		return Views.GROUPSPAGE;
	}

	/**
	 * Checks the captcha. If the response from the user does not match the captcha,
	 * an error is added. 
	 * 
	 * FIXME: duplictaed in {@link EditPostController} 
	 * 
	 * @param command - the command associated with this request.
	 * @param hostInetAddress - the address of the client
	 * @throws InternServerException - if checking the captcha was not possible due to 
	 * an exception. This could be caused by a non-rechable captcha-server. 
	 */
	private void checkCaptcha(final String challenge, final String response, final String hostInetAddress) throws InternServerException {
		if (org.bibsonomy.util.ValidationUtils.present(challenge) && org.bibsonomy.util.ValidationUtils.present(response)) {
			/*
			 * check captcha response
			 */
			try {
				final CaptchaResponse res = captcha.checkAnswer(challenge, response, hostInetAddress);

				if (!res.isValid()) {
					/*
					 * invalid response from user
					 */
					errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
				} else if (res.getErrorMessage() != null) {
					/*
					 * valid response, but still an error
					 */
					log.warn("Could not validate captcha response: " + res.getErrorMessage());
				}
			} catch (final Exception e) {
				log.fatal("Could not validate captcha response.", e);
				throw new InternServerException("error.captcha");
			}
		}
	}

}
