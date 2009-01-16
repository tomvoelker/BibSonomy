package org.bibsonomy.layout.jabref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import net.sf.jabref.Globals;
import net.sf.jabref.export.layout.Layout;
import net.sf.jabref.export.layout.LayoutHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Holds and manages the available jabref layouts.
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
	 * One layout my consist of several files - e.g., sublayouts. These are 
	 * the possible (typical) sublayouts. They're used as part of the file 
	 * name.
	 */
	private static final String[] subLayouts = new String[] {
		"", 				/* the default layout - should almost always exist; renders one entry */
		".begin", 			/* the beginning - is added to the beginning of the rendered entries */
		".end",				/* the end - is added to the end of the rendered entries */
		".article",			/* ****************************************************** */ 
		".inbook",			/* the remaining sublayouts are for different entry types */
		".book",
		".booklet",
		".incollection",
		".conference",
		".inproceedings",
		".proceedings",
		".manual",
		".mastersthesis",
		".phdthesis",
		".techreport",
		".unpublished",
		".patent",
		".standard",
		".electronic",
		".periodical",
		".misc",
		".other"
	};

	/**
	 * Configured by the setter: the path where the user layout files are.
	 */
	private String userLayoutFilePath;
	/**
	 * Can be configured by the setter: the path where the default layout files are.
	 */
	private String defaultLayoutFilePath = "jabref";
	/**
	 * saves all loaded layouts (html, bibtexml, tablerefs, hash(user.username), ...)
	 */
	private HashMap<String,Layout> layouts;

	/** Initialize the layouts by loading them into a map.
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {
		loadDefaultLayouts();
	}

	/**
	 * Loads default filters (xxx.xxx.layout and xxx.layout) from the default layout directory into a map.
	 * 
	 * @throws IOException 
	 */
	private void loadDefaultLayouts() throws IOException {
		/*
		 * create a new hashmap to store the layouts
		 */
		layouts = new HashMap<String, Layout>();
		/*
		 * load layout definition from XML file
		 */
		final List<JabrefLayoutDefinition> jabrefLayouts = new XMLJabrefLayoutReader(new BufferedReader(new InputStreamReader(JabrefLayoutUtils.getResourceAsStream(defaultLayoutFilePath + "/" + "JabrefLayouts.xml"), "UTF-8"))).getJabrefLayoutsDefinitions();
		log.info("found " + jabrefLayouts.size() + " layout definitions");
		/*
		 * iterate over all layout definitions
		 */
		for (final JabrefLayoutDefinition jabrefLayout : jabrefLayouts) {
			log.debug("loading layout " + jabrefLayout.getName());
			final String path = defaultLayoutFilePath + "/" + getDirectory(jabrefLayout.getDirectory());
			/*
			 * iterate over all subLayouts
			 */
			for (final String subLayout: subLayouts) {
				final String fileName = jabrefLayout.getBaseFileName() + subLayout + JabrefLayoutUtils.layoutFileExtension;
				final String fileLocation = path + fileName;

				final Layout layout = loadLayout(fileLocation);
				if (layout != null) {
					layouts.put(fileName.toLowerCase(), layout);
				}
			}
		}
		log.info("loaded " + layouts.size() + " layouts");
	}

	/** Loads a layout from the given location. 
	 * 
	 * @param fileLocation - the location of the file, such that it can be found by the used class loader.
	 * @return The loaded layout, or <code>null</code> if it could not be found.
	 * @throws IOException
	 */
	private Layout loadLayout(final String fileLocation) throws IOException {
		final InputStream resourceAsStream = JabrefLayoutUtils.getResourceAsStream(fileLocation);
		if (resourceAsStream != null) {
			/*
			 * give file to layout helper
			 */
			final LayoutHelper layoutHelper = new LayoutHelper(new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8")));
			/*
			 * load layout
			 */
			try {
				return layoutHelper.getLayoutFromText(Globals.FORMATTER_PACKAGE);
			} catch (Exception e) {
				throw new IOException(e);
			}
		} 
		return null;
	}

	/** Create string for directories. If no given, the string is empty.
	 * @param directory
	 * @return
	 */
	private String getDirectory(final String directory) {
		if (directory == null) return "";
		return directory + "/";
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

	/** Returns the requested layout for the requested entryType. 
	 * If no entry type specific layout is available, the default layout is returned. 
	 * @param layout
	 * @param entryType
	 * @return
	 */
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
	 * @throws IOException
	 */
	public void resetFilters() throws IOException {
		loadDefaultLayouts();
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

