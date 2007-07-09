package org.bibsonomy.rest.strategy.tags;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetTagDetailsStrategy extends Strategy {
	private final Tag tag;
	private final String tagName;

	public GetTagDetailsStrategy(final Context context, final String tag) {
		super(context);
		this.tagName = tag;
		this.tag = context.getLogic().getTagDetails(tagName);
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		// delegate to the renderer
		this.getRenderer().serializeTag(writer, this.tag, new ViewModel());
	}

	@Override
	public String getContentType() {
		return "tag";
	}
}