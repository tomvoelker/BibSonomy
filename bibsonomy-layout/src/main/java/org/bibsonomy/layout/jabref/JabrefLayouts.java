package org.bibsonomy.layout.jabref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import net.sf.jabref.Globals;
import net.sf.jabref.export.layout.Layout;
import net.sf.jabref.export.layout.LayoutHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayouts {

	private static final Log log = LogFactory.getLog(JabrefLayouts.class);

	private static final String message_no_custom_layout = "You don't have a custom filter installed. Please go to the settings page and install one.";

	
	/**
	 * Configured by the setter: the path where the user layout files are.
	 */
	private String userLayoutFilePath;
	/**
	 * Can be configured by the setter: the path where the default layout files are.
	 */
	private String defaultLayoutFilePath = "org/bibsonomy/layout/jabref";
	/**
	 * saves all loaded layouts (html, bibtexml, tablerefs, hash(user.username), ...)
	 */
	private HashMap<String,Layout> layouts;

	public void init() throws URISyntaxException {
		loadDefaultFilters();
	}
	
	/**
	 * Loads default filters (xxx.xxx.layout and xxx.layout) from the default layout directory into a map.
	 * 
	 * @throws URISyntaxException 
	 * @throws Exception
	 */
	private void loadDefaultFilters() throws URISyntaxException {
		// create new hashmap for filters
		layouts = new HashMap<String, Layout>();

		final Stack<File> dirs = new Stack<File>();

		final URL url = JabrefLayoutRenderer.class.getClassLoader().getResource(defaultLayoutFilePath); // URL to layout directory
		final File startdir = new File(url.toURI());

		// add first directory to stack
		if (startdir.isDirectory()) 
			dirs.push(startdir);

		while (dirs.size() > 0) {
			for (final File file : dirs.pop().listFiles()){
				if (file.isDirectory()) {	        
					dirs.push(file);
				} else {
					//check extension
					if (StringUtils.matchExtension(file.getName(), JabrefLayoutUtils.layoutFileExtension)){
						try {
							final LayoutHelper layoutHelper = new LayoutHelper(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")));
							// NOTE: now case of layouts is ignored
							layouts.put(file.getName().toLowerCase(), layoutHelper.getLayoutFromText(Globals.FORMATTER_PACKAGE));
							log.info("loaded filter " + file.getName());
						} catch (Exception e) {
							log.fatal("Error loading default filters.", e);
						}
					}//if	
				}//else
			}//for
		}//while
	}

	/** Returns the appropriate part for the specified layout.
	 *  
	 * @param layout
	 * @param part
	 * @return
	 */
	public Layout getLayout(final String layout, final LayoutPart part) {
		return layouts.get(JabrefLayoutUtils.getLayoutFileName(layout, part.toString()));
	}
	
	/** Returns the requested layout. This is for layouts which don't have item parts for specific publication types. 
	 * 
	 * @param layout
	 * @return
	 */
	public Layout getLayout(final String layout) {
		return layouts.get(JabrefLayoutUtils.getLayoutFileName(layout));
	}
	
	public Layout getLayout(final String layout, final String entryType) {
		/*
		 * try type-specific layout
		 */
		final Layout layout2 = layouts.get(JabrefLayoutUtils.getLayoutFileName(layout, entryType));
		
		if (layout2 != null) return layout2;
		/*
		 * no type-specific layout available, take default
		 */
		return getLayout(layout);
	}


	/** Removes all filters from the cache and loads the default filters.
	 * @throws URISyntaxException
	 */
	public void resetFilters() throws URISyntaxException {
		loadDefaultFilters();
	}
	

	/**
	 * Loads user filter from file into a map.
	 * 
	 * @param userName The user who requested a filter
	 * @throws Exception  
	 */
	private void loadUserLayoutFilter(final String hashedName) throws Exception {
		/*
		 * build path from first two letters of file name hash
		 */
		final String docPath = userLayoutFilePath + hashedName.substring(0, 2);

		final File file = new File(docPath + "/" + hashedName);
		if (file.exists()) {
			final LayoutHelper layoutHelper = new LayoutHelper(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")));
			synchronized(layouts) {
				layouts.put(hashedName, layoutHelper.getLayoutFromText(Globals.FORMATTER_PACKAGE));
			}
		}
	}

	public String toString() {
		return layouts.toString();
	}
	
	/** Returns the layout for the given user
	 * 
	 * @param userName
	 * @return
	 * @throws Exception if layout is not available
	 */
	public Layout getUserLayout(final String userName, final LayoutPart layoutPart) {
		final String hashedName = JabrefLayoutUtils.userLayoutHash(userName, layoutPart);
		/*
		 * check if custom filter exists
		 */
		if (!layouts.containsKey(hashedName)) {
			/*
			 * custom filter of current user is not loaded yet -> check if a filter exists at all
			 */
			try {
				loadUserLayoutFilter(hashedName);
			} catch (final Exception e) {
				log.fatal("Error loading custom filter for user " + userName, e);
				throw new IllegalArgumentException(message_no_custom_layout);    			
			}
		}
		/* *************** Printing the entries ******************/
		final Layout userLayout = layouts.get(hashedName);
		if (userLayout == null){
			/*
			 * custom filter deleted meanwhile -> exception
			 */
			log.fatal("Error loading custom filter for user " + userName);
			throw new IllegalArgumentException(message_no_custom_layout);		    		
		}
		return userLayout;
	}
	
	/**
	 * Unloads layout objects adequate to deleted custom filter.
	 * @param hashedName Hash representing the deleted document.
	 */
	public void unloadCustomFilter(final String hashedName){
		synchronized(layouts) {
			layouts.remove(hashedName);
		}
	}
	
	/** The path where the user layout files are.
	 * 
	 * @param userLayoutFilePath
	 */
	@Required
	public void setUserLayoutFilePath(String userLayoutFilePath) {
		this.userLayoutFilePath = userLayoutFilePath;
	}

	/**
	 * The path where the default layout files are. Defaults to <code>layouts</code>.
	 * Must be accessible by the classloader.
	 * 
	 * @param defaultLayoutFilePath
	 */
	public void setDefaultLayoutFilePath(String defaultLayoutFilePath) {
		this.defaultLayoutFilePath = defaultLayoutFilePath;
	}
	
}

