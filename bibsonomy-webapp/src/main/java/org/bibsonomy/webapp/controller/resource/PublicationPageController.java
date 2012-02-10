package org.bibsonomy.webapp.controller.resource;

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
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
}
