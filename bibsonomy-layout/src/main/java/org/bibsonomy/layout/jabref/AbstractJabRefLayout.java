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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.export.layout.Layout;

import org.bibsonomy.common.enums.LayoutPart;
import org.bibsonomy.common.exceptions.LayoutRenderingException;

/**
 * abstract jabref layout
 *
 * @author dzo
 */
public abstract class AbstractJabRefLayout extends org.bibsonomy.model.Layout {

	/** The associated layouts filters. */
	protected Map<String, Layout> subLayouts = new HashMap<>();

	/**
	 * @param name
	 */
	public AbstractJabRefLayout(String name) {
		super(name);
	}
	
	/**
	 * @param subLayoutName
	 * @return the sublayouts of the layout
	 */
	public Layout getSubLayout(final String subLayoutName) {
		return subLayouts.get(subLayoutName);
	}
	
	/**
	 * @param subLayoutName the sublayout name to add
	 * @param layout the layout to add
	 */
	public void addSubLayout(final String subLayoutName, final Layout layout) {
		subLayouts.put(subLayoutName, layout);
	}

	public Layout getSubLayout(final LayoutPart layoutPart) {
		return getSubLayout("." + layoutPart);
	}

	public void addSubLayout(final LayoutPart layoutPart, final Layout layout) {
		addSubLayout("." + layoutPart, layout);
	}

	@Override
	public boolean hasEmbeddedLayout() {
		return (this.getSubLayout(LayoutPart.EMBEDDEDBEGIN) != null) && (this.getSubLayout(LayoutPart.EMBEDDEDEND) != null);
	}
	
	/**
	 * rendering a database
	 * @param database
	 * @param sorted
	 * @param embeddedLayout
	 * @return
	 * @throws LayoutRenderingException
	 */
	public abstract StringBuffer render(final BibtexDatabase database, final List<BibtexEntry> sorted, final boolean embeddedLayout) throws LayoutRenderingException;

	/**
	 * inits the layout
	 * @param config
	 * @throws Exception 
	 */
	public void init(JabRefConfig config) throws Exception {
		// noop
	}
}
