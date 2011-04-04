package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.classification.PublicationClassificatorSingleton;
import de.unikassel.puma.openaccess.sword.SwordService;
import de.unikassel.puma.webapp.command.PublicationClassificationCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassificationController extends AjaxController implements MinimalisticController<PublicationClassificationCommand> {

	private static final String GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
	private static final String SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
	private static final String GET_ADDITIONAL_METADATA = "GET_ADDITIONAL_METADATA";
	private static final String SAVE_ADDITIONAL_METADATA = "SAVE_ADDITIONAL_METADATA";
	private static final String REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
	private static final String GET_POST_CLASSIFICATION_LIST = "GET_POST_CLASSIFICATION_LIST"; 
	private static final String GET_CLASSIFICATION_DESCRIPTION = "GET_CLASSIFICATION_DESCRIPTION"; 

	private PublicationClassificatorSingleton classificator;

	@Override
	public PublicationClassificationCommand instantiateCommand() {
		return new PublicationClassificationCommand();
	}

	@Override
	public View workOn(PublicationClassificationCommand command) {

		// check if user is logged in
		if(!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}

		final String loginUserName = command.getContext().getLoginUser().getName();
		final JSONObject json = new JSONObject();
		final String action = command.getAction();
		
		if (present(action)) {

			if (GET_AVAILABLE_CLASSIFICATIONS.equals(action)) {
				json.put("available", new JSONArray(classificator.getInstance().getAvailableClassifications()));
			} else if(SAVE_CLASSIFICATION_ITEM.equals(action)) {

				// save classification data to database
				// implement return value to verify storing of classification 
				logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());				
				logic.createExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());

				json.put("saveTEST", "Hello World"+command.getHash()+" / "+command.getKey()+" = "+command.getValue());
			} else if(SAVE_ADDITIONAL_METADATA.equals(action)) {
				final JSONObject jsonData = (JSONObject) JSONSerializer.toJSON(command.getValue());

				// save classification data to database
				for (final String key : SwordService.AF_FIELD_NAMES) {
					// implement return value to verify storing of classification 
					logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), key, null);				
					logic.createExtendedField(BibTex.class, loginUserName, command.getHash(), key, jsonData.getString(key));
				}

				json.put("saveTEST", "Hello World"+command.getHash()+" / "+command.getKey()+" = "+command.getValue());
			} else if(GET_ADDITIONAL_METADATA.equals(action)) {
				// get extended fields
				final Map<String, List<String>> classificationMap = logic.getExtendedFields(BibTex.class, loginUserName, command.getHash(), null);

				// build json output  
				final Set<String> availableClassifications = classificator.getInstance().getAvailableClassifications();
				for (final Entry<String, List<String>> entry : classificationMap.entrySet()) {
					if ( !availableClassifications.contains(entry.getKey())) {
						json.put(entry.getKey(), entry.getValue());
					}
				}
			} else if(REMOVE_CLASSIFICATION_ITEM.equals(action)) {

				// delete extended fields
				logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());

				json.put("removeTEST", "Hallo Welt "+command.getHash()+" / "+command.getKey());
			} else if(GET_POST_CLASSIFICATION_LIST.equals(action)) {
				// get extended fields
				final Map<String, List<String>> classificationMap = logic.getExtendedFields(BibTex.class, loginUserName, command.getHash(), null);

				// build json output  
				final Set<String> availableClassifications = classificator.getInstance().getAvailableClassifications();
				for (final Entry<String, List<String>> entry : classificationMap.entrySet()) {
					if ( availableClassifications.contains(entry.getKey())) {
						json.put(entry.getKey(), entry.getValue());
					}
				}
			} else if(GET_CLASSIFICATION_DESCRIPTION.equals(action)) {
				json.put("name", command.getKey());
				json.put("value", command.getValue());
				json.put("description", classificator.getInstance().getDescription(command.getKey(), command.getValue()));
			}
		} else {
			json.put("children", new JSONArray(classificator.getInstance().getChildren(command.getClassificationName(), command.getId())));
		}
		command.setResponseString(json.toString());
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
