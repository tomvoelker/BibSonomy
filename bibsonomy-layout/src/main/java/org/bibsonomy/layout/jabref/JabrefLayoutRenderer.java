package org.bibsonomy.layout.jabref;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.KeyCollisionException;
import net.sf.jabref.export.FileActions;
import net.sf.jabref.export.layout.Layout;
import net.sf.jabref.imports.BibtexParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.AbstractLayoutRenderer;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.BibTexUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayoutRenderer extends AbstractLayoutRenderer {

	private static final Log log = LogFactory.getLog(JabrefLayoutRenderer.class);

	/**
	 * saves all loaded layouts (html, bibtexml, tablerefs, hash(user.username), ...)
	 */
	private JabrefLayouts layouts = new JabrefLayouts();


	@Override
	protected <T extends Resource> void renderInternal(final String layout, final List<Post<T>> posts, final String loginUserName, final OutputStream outputStream) throws IOException {
		
		/*
		 * convert posts into Jabref BibtexDatabase
		 */
		final BibtexDatabase database = bibtex2JabrefDB(posts);
		
		/*
		 * render the database
		 */
		outputStream.write(renderDatabase(database, loginUserName, layout).toString().getBytes("UTF-8"));
	}

	/**
	 * Initializes the bean by loading default layouts.
	 */
	public void init() {
		/* 
		 * initialize JabRef preferences. This is neccessary ... because they use global 
		 * preferences and if we don't initialize them, we get NullPointerExceptions later 
		 */
		Globals.prefs = JabRefPreferences.getInstance();

		// load default filters 
		try {
			layouts.init();
		} catch (URISyntaxException ex) {
			log.fatal("Could not load default layout filters.", ex);
		}
	}

	public Object clone()throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); 
	}



	/**
	 * This is the export method for BibTeX entries to any available format. 
	 * @param postList Entries to export.
	 * @param userName User to whom the passed entries belong 
	 * @param layout - the name of the layout If "custom", export with user specific layout filter
	 * @return output The formatted BibTeX entries as a string.
	 * @throws Exception, IOException
	 */
	private StringBuffer renderDatabase(final BibtexDatabase database, final String userName, final String layout) throws IOException {
		final StringBuffer output = new StringBuffer();  

		// Write database entries; entries will be sorted as they
		// appear on the screen, or sorted by author, depending on
		// Preferences.
		List<BibtexEntry> sorted = FileActions.getSortedEntries(database, null, false);

		if("custom".equals(layout)) {

			/* *************** Printing the header ******************/
			final Layout beginLayout = layouts.getUserLayout(userName, LayoutPart.BEGIN);
			if (beginLayout != null) {
				output.append(beginLayout.doLayout(database, "UTF-8"));
			}

			/* *************** Printing the entries ******************/ 
			final Layout itemLayout = layouts.getUserLayout(userName, LayoutPart.ITEM);
			for(BibtexEntry entry: sorted){	              
				output.append(itemLayout.doLayout(entry, database));
			}	        

			/* *************** Printing the footer ******************/
			final Layout endLayout = layouts.getUserLayout(userName, LayoutPart.END);
			if (endLayout != null) {
				output.append(endLayout.doLayout(database, "UTF-8"));
			}
		} else {
			/*
			 * must be one of the default formats like html, bibtexml, docbook, tablerefs, tablerefsabsbib, openoffice, harvard or endnote
			 */

			/* *************** Printing the header ******************/
			final Layout beginLayout = layouts.getLayout(layout, LayoutPart.BEGIN);
			if (beginLayout != null) {
				output.append(beginLayout.doLayout(database, "UTF-8"));
			}

			/* *************** Printing the entries ******************/ 

			// try to retrieve type-specific layouts and process output
			for(BibtexEntry entry: sorted) {

				// We try to get a type-specific layout for this entry
				final Layout itemLayout = layouts.getLayout(layout, entry.getType().getName().toLowerCase());
				if (itemLayout != null) {
					output.append(itemLayout.doLayout(entry, database));
				}
			}

			/* *************** Printing the footer ******************/
			final Layout endLayout = layouts.getLayout(layout, LayoutPart.END);
			if (endLayout != null) {
				output.append(endLayout.doLayout(database, "UTF-8"));
			}
		}
		return output;
	}


	/**
	 * This method converts BibSonomy BibTeX entries to JabRef entries and stores
	 * them into a JabRef specific BibtexDatabase! 
	 * @param bibtexList List of BibSonomy BibTeX objects
	 * @return BibtexDatabase
	 * @throws IOException
	 * @throws KeyCollisionException If two entries have exactly the same BibTeX key
	 */
	private <T extends Resource> BibtexDatabase bibtex2JabrefDB(List<Post<T>> bibtexList) {
		/*
		 * put all bibtex together as string
		 */
		final StringBuffer bibtexStrings = new StringBuffer();
		for (final Post<T> post : bibtexList) {
			final T resource = post.getResource();
			if (resource instanceof BibTex) {
				final BibTex bibtex = (BibTex) resource;
				bibtexStrings.append(" " + BibTexUtils.toBibtexString(bibtex)); 
			}
		}
		/*
		 * parse them!
		 */
		try {
			return BibtexParser.parse(new StringReader(bibtexStrings.toString())).getDatabase();
		} catch (IOException e) {
			log.fatal("Error parsing bibtex objects for JabRef output.", e);
		}

		return null;
	}
	
	/**
	 * Prints the loaded layouts.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return layouts.toString();
	}


	/** The path where the user layout files are.
	 * 
	 * @param userLayoutFilePath
	 */
	@Required
	public void setUserLayoutFilePath(String userLayoutFilePath) {
		layouts.setUserLayoutFilePath(userLayoutFilePath);
	}

	/**
	 * The path where the default layout files are. Defaults to <code>layouts</code>.
	 * Must be accessible by the classloader.
	 * 
	 * @param defaultLayoutFilePath
	 */
	public void setDefaultLayoutFilePath(String defaultLayoutFilePath) {
		layouts.setDefaultLayoutFilePath(defaultLayoutFilePath);
	}

}
