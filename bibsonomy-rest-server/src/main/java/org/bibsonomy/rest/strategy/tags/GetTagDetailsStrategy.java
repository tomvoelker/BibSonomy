package org.bibsonomy.rest.strategy.tags;

import java.io.ByteArrayOutputStream;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.xml.tools.EscapingPrintWriter;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetTagDetailsStrategy extends Strategy {
	private final Tag tag;
	private final String tagName;
	private Writer writer;

	/**
	 * @param context
	 * @param tag
	 */
	public GetTagDetailsStrategy(final Context context, final String tag) {
		super(context);
		this.tagName = tag;
		this.tag = context.getLogic().getTagDetails(tagName);
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		writer = new EscapingPrintWriter(outStream);
		if (this.tag == null) {
			throw new NoSuchResourceException("The requested tag '" + this.tagName + "' does not exist.");
		}		
		// delegate to the renderer
		this.getRenderer().serializeTag(writer, this.tag, new ViewModel());
	}

	@Override
	public String getContentType() {
		return "tag";
	}
}