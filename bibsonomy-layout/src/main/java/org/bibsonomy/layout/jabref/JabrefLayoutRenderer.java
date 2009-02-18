package org.bibsonomy.layout.jabref;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.GlobalsSuper;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.KeyCollisionException;
import net.sf.jabref.export.FileActions;
import net.sf.jabref.export.layout.Layout;
import net.sf.jabref.imports.BibtexParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.LayoutRenderer;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.BibTexUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * This renderer handles jabref layouts. 
 *  
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayoutRenderer implements LayoutRenderer<JabrefLayout> {

	private static final Log log = LogFactory.getLog(JabrefLayoutRenderer.class);

	/**
	 * saves all loaded layouts (html, bibtexml, tablerefs, hash(user.username), ...)
	 */
	private JabrefLayouts layouts = new JabrefLayouts();


	/** Returns the requested layout.
	 *  
	 * @see org.bibsonomy.layout.LayoutRenderer#getLayout(java.lang.String, java.lang.String)
	 */
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
			throw new LayoutRenderingException("Could not find layout '" + layout + "' for user " + loginUserName);
		}
		return jabrefLayout;

	}

	/** Renders the posts with the given layout.
	 * 
	 * @see org.bibsonomy.layout.LayoutRenderer#renderLayout(org.bibsonomy.layout.Layout, java.util.List, java.io.OutputStream)
	 */
	public <T extends Resource> void renderLayout(final JabrefLayout layout, final List<Post<T>> posts, final OutputStream outputStream) throws LayoutRenderingException, IOException {
		log.debug("rendering " + posts.size() + " posts with " + layout.getName() + " layout");
		/*
		 * XXX: different handling of "duplicates = no" in new code:
		 * old code: duplicate removal and sorting by year, only for layouts, done in layout 
		 * renderer 
		 * new code: duplicate removal in controller, no sorting by year - must be enforced 
		 * by another parameter
		 */
		/*
		 * convert posts into Jabref BibtexDatabase ... in a horribly inefficient way
		 * FIXME: well ... in the future we will use wrapper objects instead ...
		 */
		final BibtexDatabase database = bibtex2JabrefDB(posts);
		/*
		 * render the database
		 */
		final StringBuffer renderDatabase = renderDatabase(database, layout);
		try {
			outputStream.write(renderDatabase.toString().getBytes("UTF-8"));
		} catch (final IOException e) {
			throw new LayoutRenderingException("Could not render layout: " + e.getMessage());
		}
	}



	/**
	 * This is a singleton! 
	 * FIXME: is this really neccessary? At least until the old code from LayoutHandler
	 * is moved we need an instance if the JabrefLayoutRenderer there to unload custom
	 * user layouts. The easiest way to do this is via a singleton.
	 * 
	 */
	private static JabrefLayoutRenderer instance = new JabrefLayoutRenderer();

	public static JabrefLayoutRenderer getInstance() {
		return instance;
	}

	private JabrefLayoutRenderer() {
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
		} catch (IOException e) {
			log.fatal("Could not load default layout filters.", e);
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
	 * @throws LayoutRenderingException - if a layout could not be found
	 */
	private StringBuffer renderDatabase(final BibtexDatabase database, final JabrefLayout layout) throws LayoutRenderingException {
		final StringBuffer output = new StringBuffer();  

		/* 
		 * *************** rendering the header ***************** 
		 */
		final Layout beginLayout = layout.getSubLayout(LayoutPart.BEGIN);
		if (beginLayout != null) {
			output.append(beginLayout.doLayout(database, "UTF-8"));
		}

		/*
		 * sorting database entries
		 * 
		 * Write database entries; entries will be sorted as they
		 * appear on the screen, or sorted by author, depending on
		 * Preferences.
		 */
		final List<BibtexEntry> sorted = FileActions.getSortedEntries(database, null, false);


		/* 
		 * *************** rendering the entries *****************
		 */ 
		if (layout.isUserLayout()) {
			/*
			 * render custom user layout
			 */
			final Layout itemLayout = layout.getSubLayout(LayoutPart.ITEM);
			if (itemLayout == null) {
				/*
				 * no layout for user found -> throw an exception
				 */
				throw new LayoutRenderingException("no custom layout found");
			} 
			for (final BibtexEntry entry: sorted) {	              
				output.append(itemLayout.doLayout(entry, database));
			}	        

		} else {
			// try to retrieve type-specific layouts and process output
			for (final BibtexEntry entry: sorted) {

				// We try to get a type-specific layout for this entry
				// FIXME: adding the dot "." here isn't so nice ...
				Layout itemLayout = layout.getSubLayout("." + entry.getType().getName().toLowerCase());
				if (itemLayout == null) {
					/*
					 * try to get a generic layout
					 */
					itemLayout = layout.getSubLayout("");
					if (itemLayout == null) {
						/*
						 * no layout found -> throw an exception
						 */
						throw new LayoutRenderingException("no layout '" + layout + "' found");
					}
				} 
				output.append(itemLayout.doLayout(entry, database));
			}


		}




		/* 
		 * *************** rendering the footer ***************** 
		 */
		final Layout endLayout = layout.getSubLayout(LayoutPart.END);
		if (endLayout != null) {
			output.append(endLayout.doLayout(database, "UTF-8"));
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
			log.fatal("Error parsing BibTeX objects for JabRef output.", e);
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

	/**
	 * This renderer only supports {@link BibTex}.
	 * 
	 * @see org.bibsonomy.layout.LayoutRenderer#supportsResourceType(java.lang.Class)
	 */
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

}
