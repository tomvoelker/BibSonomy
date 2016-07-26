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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bibsonomy.services.help.HelpParser;
import org.bibsonomy.util.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

/**
 * Helper class for the Markdown parsing.
 * 
 * @author Johannes Blum
 */
public class Parser implements HelpParser {
	
	/** A map which maps a variable to the value it should be replaced with */
	private Map<String, String> replacements;

	/**
	 * @param replacements the map for the replacement of the variables
	 */
	public Parser(Map<String, String> replacements) {
		super();
		this.replacements = replacements;
	}

	/**
	 * Parses the given file and renders it as HTML
	 * 
	 * @param filename The file to parse
	 * @return The resulting HTML
	 * @throws IOException
	 */
	@Override
	public String parseFile(final String filename) throws IOException {
		// read file content
		final File file = new File(filename);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		try (final InputStream stream = new FileInputStream(file)) {
			final String content = StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(stream)));

			// Instantiate Markdown Parser
			final PegDownPlugins plugins = new PegDownPlugins.Builder().withPlugin(Plugin.class).build();
			final PegDownProcessor proc = new PegDownProcessor(0, plugins);

			// Parse and serialize content
			final RootNode ast = proc.parseMarkdown(content.toCharArray());
			final List<ToHtmlSerializerPlugin> serializePlugins = Arrays.asList((ToHtmlSerializerPlugin) (new Serializer(replacements)));
			final ToHtmlSerializer serializer = new ToHtmlSerializer(new LinkRenderer(), serializePlugins);
			
			return serializer.toHtml(ast);
		}
	}

}
