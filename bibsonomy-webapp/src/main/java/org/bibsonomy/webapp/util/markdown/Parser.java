package org.bibsonomy.webapp.util.markdown;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bibsonomy.util.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

/**
 * Helper class for the Markdown parsing.
 */
public class Parser {
	
	/** A HashMap which maps a variable to the value it should be replaced with */
	HashMap<String, String> replacements;

	/**
	 * @param replacements the HashMap for the replacement of the variables
	 */
	public Parser(HashMap<String, String> replacements) {
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
	public String parseFile(String filename) throws IOException {
		
		// Read file content
		final InputStream stream = Parser.class.getClassLoader().getResourceAsStream(filename);
		if (stream == null)
			throw new FileNotFoundException(); 
		String content = StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(stream)));

		// Instantiate Markdown Parser
		PegDownPlugins plugins = new PegDownPlugins.Builder().withPlugin(Plugin.class).build();
		PegDownProcessor proc = new PegDownProcessor(0, plugins);

		// Parse and serialize content
		RootNode ast = proc.parseMarkdown(content.toCharArray());
		List<ToHtmlSerializerPlugin> serializePlugins = Arrays.asList((ToHtmlSerializerPlugin) (new Serializer(replacements)));
		String output = "test";
		output = new ToHtmlSerializer(new LinkRenderer(), serializePlugins).toHtml(ast);

		return output;
	}

}
