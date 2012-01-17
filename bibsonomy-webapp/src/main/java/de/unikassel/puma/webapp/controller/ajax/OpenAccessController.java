package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.sherparomeo.SherpaRomeoImpl;
import de.unikassel.puma.openaccess.sherparomeo.SherpaRomeoInterface;
import de.unikassel.puma.webapp.command.ajax.OpenAccessCommand;

/**
 * @author clemens
 * @version $Id$
 */
public class OpenAccessController extends AjaxController implements MinimalisticController<OpenAccessCommand> {

	private static final String GET_SENT_REPOSITORIES = "GET_SENT_REPOSITORIES";
	private static final String SHERPAROMEO = "SHERPAROMEO";

	private SherpaRomeoInterface sherpaLogic;
	
	@Override
	public OpenAccessCommand instantiateCommand() {
		return new OpenAccessCommand();
	}

	@Override
	public View workOn(final OpenAccessCommand command) {
		final JSONObject json = new JSONObject();

		// check if user is logged in
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		final String action = command.getAction();
		if (present(action)) {
			if (GET_SENT_REPOSITORIES.equals(action)) {
				final List<Post<BibTex>> posts = logic.getPosts(BibTex.class, GroupingEntity.USER, command.getContext().getLoginUser().getName(), null, command.getInterhash(), null, FilterEntity.POSTS_WITH_REPOSITORY, 0, Integer.MAX_VALUE, null);

				// TODO: implement this
				/*
				 * Schleife über alle Posts
				 * nimm Repository-Speicher-Datum und User
				 * schreibe JSON-Output mit Datum und Flag ob selbst versendet oder durch wen anderes
				 * 
				 * Titel, Link mit intrahash, Datum
				 */
				final JSONObject jsonpost = new JSONObject();
				for (final Post<BibTex> p : posts) {
					final JSONObject jsonObject = new JSONObject();
					final JSONArray jsonArray = new JSONArray();
					jsonArray.addAll(p.getRepositorys());
					jsonObject.put("repositories", jsonArray);
					jsonObject.put("selfsent", (command.getContext().getLoginUser().getName().equals(p.getUser().getName())?1:0) );
					jsonObject.put("intrahash", p.getResource().getIntraHash());
					jsonpost.put(p.getResource().getIntraHash(), jsonObject);
				}
				
				json.put("posts", jsonpost);
				command.setResponseString(json.toString());
				return Views.AJAX_JSON;
			}
			
			if (SHERPAROMEO.equals(action)) {
				// TODO: config via spring + singleton
				this.sherpaLogic = new SherpaRomeoImpl();
		
				if (command.getPublisher() != null) {
					command.setResponseString(this.sherpaLogic.getPolicyForPublisher(command.getPublisher(), command.getqType()));
				}
				if (command.getjTitle() != null) {
					command.setResponseString(this.sherpaLogic.getPolicyForJournal(command.getjTitle(), command.getqType()));			
				}
		
				return Views.AJAX_JSON;
			}
		}
		
		return Views.AJAX_JSON;
	}

}
