/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		case CUSTOM:
			if (favouriteLayout.getStyle().toLowerCase().endsWith(".csl")){
				return getCslDisplayNameByStyle(favouriteLayout.getStyle());
			}
			//$FALL-THROUGH$
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
	private String getCslDisplayNameByStyle(final String style) {
		final CSLStyle CSLstyle = this.getCslFileManager().getStyleByName(style);
		if (CSLstyle == null){
			return "Style has been deleted."; // TODO: i18n
		}
		return CSLstyle.getDisplayName();
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
