package org.bibsonomy.rest.strategy.cris_links;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;
import java.util.stream.Collectors;

public class PutCRISLinkStrategy extends AbstractUpdateStrategy {
	/**
	 * @param context
	 */
	public PutCRISLinkStrategy(Context context) {
		super(context);
	}

	@Override
	protected void render(Writer writer, String linkId) {
		this.getRenderer().serializeCRISLinkId(writer, linkId);
	}

	@Override
	protected String update() {
		final CRISLink crisLink = this.getRenderer().parseCRISLink(this.doc);
		final JobResult jobResult = this.getLogic().updateCRISLink(crisLink);
		if (Status.FAIL.equals(jobResult.getStatus())) {
			throw new BadRequestOrResponseException(jobResult.getErrors().stream().
							map(ErrorMessage::getDefaultMessage).collect(Collectors.joining(",")));
		}
		return crisLink.getSource().getLinkableId() + "-" + crisLink.getTarget().getLinkableId();
	}
}
