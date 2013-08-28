package org.bibsonomy.wiki.tags;

import static org.bibsonomy.util.ValidationUtils.present;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Layout;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.bibsonomy.wiki.CVWikiModel;
import org.springframework.context.MessageSource;


/**
 * @author philipp
 * @author Bernd Terbrack
 * @version $Id$
 */
public abstract class AbstractTag extends HTMLTag implements INoBodyParsingTag  {
	private static final Log log = LogFactory.getLog(AbstractTag.class);
	protected LogicInterface logic;
	protected User requestedUser;
	protected Group requestedGroup;
	protected LayoutRenderer<Layout> layoutRenderer;
	protected MessageSource messageSource;
	protected Locale locale;
	
	/**
	 * 
	 * @param name the name of the tag
	 */
	public AbstractTag(final String name) {
		super(name);
	}

	/**
	 * @param converter unused. Only there to be able to override another method.
	 * @param buf some kind of buffer, as it seems. We will write on this buffer.
	 * @param model the wiki model used to render this tag.
	 */
	@Override
	public void renderHTML(final ITextConverter converter, final Appendable buf, final IWikiModel model) throws IOException {
		final CVWikiModel wiki = (CVWikiModel) model;
		this.logic = wiki.getLogic();
		
		this.requestedUser = wiki.getRequestedUser();
		this.requestedGroup = wiki.getRequestedGroup();
		this.layoutRenderer = wiki.getLayoutRenderer();
		this.messageSource = wiki.getMessageSource();
		this.locale = wiki.getLocale(); 
		buf.append(this.render());
	}
	
	/**
	 * Render a string by escaping XML Chars. Otherwise just return an empty string.
	 * @param toRender soem kind of string.
	 * @return the rendered string or an empty string, if toRender was empty or null.
	 */
	protected String renderString(final String toRender) {
		if (present(toRender)) {
			return Utils.escapeXmlChars(toRender);
		}
		return "";
	}
	
	/*
	 * TODO comment
	 */
	protected String render() {
		try{
			final String tagData = this.renderSafe();
			if (tagData == null)
				return this.messageSource.getMessage("cv.error.common.notVisible", new Object[]{this.getName()}, this.locale);
			else if (tagData.trim().length() == 0)
				return this.messageSource.getMessage("cv.error.common.noData", new Object[]{this.getName()}, this.locale);
			else
				return tagData;
			
			
		} catch (final Exception e) {
			log.fatal("Error while rendering the tag: " + this.name, e);
			return this.messageSource.getMessage("cv.error.common.stacktrace", new Object[]{this.getName(), e.toString(), e}, this.locale);
		}
	}
	
	protected abstract String renderSafe();

}