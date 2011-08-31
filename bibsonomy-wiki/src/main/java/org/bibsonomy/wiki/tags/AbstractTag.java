package org.bibsonomy.wiki.tags;

import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import java.io.IOException;

import org.bibsonomy.model.Layout;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.wiki.CVWikiModel;

/**
 * @author philipp
 * @version $Id$
 */
public abstract class AbstractTag extends HTMLTag implements INoBodyParsingTag  {
	
	protected LogicInterface logic;
	protected User requestedUser;
	protected LayoutRenderer<Layout> layoutRenderer;

	/**
	 * 
	 * @param name the name of the tag
	 */
	public AbstractTag(final String name) {
		super(name);
	}

	@Override
	public void renderHTML(final ITextConverter converter, final Appendable buf, final IWikiModel model) throws IOException {
		final CVWikiModel wikiUtil = (CVWikiModel) model;
		this.logic = wikiUtil.getLogic();
		this.requestedUser = wikiUtil.getUser();
		this.layoutRenderer = wikiUtil.getLayoutRenderer();
		buf.append(this.render());
	}
	
	protected abstract StringBuilder render();

}