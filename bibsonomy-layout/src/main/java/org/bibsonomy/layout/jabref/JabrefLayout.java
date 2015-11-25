/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.jabref;

import java.util.List;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.export.layout.Layout;

import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.util.StringUtils;


/**
 * Represents an entry of a jabref layout definition XML file according to
 * JabrefLayoutDefinition.xsd.  
 * 
 * @author:  rja
 */
public class JabrefLayout extends AbstractJabRefLayout {
	/**
	 * If the layout files are in a subdirectory of the layout directory, the name of the directory.
	 */
	private String directory;
	
	/**
	 * The base file name, most often equal to {@link #name}.
	 */
	private String baseFileName;
	
	/**
	 * <code>true</code>, if this is a custom user layout
	 */
	private boolean userLayout;
	
	/**
	 * the default constructor
	 * @param name
	 */
	public JabrefLayout(final String name) {
		super(name);
	}
	
	/**
	 * This is the export method for BibTeX entries to any available format. 
	 * @param postList Entries to export.
	 * @param userName User to whom the passed entries belong 
	 * @param layout - the layout to be rendered. If "custom", export with user specific layout filter
	 * @param embeddedLayout - if <code>true</code> the corresponding embedded begin/end parts 
	 * (see {@link LayoutPart}) are used (only if available).
	 * @return output The formatted BibTeX entries as a string.
	 * @throws LayoutRenderingException - if a layout could not be found
	 */
	@Override
	public StringBuffer render(final BibtexDatabase database, final List<BibtexEntry> sorted, final boolean embeddedLayout) throws LayoutRenderingException {
		final StringBuffer output = new StringBuffer();

		/* 
		 * *************** rendering the header ***************** 
		 */
		Layout beginLayout = null;
		/*
		 * first: try embedded begin layout, if requested.
		 */
		if (embeddedLayout && hasEmbeddedLayout()) {
			beginLayout = getSubLayout(LayoutPart.EMBEDDEDBEGIN);
		} 
		/*
		 * second: if not available, take normal begin layout
		 */
		else {
			beginLayout = getSubLayout(LayoutPart.BEGIN);
		}
		/*
		 * third: render, if layout found
		 */
		if (beginLayout != null) {
			output.append(beginLayout.doLayout(database, StringUtils.CHARSET_UTF_8));
		}
		
		/* 
		 * *************** rendering the entries *****************
		 */ 
		if (isUserLayout()) {
			/*
			 * render custom user layout
			 */
			final Layout itemLayout = getSubLayout(LayoutPart.ITEM);
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
			for (final BibtexEntry entry : sorted) {
				// We try to get a type-specific layout for this entry
				// FIXME: adding the dot "." here isn't so nice ...
				Layout itemLayout = getSubLayout("." + entry.getType().getName().toLowerCase());
				if (itemLayout == null) {
					/*
					 * try to get a generic layout
					 */
					itemLayout = getSubLayout("");
					if (itemLayout == null) {
						/*
						 * no layout found -> throw an exception
						 */
						throw new LayoutRenderingException("layout file(s) for '" + getName() + "' could not be found");
					}
				} 
				output.append(itemLayout.doLayout(entry, database));
			}
		}

		/* 
		 * *************** rendering the footer ***************** 
		 */
		Layout endLayout = null;
		/*
		 * first: try embedded end layout, if requested.
		 */
		if (embeddedLayout && hasEmbeddedLayout()) {
			endLayout = getSubLayout(LayoutPart.EMBEDDEDEND);
		} 
		/*
		 * second: if not available, take normal begin layout
		 */
		else {
			endLayout = getSubLayout(LayoutPart.END);
		}
		/*
		 * third: render, if layout found
		 */
		if (endLayout != null) {
			output.append(endLayout.doLayout(database, StringUtils.CHARSET_UTF_8));
		}

		return output;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.layout.jabref.AbstractJabRefLayout#init()
	 */
	@Override
	public void init(final JabRefConfig config) throws Exception {
		super.init(config);
		
		this.subLayouts = JabrefLayoutUtils.loadSubLayouts(this, config);
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return this.directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return the baseFileName
	 */
	public String getBaseFileName() {
		return this.baseFileName;
	}

	/**
	 * @param baseFileName the baseFileName to set
	 */
	public void setBaseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
	}

	@Override
	public String toString() {
		return super.toString() + "/" + directory + "/" + baseFileName + "(" + subLayouts.size() + ")";
	}

	/**
	 * @return the userLayout
	 */
	public boolean isUserLayout() {
		return this.userLayout;
	}

	/**
	 * @param userLayout the userLayout to set
	 */
	public void setUserLayout(boolean userLayout) {
		this.userLayout = userLayout;
	}
}