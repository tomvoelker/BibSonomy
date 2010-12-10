package org.bibsonomy.webapp.controller.ajax;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bibsonomy.classification.PublicationClassificator;
import org.bibsonomy.model.PublicationClassification;
import org.bibsonomy.webapp.command.actions.PublicationClassificationCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassificationController extends AjaxController implements MinimalisticController<PublicationClassificationCommand> {
	
	private PublicationClassificator classification;
	
	@Override
	public PublicationClassificationCommand instantiateCommand() {
		return new PublicationClassificationCommand();
	}

	@Override
	public View workOn(PublicationClassificationCommand command) {
		
		List<PublicationClassification> children = classification.getChildren(command.getClassificationName(), command.getId());
		
		final JSONArray jsonChildList = new JSONArray(children);
				
		final JSONObject json = new JSONObject();
		json.put("children", jsonChildList);
		
		/*
		 * write the output, it will show the JSON-object as a plaintext string
		 */
		
		command.setResponseString(json.toString());
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//		response.getOutputStream().write(json.toString().getBytes("UTF-8"));

		return Views.AJAX_JSON;
	}
	
	/**
	 * @param classification
	 */
	public void setClassification(PublicationClassificator classification) {
		this.classification = classification;
	}

}
