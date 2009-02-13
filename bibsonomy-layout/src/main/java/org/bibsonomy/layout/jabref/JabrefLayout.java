package org.bibsonomy.layout.jabref;

import java.util.HashMap;
import java.util.Map;

import net.sf.jabref.export.layout.Layout;


/**
 * Represents an entry of a jabref layout definition XML file according to
 * JabrefLayoutDefinition.xsd.  
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayout extends org.bibsonomy.layout.Layout {

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
	 * The associated layouts filters. 
	 */
	private Map<String, Layout> subLayouts = new HashMap<String, Layout>();
	
	public JabrefLayout(final String name) {
		super(name);
	}

	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getBaseFileName() {
		return baseFileName;
	}
	public void setBaseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
	}

	@Override
	public String toString() {
		return super.toString() + "/" + directory + "/" + baseFileName + "(" + subLayouts.size() + ")";
	}

	public Layout getSubLayout(final String subLayoutName) {
		return subLayouts.get(subLayoutName);
	}

	public void addSubLayout(final String subLayoutName, final Layout layout) {
		subLayouts.put(subLayoutName, layout);
	}
	
	public Layout getSubLayout(final LayoutPart layoutPart) {
		return getSubLayout("." + layoutPart);
	}
	
	public void addSubLayout(final LayoutPart layoutPart, final Layout layout) {
		addSubLayout("." + layoutPart, layout);
	}

	public boolean isUserLayout() {
		return userLayout;
	}

	public void setUserLayout(boolean userLayout) {
		this.userLayout = userLayout;
	}

}

