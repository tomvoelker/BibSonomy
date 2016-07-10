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
public class Parser {
	
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
