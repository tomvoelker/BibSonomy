package org.bibsonomy.rest.strategy.tags;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetTagDetailsStrategy extends Strategy {

	private final String tagName;

	public GetTagDetailsStrategy(final Context context, final String tag) {
		super(context);
		this.tagName = tag;
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		// delegate to the renderer
		final Tag tag = this.context.getLogic().getTagDetails(tagName);
		this.context.getRenderer().serializeTag(writer, tag, new ViewModel());
	}

	@Override
	public String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/tag+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}