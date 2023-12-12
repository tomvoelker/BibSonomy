/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
			switch (action) {
				case GET_AVAILABLE_CLASSIFICATIONS:
					final JSONArray jsonArray = new JSONArray();
					jsonArray.addAll(this.classificator.getAvailableClassifications());
					json.put("available", jsonArray);
					break;
				case SAVE_CLASSIFICATION_ITEM:
					// save classification data to database
					// implement return value to verify storing of classification
					this.logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());
					this.logic.createExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());

					// TODO: why are we here returning "Hello world "?
					json.put("saveTEST", "Hello World" + command.getHash() + " / " + command.getKey() + " = " + command.getValue());
					break;
				case REMOVE_CLASSIFICATION_ITEM:
					// delete extended fields
					this.logic.deleteExtendedField(BibTex.class, loginUserName, command.getHash(), command.getKey(), command.getValue());

					// TODO: why are we here returning "Hallo Welt "?
					json.put("removeTEST", "Hallo Welt " + command.getHash() + " / " + command.getKey());
					break;
				case GET_POST_CLASSIFICATION_LIST: {
					// get extended fields
					final Map<String, List<String>> classificationMap = this.logic.getExtendedFields(BibTex.class, loginUserName, command.getHash(), null);

					// build json output
					final Set<Classification> availableClassifications = this.classificator.getAvailableClassifications();
					final Set<String> availableClassificationsNames = new HashSet<String>();
					for (final Classification cfn : availableClassifications) {
						availableClassificationsNames.add(cfn.getName());
					}
					for (final Entry<String, List<String>> classificationEntry : classificationMap.entrySet()) {
						if (availableClassificationsNames.contains(classificationEntry.getKey())) {
							json.put(classificationEntry.getKey(), classificationEntry.getValue());
						}
					}
					break;
				}
				case GET_CLASSIFICATION_DESCRIPTION:
					json.put("name", command.getKey());
					json.put("value", command.getValue());
					json.put("description", this.classificator.getDescription(command.getKey(), command.getValue()));
					break;
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
