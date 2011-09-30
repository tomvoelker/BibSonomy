package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.wiki.CVWikiModel;

/**
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id$
 */
public abstract class AbstractTag extends HTMLTag implements INoBodyParsingTag  {
	private static final Log log = LogFactory.getLog(AbstractTag.class);
	protected LogicInterface logic;
	public User requestedUser;
	public Group requestedGroup;
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
	
	
	protected String renderString(final String toRender) {
		if (present(toRender)) {
			return Utils.escapeXmlChars(toRender);
		}
		return "";
	}
	
	protected String renderParagraph(final String toRender) {
		final StringBuilder renderedHTML = new StringBuilder();
		if (present(toRender)) {
			renderedHTML.append("<p class='align'>");
			renderedHTML.append(Utils.escapeXmlChars(toRender));
			renderedHTML.append("</p>");
		}
		return renderedHTML.toString();
	}

	/*
	 * TODO comment
	 */
	protected String render() {
		try{
			final String tagData = this.renderSafe();
			return present(tagData) ? tagData : this.getName() +" is/are not visible to you.";
		}catch (final Exception e) {
			log.fatal("Error while rendering the tag: " + this.name, e);
			return this.getName() + " has caused an error.";
		}
	}
	
	protected abstract String renderSafe();

}