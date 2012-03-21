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
import org.bibsonomy.webapp.command.resource.PublicationPageCommand;
import org.bibsonomy.webapp.command.resource.ResourcePageCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 * @version $Id$
 */
public class PublicationPageController extends AbstractResourcePageController<BibTex, GoldStandardPublication> {

	@Override
	public ResourcePageCommand<BibTex> instantiateCommand() {
		return new PublicationPageCommand();
	}
	
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
		
		//if creating first discussion item on normal post
		if (!present(goldStandard)) {
			return;
		}
		
		//remove all discussion if user self has not discussed
		if(goldStandard.getResource().getEntrytype().equals(PREPRINT)) {
			List<DiscussionItem> discussionItems = goldStandard.getResource().getDiscussionItems();
			for (DiscussionItem item : discussionItems) {
				if(item.getUser().equals(loginUser)) {
					return;
				}
			}
			discussionItems.clear();
		}
	}
}
