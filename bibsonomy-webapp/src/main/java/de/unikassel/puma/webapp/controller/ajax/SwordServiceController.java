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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.common.exceptions.SwordException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Repository;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;
import org.springframework.context.MessageSource;

import de.unikassel.puma.common.ReportingMode;
import de.unikassel.puma.openaccess.sword.PumaData;
import de.unikassel.puma.openaccess.sword.SwordService;
import de.unikassel.puma.webapp.command.ajax.SwordServiceCommand;

/**
 * @author philipp
 */
@Getter
@Setter
public class SwordServiceController extends AjaxController implements MinimalisticController<SwordServiceCommand> {
	
	private SwordService swordService;
	private ReportingMode mode;
	private MessageSource messageSource;

	@Override
	public View workOn(final SwordServiceCommand command) {
		if (!command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		
		final User user = command.getContext().getLoginUser();
		if (ReportingMode.GROUP.equals(this.mode) && !UserUtils.userIsGroup(user)) {
			throw new AccessDeniedException("you are not allowed to report publications");
		}
		
		String message = "error.sword.sentsuccessful";
		int statuscode = 1; // statuscode = 1: ok; = 0: error 

		
		final Post<BibTex> post = this.getPostToHash(command.getIntrahash(), user.getName());
		
		if (!present(post)) {
			// TODO: do something
			return Views.AJAX_JSON;
		}
		
		// add some metadata to post
		final PumaData<BibTex> pumaData = command.getPumaData();
		pumaData.setPost(post);

		try {
			// TODO: do not throw an exception if transfer was ok
			this.swordService.submitDocument(pumaData, user);
		} catch (final SwordException | FileNotFoundException ex) {
			
			// send message of exception to webpage via ajax to give feedback of submission result
			message = ex.getMessage();
			
			// errcode 2xx is ok / 200, 201, 202
			if (message.startsWith("error.sword.errcode2")){
				// transmission complete and successful
				statuscode = 1;
				message = "error.sword.sentsuccessful";
			} else {
				// Error
				statuscode = 0;
			}
		}

		// log successful store to repository 
		if (statuscode == 1) {
			//final Post<?> createdPost = logic.getPostDetails(command.getResourceHash(), user.getName());
			final List<Repository> repositories = new ArrayList<Repository>();
			final Repository repo = new Repository();
			repo.setId("REPOSITORY");  // TODO: set ID to current repository - it should be possible in future to send a post to multiple/different repositories
			repositories.add(repo);
			post.setRepositories(repositories);
	
			this.logic.updatePosts(Collections.singletonList(post), PostUpdateOperation.UPDATE_REPOSITORIES);
		}
	
		final JSONObject json = new JSONObject();
		final JSONObject jsonResponse = new JSONObject();

		final Locale locale = this.requestLogic.getLocale();
		
		jsonResponse.put("statuscode", statuscode);
		jsonResponse.put("message", message);
		jsonResponse.put("localizedMessage", this.messageSource.getMessage(message, null, locale));
		json.put("response", jsonResponse);
		
		/*
		 * write the output, it will show the JSON-object as a plaintext string
		 */
		command.setResponseString(json.toString());
		
		return Views.AJAX_JSON;
	}
	
	@SuppressWarnings("unchecked")
	private Post<BibTex> getPostToHash(final String intraHash, final String userName) {
		try {
			return (Post<BibTex>) this.logic.getPostDetails(intraHash, userName);
		} catch (final ObjectNotFoundException ex) {
			return null;
		} catch (final ObjectMovedException ex) {
			return getPostToHash(ex.getNewId(), userName);
		}
	}

	@Override
	public SwordServiceCommand instantiateCommand() {
		SwordServiceCommand command = new SwordServiceCommand();
		command.setPumaData(new PumaData<>());
		return command;
	}
}
