package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import java.io.IOException;

import org.bibsonomy.model.Group;
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
	protected Group requestedGroup;
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
		final CVWikiModel wiki = (CVWikiModel) model;
		this.logic = wiki.getLogic();
		this.requestedUser = wiki.getRequestedUser();
		this.requestedGroup = wiki.getRequestedGroup();
		this.layoutRenderer = wiki.getLayoutRenderer();
		buf.append(this.render());
	}
	
	
	protected StringBuilder renderString(final String toRender) {
		final StringBuilder renderedHTML = new StringBuilder();
		if (present(toRender)) {
			renderedHTML.append(Utils.escapeXmlChars(toRender));
		}
		return renderedHTML;
	}
	
	protected StringBuilder renderParagraph(final String toRender) {
		final StringBuilder renderedHTML = new StringBuilder();
		if (present(toRender)) {
			renderedHTML.append("<p class='align'>");
			renderedHTML.append(Utils.escapeXmlChars(toRender));
			renderedHTML.append("</p>");
		}
		return renderedHTML;
	}

	/*
	 * TODO comment
	 */
	protected abstract StringBuilder render();

}