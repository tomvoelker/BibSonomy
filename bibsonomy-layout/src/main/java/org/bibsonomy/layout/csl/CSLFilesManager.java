package org.bibsonomy.layout.csl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * class for manaaging all csl files
 * 
 * @author jp
 */
public class CSLFilesManager {
	private static final Log log = LogFactory.getLog(CSLFilesManager.class);

	// mapping from id to CSLStyle which contains the id itself, a display name and the content of the file
	private Map<String, CSLStyle> cslFiles = new HashMap<String, CSLStyle>();

	/**
	 * init this manager
	 * reads all csl files from {@link #CSL_FOLDER}
	 */
	public void init() {
		final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(CSLFilesManager.class.getClassLoader());
		
		try {
			final Resource[] resources = resolver.getResources("classpath:/org/citationstyles/styles/*.csl");
			for (final Resource resource : resources) {
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
					final StringBuilder builder = new StringBuilder();
					while (reader.ready()) {
						builder.append(reader.readLine() + "\n");
					}
					
					try {
						final String cslStyleSource = builder.toString().trim();
						final String fileName = resource.getFilename();
						this.cslFiles.put(fileName.toLowerCase().replace(".csl", ""), new CSLStyle(fileName, extractTitle(cslStyleSource), cslStyleSource));
					} catch (final ParserConfigurationException | SAXException | IOException e) {
						log.error("error reading file " + resource.getFilename(), e);
					}
					
				}
			}
		} catch (final IOException e) {
			log.error("error while loading csl files", e);
		}
	}

	/**
	 * @param cslStyleSource
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private static String extractTitle(final String cslStyleSource) throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		final Document document = documentBuilder.parse(new InputSource(new StringReader(cslStyleSource)));
		
		String title = document.getElementsByTagName("title").item(0).getTextContent();
		title = title.replaceAll("\"", "'"); // TODO: @jpf: why do we replace every " with a '?
		return title.trim();
	}
	
	/**
	 * @param cslName
	 * @return the csl style
	 */
	public CSLStyle getStyleByName(final String cslName) {
		return this.cslFiles.get(cslName);
	}

	/**
	 * @return the cslFiles
	 */
	public Map<String, CSLStyle> getCslFiles() {
		return Collections.unmodifiableMap(this.cslFiles);
	}
}
