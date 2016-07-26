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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ajax.PrivateNoteAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author wla
 */
public class PrivateNoteAjaxController extends AjaxController implements MinimalisticController<PrivateNoteAjaxCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(PrivateNoteAjaxController.class);
	
	private Errors errors;
	
	@Override
	public PrivateNoteAjaxCommand instantiateCommand() {
		return new PrivateNoteAjaxCommand();
	}

	@Override
	public View workOn(final PrivateNoteAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}
		
		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
		}
		
		final Post<? extends Resource> post = this.logic.getPostDetails(command.getIntraHash(), context.getLoginUser().getName());
		if (!present(post)) {
			this.errors.reject("error.general");
			log.error("post not found");
		}
		
		if (this.errors.hasErrors()) {
			return this.getErrorView();
		}
		
		final BibTex bib = (BibTex) post.getResource();
		bib.setPrivnote(command.getPrivateNote());
		
		this.logic.updatePosts(Collections.<Post<?>>singletonList(post), PostUpdateOperation.UPDATE_ALL);
		
		command.setResponseString("OK");
		return Views.AJAX_JSON;
	}

	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

}