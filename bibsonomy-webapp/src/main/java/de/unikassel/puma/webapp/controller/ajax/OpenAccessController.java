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

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import de.unikassel.puma.openaccess.sherparomeo.SherpaRomeoImpl;
import de.unikassel.puma.openaccess.sherparomeo.SherpaRomeoInterface;
import de.unikassel.puma.webapp.command.ajax.OpenAccessCommand;

/**
 * @author clemens
 */
public class OpenAccessController extends AjaxController implements MinimalisticController<OpenAccessCommand> {

	private static final String GET_SENT_REPOSITORIES = "GET_SENT_REPOSITORIES";
	private static final String SHERPAROMEO = "SHERPAROMEO";

	private SherpaRomeoInterface sherpaLogic;
	
	private int maxQuerySize;
	
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
				// TODO: adapt limit to get all posts
				final List<Post<BibTex>> posts = logic.getPosts(BibTex.class, GroupingEntity.USER, command.getContext().getLoginUser().getName(), null, command.getInterhash(), null, SearchType.LOCAL, Sets.<Filter>asSet(FilterEntity.POSTS_WITH_REPOSITORY), null, null, null, 0, this.maxQuerySize);

				// TODO: implement this
				/*
				 * Schleife Ã¼ber alle Posts
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

	/**
	 * @return the maxQuerySize
	 */
	public int getMaxQuerySize() {
		return this.maxQuerySize;
	}

	/**
	 * @param maxQuerySize the maxQuerySize to set
	 */
	public void setMaxQuerySize(int maxQuerySize) {
		this.maxQuerySize = maxQuerySize;
	}

}
