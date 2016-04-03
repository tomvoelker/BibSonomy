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
package org.bibsonomy.layout.standard;

import org.bibsonomy.layout.common.AbstractXMLHandler;
import org.xml.sax.Attributes;

/**
 * Callback handler for the SAX parser.
 * 
 * @author:  lsc
 */
public class LayoutXMLHandler extends AbstractXMLHandler<StandardLayout> {
	
	@Override
	protected boolean isLayoutElement(String name) {
		return "layout".equals(name);
	}
	
	@Override
	protected StandardLayout initLayout(String name, Attributes attrs) {
		return new StandardLayout(attrs.getValue("name"));
	}
}