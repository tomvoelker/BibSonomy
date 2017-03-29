/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unikassel.puma.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Classification;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.classification.PublicationClassificator;
import de.unikassel.puma.openaccess.sword.SwordService;
import de.unikassel.puma.webapp.command.ajax.PublicationClassificationCommand;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * @author philipp
 */
public class PublicationClassificationController extends AjaxController implements MinimalisticController<PublicationClassificationCommand> {

	private static final String GET_AVAILABLE_CLASSIFICATIONS = "AVAILABLE_CLASSIFICATIONS";
	private static final String SAVE_CLASSIFICATION_ITEM = "SAVE_CLASSIFICATION_ITEM";
	private static final String GET_ADDITIONAL_METADATA = "GET_ADDITIONAL_METADATA";
	private static final String SAVE_ADDITIONAL_METADATA = "SAVE_ADDITIONAL_METADATA";
	private static final String REMOVE_CLASSIFICATION_ITEM = "REMOVE_CLASSIFICATION_ITEM";
	private static final String GET_POST_CLASSIFICATION_LIST = "GET_POST_CLASSIFICATION_LIST"; 
	private static final String GET_CLASSIFICATION_DESCRIPTION = "GET_CLASSIFICATION_DESCRIPTION"; 

	private PublicationClassificator classificator;

	@Override
	public PublicationClassificationCommand instantiateCommand() {
		return new PublicationClassificationCommand();
	}

	@Override
	public View workOn(final PublicationClassificationCommand command) {
		// check if user is logged in
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}

		final String loginUserName = command.getContext().getLoginUser().getName();
		final JSONObject json = new JSONObject();
		final String action = command.getAction();
		
		if (present(action)) {
			if (GET_AVAILABLE_CLASSIFICATIONS.equals(action)) {
				final JSONArray jsonArray = new JSONArray();
				jsonArray.addAll(this.classificator.getAvailableClassifications());
				json.put("available", jsonArray);
			} else if (SAVE_CLASSIFICATION_ITEM.equals(action)) {
				// save classification data to database
				// implement return value to verify storing of classification 
				this.logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());				
				this.logic.createExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());

				// TODO: why are we here returning "Hello world "?
				json.put("saveTEST", "Hello World"+command.getHash()+" / "+command.getKey()+" = "+command.getValue());
			} else if(SAVE_ADDITIONAL_METADATA.equals(action)) {
				final JSONObject jsonData = (JSONObject) JSONSerializer.toJSON(command.getValue());

				// save classification data to database
				for (final String key : SwordService.AF_FIELD_NAMES) {
					// implement return value to verify storing of classification 
					this.logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), key, null);				
					this.logic.createExtendedField(BibTex.class, loginUserName, command.getHash(), key, jsonData.getString(key));
				}

				// TODO: why are we here returning "Hello world "?
				json.put("saveTEST", "Hello World" + command.getHash() + " / " + command.getKey() + " = " + command.getValue());
			} else if(GET_ADDITIONAL_METADATA.equals(action)) {
				// get extended fields
				final Map<String, List<String>> classificationMap = this.logic.getExtendedFields(BibTex.class, loginUserName, command.getHash(), null);

				// build json output  
				final Set<Classification> availableClassifications = this.classificator.getAvailableClassifications();
				L: for (final Entry<String, List<String>> entry : classificationMap.entrySet()) {
					for(final Classification c : availableClassifications) {
						if(c.getName().equals(entry.getKey()))
							continue L;
					}
					json.put(entry.getKey(), entry.getValue());
					
				}
			} else if (REMOVE_CLASSIFICATION_ITEM.equals(action)) {
				// delete extended fields
				this.logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());

				// TODO: why are we here returning "Hallo Welt "?
				json.put("removeTEST", "Hallo Welt " + command.getHash() + " / " + command.getKey());
			} else if (GET_POST_CLASSIFICATION_LIST.equals(action)) {
				// get extended fields
				final Map<String, List<String>> classificationMap = this.logic.getExtendedFields(BibTex.class, loginUserName, command.getHash(), null);

				// build json output  
				final Set<Classification> availableClassifications = this.classificator.getAvailableClassifications();
				final Set<String> availableClassificationsNames = new HashSet<String>();
				for (final Classification cfn : availableClassifications) {
					availableClassificationsNames.add(cfn.getName());
				} 
				for (final Entry<String, List<String>> classificationEntry : classificationMap.entrySet()) {
					if ( availableClassificationsNames.contains(classificationEntry.getKey())) {
						json.put(classificationEntry.getKey(), classificationEntry.getValue());
					}
				}
			} else if (GET_CLASSIFICATION_DESCRIPTION.equals(action)) {
				json.put("name", command.getKey());
				json.put("value", command.getValue());
				json.put("description", this.classificator.getDescription(command.getKey(), command.getValue()));
			}
		} else {
			final JSONArray jsonArray = new JSONArray();
			jsonArray.addAll(this.classificator.getChildren(command.getClassificationName(), command.getId()));
			json.put("children", jsonArray);
		}
		
		command.setResponseString(json.toString());
		return Views.AJAX_JSON;
	}

	/**
	 * Sets the classificator which provides access to classification schemes.
	 * @param classificator
	 */
	public void setClassificator(final PublicationClassificator classificator) {
		this.classificator = classificator;
	}
}
