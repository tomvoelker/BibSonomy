package org.bibsonomy.webapp.controller.ajax;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bibsonomy.webapp.command.actions.PublicationClassificationCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.classification.PublicationClassification;
import de.unikassel.puma.openaccess.classification.PublicationClassificator;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassificationController extends AjaxController implements MinimalisticController<PublicationClassificationCommand> {
	
	private PublicationClassificator classificator;
	
	@Override
	public PublicationClassificationCommand instantiateCommand() {
		return new PublicationClassificationCommand();
	}

	@Override
	public View workOn(PublicationClassificationCommand command) {
		
		List<PublicationClassification> children = classificator.getChildren(command.getClassificationName(), command.getId());
		
		final JSONArray jsonChildList = new JSONArray(children);
				
		final JSONObject json = new JSONObject();
		json.put("children", jsonChildList);
		
		/*
		 * write the output, it will show the JSON-object as a plaintext string
		 */
		command.setResponseString(json.toString());

		return Views.AJAX_JSON;
	}

	/**
	 * Sets the classificator which provides access to classification schemes.
	 * @param classificator
	 */
	public void setClassificator(PublicationClassificator classificator) {
		this.classificator = classificator;
	}
}
