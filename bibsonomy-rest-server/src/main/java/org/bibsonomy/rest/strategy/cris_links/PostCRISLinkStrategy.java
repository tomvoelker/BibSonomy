package org.bibsonomy.rest.strategy.cris_links;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;
import java.util.stream.Collectors;

/**
 * strategy to create a new cris link
 *
 * @author pda
 */
public class PostCRISLinkStrategy extends AbstractCreateStrategy {
	public PostCRISLinkStrategy(Context context) {
		super(context);
	}

	@Override
	protected void render(final Writer writer, final String linkId) {
		this.getRenderer().serializeCRISLinkId(writer, linkId);
	}

	@Override
	protected String create() {
		final CRISLink crisLink = this.getRenderer().parseCRISLink(this.doc);
		final JobResult jobResult = this.getLogic().createCRISLink(crisLink);
		if (Status.FAIL.equals(jobResult.getStatus())) {
			throw new BadRequestOrResponseException(jobResult.getErrors().stream().
							map(ErrorMessage::getDefaultMessage).collect(Collectors.joining(",")));
		}
		return crisLink.getSource().getLinkableId() + "-" + crisLink.getTarget().getLinkableId();
	}
}
