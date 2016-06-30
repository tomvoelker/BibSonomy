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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
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
import org.springframework.context.MessageSource;

import de.unikassel.puma.common.ReportingMode;
import de.unikassel.puma.openaccess.sword.PumaData;
import de.unikassel.puma.openaccess.sword.SwordService;
import de.unikassel.puma.webapp.command.ajax.SwordServiceCommand;

/**
 * @author philipp
 */
public class SwordServiceController extends AjaxController implements MinimalisticController<SwordServiceCommand> {
	
	private SwordService swordService;
	private MessageSource messageSource;
	private ReportingMode mode;

	@Override
	public SwordServiceCommand instantiateCommand() {
		return new SwordServiceCommand();
	}

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

		
		final Post<BibTex> post = this.getPostToHash(command.getResourceHash(), user.getName());
		
		if (!present(post)) {
			// TODO: do something
		}
		
		// add some metadata to post
		final PumaData<BibTex> pumaData = new PumaData<BibTex>();
		pumaData.setPost(post);
		
		// get additional metadata
		final Map<String, List<String>> metadataMap = this.logic.getExtendedFields(BibTex.class, command.getContext().getLoginUser().getName(), post.getResource().getIntraHash(), null);
		// TODO is use of PublicationClassificatorSingleton classification here possible?
//		Set<String> availableClassifications = classificator.getInstance().getAvailableClassifications(); 
		
		for (final Entry<String, List<String>> item : metadataMap.entrySet()) {
			final String firstValue = item.getValue().get(0);
			final String key = item.getKey();
			
			if (SwordService.AF_INSTITUTION.equals(key)) pumaData.setExaminstitution(firstValue);
			else if (SwordService.AF_PHDREFEREE.equals(key)) pumaData.addExamreferee(firstValue);
			else if (SwordService.AF_PHDREFEREE2.equals(key)) pumaData.addExamreferee(firstValue);
			else if (SwordService.AF_PHDORALEXAM.equals(key)) pumaData.setPhdoralexam(firstValue);
			else if (SwordService.AF_SPONSOR.equals(key)) pumaData.addSponsor(firstValue);
			else if (SwordService.AF_ADDITIONALTITLE.equals(key)) pumaData.addAdditionaltitle(firstValue);
			else pumaData.addClassification(key, item.getValue());

//			if (availableClassifications.contains(item.getKey())) {
//				pumaData.addClassification(item.getKey(), item.getValue());
//			}
		}

		try {
			// TODO: do not throw an exception if transfer was ok
			this.swordService.submitDocument(pumaData, user);
		} catch (final SwordException ex) {
			
			// send message of exception to webpage via ajax to give feedback of submission result
			message = ex.getMessage();
			
			// errcode 2xx is ok / 200, 201, 202
			if (message.substring(0, 20).equals("error.sword.errcode2")){
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
			final List<Repository> repositorys = new ArrayList<Repository>();
			final Repository repo = new Repository();
			repo.setId("REPOSITORY");  // TODO: set ID to current repository - it should be possible in fututre to send a post to multiple/different repositories 
			repositorys.add(repo);
			post.setRepositorys(repositorys);
	
			this.logic.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_REPOSITORY);
		}
	
		final JSONObject json = new JSONObject();
		final JSONObject jsonResponse = new JSONObject();

		final Locale locale = this.requestLogic.getLocale();
		
		jsonResponse.put("statuscode", statuscode);
		jsonResponse.put("message", message);
		// TODO: get from somewhere localized messages to transmit via ajax
		// localizedMessage = puma.repository.response.$message
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
		} catch (final ResourceMovedException ex) {
			return getPostToHash(ex.getNewIntraHash(), userName);
		}
	}
	
	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param swordService
	 */
	public void setSwordService(final SwordService swordService) {
		this.swordService = swordService;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(ReportingMode mode) {
		this.mode = mode;
	}
}
