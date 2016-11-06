package org.bibsonomy.webapp.util.tags;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.bibsonomy.layout.jabref.JabrefLayoutRenderer;
import org.bibsonomy.model.enums.FavouriteLayoutSource;
import org.bibsonomy.model.user.settings.FavouriteLayout;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * 
 * @author dzo
 */
public class FavouriteLayoutsDisplayNameTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -1696080188182704061L;
	
	private FavouriteLayout favouriteLayout;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.tags.RequestContextAwareTag#doStartTagInternal()
	 */
	@Override
	protected int doStartTagInternal() throws Exception {
		try {
			pageContext.getOut().print(this.renderFavouriteLayout());
		} catch (final IOException ex) {
			throw new JspException("Error: IOException while writing to client" + ex.getMessage());
		}
		return SKIP_BODY;
	}

	/**
	 * @return a correct display name for the given style
	 */
	private String renderFavouriteLayout() {
		final FavouriteLayoutSource source = this.favouriteLayout.getSource();
		final String name = this.favouriteLayout.getDisplayName();
		try {
			return this.getMessageSource().getMessage("bibtex.citation_format." + name, null, this.getLocale());
		} catch (final NoSuchMessageException e) {
			// ignore
		}
		
		switch (source) {
		case SIMPLE:
			return name;
		case JABREF:
			return getJabRefDisplayName(name);
		case CSL:
			return getCslDisplayName(name);
		default:
			break;
		}
		return null;
	}

	/**
	 * @param displayName
	 * @return
	 */
	private String getCslDisplayName(final String displayName) {
		final CSLStyle style = this.getCslFileManager().getStyleByName(displayName);
		if (style == null){
			return "Style has been deleted."; // TODO: i18n
		}
		return style.getDisplayName();
	}

	/**
	 * @param displayName
	 * @return
	 */
	private String getJabRefDisplayName(final String displayName) {
		return getJabRefLayoutRenderer().getLayouts().get(displayName).getDisplayName();
	}
	
	private CSLFilesManager getCslFileManager() {
		final WebApplicationContext webCtx = this.getRequestContext().getWebApplicationContext();
		return webCtx.getBean(CSLFilesManager.class);
	}
	
	private LayoutRenderer<AbstractJabRefLayout> getJabRefLayoutRenderer() {
		final WebApplicationContext ctx = this.getRequestContext().getWebApplicationContext();
		return ctx.getBean(JabrefLayoutRenderer.class);
	}
	
	private MessageSource getMessageSource() {
		return getRequestContext().getMessageSource();
	}
	
	/**
	 * Use the current RequestContext's application context as MessageSource.
	 */
	private Locale getLocale() {
		return getRequestContext().getLocale();
	}

	/**
	 * @param favouriteLayout the favouriteLayout to set
	 */
	public void setFavouriteLayout(FavouriteLayout favouriteLayout) {
		this.favouriteLayout = favouriteLayout;
	}

}
