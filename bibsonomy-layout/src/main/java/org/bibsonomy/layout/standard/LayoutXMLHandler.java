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