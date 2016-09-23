/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.markdown;

import java.util.List;
import java.util.Map;

import org.pegdown.LinkRenderer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.SuperNode;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

/**
 * TODO: add documentation to this class
 *
 * @author nosebrain
 */
public class ToHtmlSerializer extends org.pegdown.ToHtmlSerializer {

	/**
	 * @param linkRenderer
	 * @param plugins
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer, List<ToHtmlSerializerPlugin> plugins) {
		super(linkRenderer, plugins);
	}

	/**
	 * @param linkRenderer
	 * @param verbatimSerializers
	 * @param plugins
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers, List<ToHtmlSerializerPlugin> plugins) {
		super(linkRenderer, verbatimSerializers, plugins);
	}

	/**
	 * @param linkRenderer
	 * @param verbatimSerializers
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers) {
		super(linkRenderer, verbatimSerializers);
	}

	/**
	 * @param linkRenderer
	 */
	public ToHtmlSerializer(LinkRenderer linkRenderer) {
		super(linkRenderer);
	}

	/* (non-Javadoc)
	 * @see org.pegdown.ToHtmlSerializer#printIndentedTag(org.pegdown.ast.SuperNode, java.lang.String)
	 */
	@Override
	protected void printIndentedTag(SuperNode node, String tag) {
		// XXX: not so nice, but we want to add a class attribute to the table tag
		if ("table".equals(tag)) {
			printer.println().print("<table class=\"table\">").indent(+2);
			visitChildren(node);
			printer.indent(-2).println().print('<').print('/').print(tag).print('>');
		} else {
			super.printIndentedTag(node, tag);
		}
	}
}
