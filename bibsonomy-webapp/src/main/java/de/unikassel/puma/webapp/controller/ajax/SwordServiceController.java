package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.sword.SwordService;
import de.unikassel.puma.webapp.command.SwordServiceCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class SwordServiceController extends AjaxController implements MinimalisticController<SwordServiceCommand> {

	private SwordService swordService;
	
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
		
		final User user = command.getContext().getLoginUser();
		
		Post<?> post = getPostToHash(command.getResourceHash(), user.getName());
		
		if(!present(post)) {
			
		}
		
		swordService.checkDepositResponse(swordService.submitDocument(post, user));
		
		//FIXME is this the right way to return nothing ? 
		return Views.AJAX_TEXT;
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
	 * @param swordService
	 */
	public void setSwordService(SwordService swordService) {
		this.swordService = swordService;
	}

}
