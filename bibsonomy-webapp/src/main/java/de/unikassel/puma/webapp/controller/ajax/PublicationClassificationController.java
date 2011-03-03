package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.classification.PublicationClassification;
import de.unikassel.puma.openaccess.classification.PublicationClassificatorSingleton;
import de.unikassel.puma.openaccess.sword.SwordService;
import de.unikassel.puma.webapp.command.PublicationClassificationCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassificationController extends AjaxController implements MinimalisticController<PublicationClassificationCommand> {
	
	private static final Log log = LogFactory.getLog(SwordService.class);
	private static final String GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
	private static final String SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
	private static final String SAVE_CLASSIFICATION_ITEMS = "SAVE_CLASSIFICATION_ITEMS";
	private static final String REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
	private static final String GET_POST_CLASSIFICATION_LIST = "GET_POST_CLASSIFICATION_LIST"; 
	
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

			// save classification data to database
			// implement return value to verify storing of classification 
			logic.createExtendedField(command.getContext().getLoginUser().getName(), command.getHash(), command.getKey(), command.getValue());

			// generate json return value
			final JSONObject json = new JSONObject();
			json.put("saveTEST", "Hello World"+command.getHash()+" / "+command.getKey()+" = "+command.getValue());
			command.setResponseString(json.toString());
			
			return Views.AJAX_JSON;
			
		} else if(present(command.getAction()) && command.getAction().equals(SAVE_CLASSIFICATION_ITEMS)) {

			
			ArrayList<String> dataFields = new ArrayList<String>();
			dataFields.add("post.resource.openaccess.additionalfields.institution");
			dataFields.add("post.resource.openaccess.additionalfields.phdreferee");
			dataFields.add("post.resource.openaccess.additionalfields.phdoralexam");
			dataFields.add("post.resource.openaccess.additionalfields.sponsor");
			dataFields.add("post.resource.openaccess.additionalfields.additionaltitle");
			
			JSONObject jsonData = null;
			jsonData = (JSONObject) JSONSerializer.toJSON(command.getValue());

			// save classification data to database
			Iterator<?> i = dataFields.iterator();
			while (i.hasNext()) {
				String key = (String)i.next();
				// implement return value to verify storing of classification 
				logic.createExtendedField(command.getContext().getLoginUser().getName(), command.getHash(), key, jsonData.getString(key));
			}
			
			// generate json return value
			final JSONObject json = new JSONObject();
			json.put("saveTEST", "Hello World"+command.getHash()+" / "+command.getKey()+" = "+command.getValue());
			command.setResponseString(json.toString());
			
			return Views.AJAX_JSON;
			
		} else if(present(command.getAction()) && command.getAction().equals(REMOVE_CLASSIFICATION_ITEM)) {

			// delete extended fields
			logic.deleteExtendedField(command.getContext().getLoginUser().getName(), command.getHash(), command.getKey(), command.getValue());

			final JSONObject json = new JSONObject();
			json.put("removeTEST", "Hallo Welt "+command.getHash()+" / "+command.getKey());
			command.setResponseString(json.toString());
			
			return Views.AJAX_JSON;

		} else if(present(command.getAction()) && command.getAction().equals(GET_POST_CLASSIFICATION_LIST)) {
			
			// get extended fields
			Map<String, List<String>> classificationMap = logic.getExtendedFields(command.getContext().getLoginUser().getName(), command.getHash(), null);
			
			Iterator<?> i = classificationMap.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry<String, List<String>> pairs = (Map.Entry<String, List<String>>)i.next();
			    log.info(pairs.getKey()+": "+pairs.getValue());
			}
			
			final JSONObject json = new JSONObject();
			json.put("getListTEST", "Hallo Welt");
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
