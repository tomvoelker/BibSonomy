package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Locale;

import net.sf.json.JSONObject;

import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.common.exceptions.SwordException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.context.MessageSource;

import de.unikassel.puma.openaccess.sword.SwordService;
import de.unikassel.puma.webapp.command.SwordServiceCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class SwordServiceController extends AjaxController implements MinimalisticController<SwordServiceCommand> {

	private SwordService swordService;
	private MessageSource messageSource;

	@Override
	public SwordServiceCommand instantiateCommand() {
		return new SwordServiceCommand();
	}

	@Override
	public View workOn(SwordServiceCommand command) {
		if(!command.getContext().isUserLoggedIn()) {
			//TODO access denied ex ?
			return Views.AJAX_TEXT;
		}
		
		String message = "sentSuccessful";
		int statuscode = 1; // statuscode=1: ok, =0: error 

		final User user = command.getContext().getLoginUser();
		
		Post<?> post = getPostToHash(command.getResourceHash(), user.getName());
		
		if(!present(post)) {
			
		}
		
		try {
			swordService.submitDocument(post, user);
		} catch (SwordException ex) {
			
			// send message of exception to webpage via ajax to give feedback of submission result
			message = ex.getMessage();
			
			if (message.equals("error.sword.errcode201")){
				// transmission complete and successful
				statuscode = 1;
			} else {
				// Error
				statuscode = 0;
			}
		}

		
		final JSONObject json = new JSONObject();
		final JSONObject jsonResponse = new JSONObject();

		final Locale locale = requestLogic.getLocale();
		
		jsonResponse.set("statuscode", statuscode);
		jsonResponse.set("message", message);
		// TODO: get from somewhere localized messages to transmit via ajax
		// localizedMessage = puma.repository.response.$message
		jsonResponse.set("localizedMessage", messageSource.getMessage(message, null, locale));
		json.put("response", jsonResponse);
		
		/*
		 * write the output, it will show the JSON-object as a plaintext string
		 */
		command.setResponseString(json.toString());
		
		return Views.AJAX_JSON;
	}
	
	private Post<?> getPostToHash(String intraHash, String userName) {
		Post<?> post = null;
		
		try {
			post = logic.getPostDetails(intraHash, userName);
		} catch (ResourceNotFoundException ex) {
			post = null;
		} catch (ResourceMovedException ex) {
			post = getPostToHash(ex.getNewIntraHash(), userName);
		}
		
		return post;
	}
	
	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param swordService
	 */
	public void setSwordService(SwordService swordService) {
		this.swordService = swordService;
	}

}
