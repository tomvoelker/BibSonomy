package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.classification.PublicationClassification;
import de.unikassel.puma.openaccess.classification.PublicationClassificatorSingleton;
import de.unikassel.puma.webapp.command.PublicationClassificationCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassificationController extends AjaxController implements MinimalisticController<PublicationClassificationCommand> {
	
	private static final String GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
	private static final String SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
	private static final String REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
	
	private PublicationClassificatorSingleton classificator;
	
	@Override
	public PublicationClassificationCommand instantiateCommand() {
		return new PublicationClassificationCommand();
	}

	@Override
	public View workOn(PublicationClassificationCommand command) {
		
		// check if user is logged in
		if(!command.getContext().isUserLoggedIn()) {
			//TODO access denied ex ?
			return Views.AJAX_TEXT;
		}
		
		if(present(command.getAction()) && command.getAction().equals(GET_AVAILABLE_CLASSIFICATIONS)) {

			Set<String> available = classificator.getInstance().getAvailableClassifications();
			final JSONArray jsonChildList = new JSONArray(available);
			
			final JSONObject json = new JSONObject();
			json.put("available", jsonChildList);
			
			/*
			 * write the output, it will show the JSON-object as a plaintext string
			 */
			command.setResponseString(json.toString());
		} else if(present(command.getAction()) && command.getAction().equals(SAVE_CLASSIFICATION_ITEM)) {

			final JSONObject json = new JSONObject();
			json.put("saveTEST", "Hello World"+command.getHash()+" / "+command.getKey()+" = "+command.getValue());
			command.setResponseString(json.toString());
			
			return Views.AJAX_JSON;
			
		} else if(present(command.getAction()) && command.getAction().equals(REMOVE_CLASSIFICATION_ITEM)) {

			final JSONObject json = new JSONObject();
			json.put("removeTEST", "Hallo Welt "+command.getHash()+" / "+command.getKey());
			command.setResponseString(json.toString());
			
			return Views.AJAX_JSON;
			
		} else {

			List<PublicationClassification> children = classificator.getInstance().getChildren(command.getClassificationName(), command.getId());
			
			final JSONArray jsonChildList = new JSONArray(children);
					
			final JSONObject json = new JSONObject();
			json.put("children", jsonChildList);
			
			/*
			 * write the output, it will show the JSON-object as a plaintext string
			 */
			command.setResponseString(json.toString());
		}
		
		return Views.AJAX_JSON;
	}

	/**
	 * Sets the classificator which provides access to classification schemes.
	 * @param classificator
	 */
	public void setClassificator(PublicationClassificatorSingleton classificator) {
		this.classificator = classificator;
	}
}
