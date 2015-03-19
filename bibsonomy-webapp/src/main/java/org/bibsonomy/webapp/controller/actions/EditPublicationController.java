/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.MyOwnSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

import de.unikassel.puma.openaccess.sword.SwordService;

/**
 * 
 * For strange Java Generics reasons I could not implement the 
 * {@link #instantiateEditPostCommand()} method in exactly the
 * same way in the {@link AbstractEditPublicationController}. Thus I had
 * to make that controller abstract and implement the method 
 * here.
 * 
 * The underlying problem is a bit deeper: I had to parameterize
 * {@link AbstractEditPublicationController} to subclass it in
 * {@link PostPublicationController}.
 * 
 * @author rja
 */
public class EditPublicationController extends AbstractEditPublicationController<EditPublicationCommand> {

	private SwordService swordService = null;

	@Override
	protected EditPublicationCommand instantiateEditPostCommand() {
		return new EditPublicationCommand();
	}
	
	@Override
	protected View finalRedirect(String userName, Post<BibTex> post, String referer) {
		/*
		 * If a SWORD service is configured and the user claims to be the creator of the 
		 * publication, we forward him to the SWORD service to allow the user to upload the
		 * publication.
		 */
		
		if (present(swordService) && SystemTagsUtil.containsSystemTag(post.getTags(), MyOwnSystemTag.NAME)) {
			String ref = UrlUtils.safeURIEncode(referer);
			String publicationUrl = urlGenerator.getPublicationUrlByIntraHashAndUsername(post.getResource().getIntraHash(), userName);
			return new ExtendedRedirectView(publicationUrl + "?referer=" + ref);
		}
		
		return super.finalRedirect(userName, post, referer);
	}

	/**
	 * @param swordService the swordService to set
	 */
	public void setSwordService(SwordService swordService) {
		this.swordService = swordService;
	}
}
