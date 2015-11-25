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

import java.lang.reflect.InvocationTargetException;

import org.bibsonomy.layout.common.AbstractXMLHandler;
import org.xml.sax.Attributes;

/**
 * Callback handler for the SAX parser.
 * 
 * @author:  rja
 */
public class JabrefLayoutXMLHandler extends AbstractXMLHandler<AbstractJabRefLayout> {
	private static final String SELF_RENDERING_LAYOUT_ELEMENT_TAG = "selfrenderingLayout";

	
	/* (non-Javadoc)
	 * @see org.bibsonomy.layout.common.AbstractXMLHandler#isLayoutElement(java.lang.String)
	 */
	@Override
	protected boolean isLayoutElement(String name) {
		return SELF_RENDERING_LAYOUT_ELEMENT_TAG.equals(name) || "layout".equals(name);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.layout.common.AbstractXMLHandler#initLayout(org.xml.sax.Attributes)
	 */
	@Override
	protected AbstractJabRefLayout initLayout(String name, Attributes attrs) {
		if (SELF_RENDERING_LAYOUT_ELEMENT_TAG.equals(name)) {
			try {
				return (AbstractJabRefLayout) Class.forName(attrs.getValue("class")).getDeclaredConstructor(String.class).newInstance(attrs.getValue("name"));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				throw new RuntimeException("error initializing self rendering JabRef layout", e);
			}
		}
		return new JabrefLayout(attrs.getValue("name"));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.layout.common.AbstractXMLHandler#endElement(java.lang.String, java.lang.String, java.lang.String, org.bibsonomy.model.Layout)
	 */
	@Override
	protected void endElement(String uri, String name, String qName, AbstractJabRefLayout currentLayout) {
		super.endElement(uri, name, qName, currentLayout);
		if ("baseFileName".equals(name)) {
			((JabrefLayout) currentLayout).setBaseFileName(getBuf());
		} else if ("directory".equals(name)) {
			((JabrefLayout) currentLayout).setDirectory(getBuf());
		}
	}
}