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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.GlobalsSuper;
import net.sf.jabref.JabRefPreferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.util.JabRefModelConverter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.renderer.LayoutRenderer;

/**
 * This renderer handles jabref layouts. 
 * 
 * @author:  rja
 */
public class JabrefLayoutRenderer implements LayoutRenderer<AbstractJabRefLayout> {
	private static final Log log = LogFactory.getLog(JabrefLayoutRenderer.class);
	
	private static Properties properties;
	
	private JabRefConfig config;
	
	private URLGenerator urlGenerator;
	
	/** saves all loaded layouts (html, bibtexml, tablerefs, hash(user.username), ...) */
	private Map<String, AbstractJabRefLayout> layouts;
	
	/**
	 * constructs a new jabref layout renderer
	 * @param config 
	 * @throws Exception 
	 */
	public JabrefLayoutRenderer(final JabRefConfig config) throws Exception {
		this.config = config;
		this.init();
	}

	/**
	 * Initializes the bean by loading default layouts.
	 * @throws Exception 
	 */
	private void init() throws Exception {
		/* 
		 * initialize JabRef preferences. This is neccessary ... because they use global 
		 * preferences and if we don't initialize them, we get NullPointerExceptions later 
		 */
		GlobalsSuper.prefs = JabRefPreferences.getInstance();

		// load default filters 
		this.loadDefaultLayouts();
	}
	
	/**
	 * Loads default filters (xxx.xxx.layout and xxx.layout) from the default layout directory into a map.
	 * 
	 * @throws IOException 
	 */
	private void loadDefaultLayouts() throws Exception {
		/*
		 * create a new hashmap to store the layouts
		 */
		layouts = new LinkedHashMap<String, AbstractJabRefLayout>();
		/*
		 * load layout definition from XML file
		 */
		final List<AbstractJabRefLayout> jabrefLayouts = new XMLJabrefLayoutReader(new BufferedReader(new InputStreamReader(JabrefLayoutUtils.getResourceAsStream(this.config.getDefaultLayoutFilePath() + "/" + "JabrefLayouts.xml"), "UTF-8"))).getJabrefLayoutsDefinitions();
		log.info("found " + jabrefLayouts.size() + " layout definitions");
		/*
		 * iterate over all layout definitions
		 */
		for (final AbstractJabRefLayout jabrefLayout : jabrefLayouts) {
			final String layoutId = jabrefLayout.getName();
			log.debug("loading layout " + layoutId);
			jabrefLayout.init(this.config);
			if (this.layouts.containsKey(layoutId)) {
				throw new IllegalStateException("layout '" + layoutId + "' already exists.");
			}
			this.layouts.put(layoutId, jabrefLayout);
		}
		log.info("loaded " + layouts.size() + " layouts");
	}

	/** Returns the requested layout.
	 *  
	 * @see org.bibsonomy.services.renderer.LayoutRenderer#getLayout(java.lang.String, java.lang.String)
	 */
	@Override
	public AbstractJabRefLayout getLayout(final String layout, final String loginUserName) throws LayoutRenderingException, IOException {
		final AbstractJabRefLayout jabrefLayout;
		if ("custom".equals(layout)) {
			/*
			 * get custom user layout from map
			 */
			jabrefLayout = this.getUserLayout(loginUserName);
		} else {
			/*
			 * get standard layout
			 */
			jabrefLayout = this.layouts.get(layout);
		}
		/*
		 * no layout found -> LayoutRenderingException
		 */
		if (jabrefLayout == null) {
			throw new LayoutRenderingException("Could not find layout '" + layout + "' for user '" + loginUserName + "'");
		}
		return jabrefLayout;
	}

	/**
	 * renders the posts with the given layout.
	 * 
	 * @see org.bibsonomy.services.renderer.LayoutRenderer#renderLayout(org.bibsonomy.model.Layout, java.util.List, boolean)
	 */
	@Override
	public StringBuffer renderLayout(final AbstractJabRefLayout layout, final  List<? extends Post<? extends Resource>> posts, final boolean embeddedLayout) throws LayoutRenderingException, IOException {
		log.debug("rendering " + posts.size() + " posts with " + layout.getName() + " layout");
		/*
		 * XXX: different handling of "duplicates = no" in new code:
		 * old code: duplicate removal and sorting by year, only for layouts, done in layout 
		 * renderer 
		 * new code: duplicate removal in controller, no sorting by year - must be enforced 
		 * by another parameter
		 */
		final BibtexDatabase database = JabRefModelConverter.bibtex2JabrefDB(posts, urlGenerator, false);
		/*
		 * render the database
		 */
		return layout.render(database, JabRefModelConverter.convertPosts(posts, urlGenerator, false), embeddedLayout);
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

	/**
	 * This renderer only supports {@link BibTex}.
	 * 
	 * @see org.bibsonomy.services.renderer.LayoutRenderer#supportsResourceType(java.lang.Class)
	 */
	@Override
	public boolean supportsResourceType(final Class<? extends Resource> clazz) {
		return BibTex.class.equals(clazz);
	}
	
	/**
	 * @param userName
	 * @return the layout for the given user. If no layout could be found, <code>null</code>
	 * is returned instead of throwing an exception. This allows for missing parts (i.e., 
	 * no begin.layout).
	 */
	protected AbstractJabRefLayout getUserLayout(final String userName) {
		/*
		 * check if custom filter exists
		 */
		final String userLayoutName = JabrefLayoutUtils.userLayoutName(userName);
		if (present(userName) && !layouts.containsKey(userLayoutName)) {
			/*
			 * custom filter of current user is not loaded yet -> check if a filter exists at all
			 */
			try {
				JabrefLayout layout = JabrefLayoutUtils.loadUserLayout(userName, this.config);
				
				/*
				 * we add the layout only to the map, if it is complete, i.e., it contains an item layout
				 */
				if (layout.getSubLayout(LayoutPart.ITEM) != null) {
					/*
					 * add user layout to map
					 */
					log.debug("user layout contains 'item' part - loading it");
					synchronized(layouts) {
						layouts.put(layout.getName(), layout);
					}
				}
			} catch (final Exception e) {
				log.info("Error loading custom filter for user " + userName, e);
			}
		}
		
		return layouts.get(userLayoutName);
	}

	/** Unloads the custom layout of the user. Note that all parts of the 
	 * layout are unloaded!
	 * 
	 * @param userName
	 */
	public void unloadUserLayout(final String userName) {
		synchronized(layouts) {
			layouts.remove(JabrefLayoutUtils.userLayoutName(userName));
		}
	}

	/**
	 * Use this method to get all layouts
	 * 
	 * @return all layouts
	 */
	@Override
	public Map<String, AbstractJabRefLayout> getLayouts(){
		return this.layouts;
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