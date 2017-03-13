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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bibsonomy.search.es.help.HelpUtils;
import org.bibsonomy.services.help.HelpParser;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

/**
 * Helper class for the Markdown parsing.
 * 
 * @author Johannes Blum
 */
public class Parser implements HelpParser {
	/** the configuration for a processor */
	protected static final int PROCESSOR_CONFIG = Extensions.TABLES | Extensions.EXTANCHORLINKS;
	
	/** A map which maps a variable to the value it should be replaced with */
	private Map<String, String> replacements;
	private LinkRenderer linkRenderer;

	/**
	 * @param replacements the map for the replacement of the variables
	 */
	public Parser(Map<String, String> replacements) {
		super();
		this.replacements = replacements;
		this.linkRenderer = new LinkRenderer(this.replacements.get(HelpUtils.PROJECT_HOME));
	}

	/**
	 * Parses the given file and renders it as HTML
	 * 
	 * @param content the text 
	 * @return The resulting HTML
	 * @throws IOException
	 */
	@Override
	public String parseText(final String text) throws IOException {
		// Instantiate Markdown Parser
		final PegDownPlugins plugins = new PegDownPlugins.Builder().withPlugin(Plugin.class).build();
		final PegDownProcessor proc = new PegDownProcessor(PROCESSOR_CONFIG, plugins);

		// Parse and serialize content
		final RootNode ast = proc.parseMarkdown(text.toCharArray());
		final List<ToHtmlSerializerPlugin> serializePlugins = Arrays.asList((ToHtmlSerializerPlugin) (new Serializer(this.replacements)));
		
		final ToHtmlSerializer serializer = new ToHtmlSerializer(this.linkRenderer, serializePlugins);
		
		return serializer.toHtml(ast);
	}

}
