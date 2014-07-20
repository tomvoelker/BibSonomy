/**
 *
 *  BibSonomy-Layout - Layout engine for the webapp.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.layout.jabref;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.GlobalsSuper;
import net.sf.jabref.JabRefPreferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.util.JabRefModelConverter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.renderer.LayoutRenderer;
import org.springframework.beans.factory.annotation.Required;

/**
 * This renderer handles jabref layouts. 
 *  
 * @author:  rja
 * 
 */
public class JabrefLayoutRenderer implements LayoutRenderer<JabrefLayout> {
	private static final Log log = LogFactory.getLog(JabrefLayoutRenderer.class);
	
	private static Properties properties;
	
	private URLGenerator urlGenerator;

	/**
	 * saves all loaded layouts (html, bibtexml, tablerefs, hash(user.username), ...)
	 */
	private final JabrefLayouts layouts = new JabrefLayouts();

	/**
	 * constructs a new jabref layout renderer
	 */
	public JabrefLayoutRenderer() {
		this.init();
	}

	/**
	 * Initializes the bean by loading default layouts.
	 */
	private void init() {
		/* 
		 * initialize JabRef preferences. This is neccessary ... because they use global 
		 * preferences and if we don't initialize them, we get NullPointerExceptions later 
		 */
		GlobalsSuper.prefs = JabRefPreferences.getInstance();

		// load default filters 
		try {
			layouts.init();
		} catch (final IOException e) {
			log.fatal("Could not load default layout filters.", e);
		}
	}

	/** Returns the requested layout.
	 *  
	 * @see org.bibsonomy.services.renderer.LayoutRenderer#getLayout(java.lang.String, java.lang.String)
	 */
	@Override
	public JabrefLayout getLayout(final String layout, final String loginUserName) throws LayoutRenderingException, IOException {
		final JabrefLayout jabrefLayout;
		if ("custom".equals(layout)) {
			/*
			 * get custom user layout from map
			 */
			jabrefLayout = layouts.getUserLayout(loginUserName);
		} else {
			/*
			 * get standard layout
			 */
			jabrefLayout = layouts.getLayout(layout);
		}
		/*
		 * no layout found -> LayoutRenderingException
		 */
		if (jabrefLayout == null) {
			throw new LayoutRenderingException("Could not find layout '" + layout + "' for user '" + loginUserName + "'");
		}
		return jabrefLayout;

	}

	/** Renders the posts with the given layout.
	 * 
	 * @see org.bibsonomy.services.renderer.LayoutRenderer#renderLayout(org.bibsonomy.model.Layout, java.util.List, boolean)
	 */
	@Override
	public StringBuffer renderLayout(final JabrefLayout layout, final  List<? extends Post<? extends Resource>> posts, final boolean embeddedLayout) throws LayoutRenderingException, IOException {
		log.debug("rendering " + posts.size() + " posts with " + layout.getName() + " layout");
		/*
		 * XXX: different handling of "duplicates = no" in new code:
		 * old code: duplicate removal and sorting by year, only for layouts, done in layout 
		 * renderer 
		 * new code: duplicate removal in controller, no sorting by year - must be enforced 
		 * by another parameter
		 */
		final BibtexDatabase database = JabRefModelConverter.bibtex2JabrefDB(posts,urlGenerator,false);
		/*
		 * render the database
		 */
		return layout.render(database, JabRefModelConverter.convertPosts(posts, urlGenerator,false), embeddedLayout);
	}

	/**
	 * Prints the loaded layouts.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return layouts.toString();
	}

	/**
	 * @param urlGen the urlGen to set
	 */
	public void setUrlGenerator(final URLGenerator urlGen) {
		this.urlGenerator = urlGen;
	}

	/** The path where the user layout files are.
	 * 
	 * @param userLayoutFilePath
	 */
	@Required
	public void setUserLayoutFilePath(final String userLayoutFilePath) {
		layouts.setUserLayoutFilePath(userLayoutFilePath);
	}

	/**
	 * The path where the default layout files are. Defaults to <code>layouts</code>.
	 * Must be accessible by the classloader.
	 * 
	 * @param defaultLayoutFilePath
	 */
	public void setDefaultLayoutFilePath(final String defaultLayoutFilePath) {
		layouts.setDefaultLayoutFilePath(defaultLayoutFilePath);
	}

	/**
	 * This renderer only supports {@link BibTex}.
	 * 
	 * @see org.bibsonomy.services.renderer.LayoutRenderer#supportsResourceType(java.lang.Class)
	 */
	@Override
	public boolean supportsResourceType(final Class<? extends Resource> clazz) {
		return BibTex.class.equals(clazz);
	}


	/** Unloads the custom layout of the user. Note that all parts of the 
	 * layout are unloaded!
	 * 
	 * @param userName
	 */
	public void unloadUserLayout(final String userName) {
		layouts.unloadCustomFilter(userName);
	}

	/**
	 * Use this method to get all layouts
	 * 
	 * @return all layouts
	 */
	@Override
	public Map<String, JabrefLayout> getLayouts(){
		return this.layouts.getLayoutMap();
	}
	
	/**
	 * Spring-Managed
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		JabrefLayoutRenderer.properties = properties;
	}
	
	/**
	 * Returns Spring-Managed Properties
	 * @return java.util.Properties
	 */
	public static Properties getProperties() {
		return properties;
	}

}
