package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import net.sf.json.JSONObject;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Repository;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.sherparomeo.SherpaRomeoImpl;
import de.unikassel.puma.webapp.command.OpenAccessCommand;

/**
 * @author clemens
 * @version $Id$
 */
public class OpenAccessController extends AjaxController implements MinimalisticController<OpenAccessCommand> {

	private static final String GET_SENT_REPOSITORIES = "GET_SENT_REPOSITORIES";

	SherpaRomeoImpl sherpaLogic;
	
	@Override
	public OpenAccessCommand instantiateCommand() {
		return new OpenAccessCommand();
	}

	@Override
	public View workOn(OpenAccessCommand command) {
		
		// check if user is logged in
		if(!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		final String action = command.getAction();
		if (present(action)) {
		
			if (GET_SENT_REPOSITORIES.equals(action)) {
		
				final List<Post<BibTex>> posts = logic.getPosts(BibTex.class, GroupingEntity.USER, command.getContext().getLoginUser().getName(), null, command.getInterhash(), null, FilterEntity.POSTS_WITH_REPOSITORY, 0, Integer.MAX_VALUE, null);
				
/*
 * 				Post<BibTex> b = posts.get(0);
				assertEquals(b.getRepositorys().size() , 2);
 */

				// TODO: implement this
				/*
				 * Schleife über alle Posts
				 * nimm Repository-Speicher-Datum und User
				 * schreibe JSON-Output mit Datum und Flag ob selbst versendet oder durch wen anderes
				 * 
				 * Titel, Link mit intrahash, Datum
				 */
				for (final Post<BibTex> p : posts) {
					List<Repository> repositories = p.getRepositorys();
					final JSONObject jsonObject = new JSONObject();
					for (final Repository r : repositories) {
//						jsonObject.;
					}
//					json.put("available", jsonArray);
				}
				
			} else {
				this.sherpaLogic = new SherpaRomeoImpl();
		
				if(command.getPublisher() != null) {
					command.setResponseString(sherpaLogic.getPolicyForPublisher(command.getPublisher(), command.getqType()));
				}
				if (command.getjTitle() != null) {
					command.setResponseString(sherpaLogic.getPolicyForJournal(command.getjTitle(), command.getqType()));			
				}
		
				return Views.AJAX_JSON;
			}
		}
		return Views.AJAX_JSON;
	}

}
