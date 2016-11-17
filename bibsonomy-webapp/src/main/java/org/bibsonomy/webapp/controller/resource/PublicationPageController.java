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
package org.bibsonomy.webapp.controller.resource;

import static org.bibsonomy.model.util.BibTexUtils.PREPRINT;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.resource.PublicationPageCommand;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 */
public class PublicationPageController extends AbstractResourcePageController<BibTex, GoldStandardPublication> {
	private URLGenerator urlGenerator;
	
	@Override
	public ResourcePageCommand<BibTex> instantiateCommand() {
		return new PublicationPageCommand();
	}
	
	/* see issue #2512
	 * (non-Javadoc)
	 * @see org.bibsonomy.webapp.controller.resource.AbstractResourcePageController#workOnResource(org.bibsonomy.webapp.command.resource.ResourcePageCommand, java.lang.String, java.lang.String, java.lang.String, org.bibsonomy.common.enums.GroupingEntity)
	 *
	@Override
	protected View workOnResource(ResourcePageCommand<BibTex> command, String format, String longHash, String requUser, GroupingEntity groupingEntity) {
		final String title = command.getRequestedTitle();
		// redirect to /bibtex/interhash_title if only /bibtex/interhash is requested
		
		if ("html".equals(command.getFormat()) && !present(title) && GroupingEntity.ALL.equals(groupingEntity)) {
			this.setList(command, this.getResourceClass(), groupingEntity, requUser, null, longHash, null, command.getFilter(), null, null, null, 1);
			final List<Post<BibTex>> posts = command.getListCommand(this.getResourceClass()).getList();
			if (present(posts)) {
				final Post<BibTex> firstPost = posts.get(0);
				final BibTex publication = firstPost.getResource();
				if (present(publication.getTitle())) {
					return new ExtendedRedirectView(this.urlGenerator.getPublicationUrl(publication), true);
				}
			}
		}
		return super.workOnResource(command, format, longHash, requUser, groupingEntity);
	}
	*/
	
	@Override
	protected View handleFormat(final ResourcePageCommand<BibTex> command, final String format, final String longHash, final String requUser, final GroupingEntity groupingEntity, final String goldHash, final Post<GoldStandardPublication> goldStandard, final BibTex firstResource) {
		// TODO: maybe we should move this format handling to a separate controller
		if ("authoragreement".equals(format)) {
			// get additional metadata fields
			final Map<String, List<String>> additionalMetadataMap = this.logic.getExtendedFields(BibTex.class, command.getContext().getLoginUser().getName(), this.shortHash(longHash), null);
			((PublicationPageCommand)command).setAdditionalMetadata(additionalMetadataMap);
			
			return Views.AUTHORAGREEMENTPAGE;
		}
		
		return super.handleFormat(command, format, longHash, requUser, groupingEntity, goldHash, goldStandard, firstResource);
	}
	
	@Override
	protected View getResourcePage() {
		return Views.BIBTEXPAGE;
	}

	@Override
	protected View getDetailsView() {
		return Views.BIBTEXDETAILS;
	}

	@Override
	protected Class<BibTex> getResourceClass() {
		return BibTex.class;
	}
	
	@Override
	protected Class<GoldStandardPublication> getGoldStandardClass() {
		return GoldStandardPublication.class;
	}

	/**
	 * Handles discussionItems for preprint entry type.
	 * Remove this method and here and in parent class, if you want remove preprint entry type
	 * @param goldStandard
	 * @param loginUser
	 */
	@Override
	protected void handleDiskussionItems(Post<GoldStandardPublication> goldStandard, User loginUser) {
		// if creating first discussion item on normal post
		if (!present(goldStandard)) {
			return;
		}
		
		// remove all discussion if user self has not discussed
		if (goldStandard.getResource().getEntrytype().equals(PREPRINT)) {
			List<DiscussionItem> discussionItems = goldStandard.getResource().getDiscussionItems();
			for (DiscussionItem item : discussionItems) {
				if(item.getUser().equals(loginUser)) {
					return;
				}
			}
			discussionItems.clear();
		}
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
