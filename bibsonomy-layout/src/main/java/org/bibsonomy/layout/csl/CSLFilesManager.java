package org.bibsonomy.layout.csl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

import net.sf.json.JSONObject;

/**
 * class for managing all csl files
 * 
 * @author jp
 */
public class CSLFilesManager {
	private static final Log log = LogFactory.getLog(CSLFilesManager.class);

	// mapping from id to CSLStyle which contains the id itself, a display name and the content of the file
	private Map<String, CSLStyle> cslFiles = new HashMap<String, CSLStyle>();
	
	private Map<String, CSLStyle> cslFilesIncludingAliases = new HashMap<String, CSLStyle>();
	private Map<String, HashSet<String>> aliases = new HashMap<String, HashSet<String>>();
	
	/**
	 * init this manager
	 * reads all csl files from classpath: /org/citationstyles/styles/
	 */
	public void init() {
		final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(CSLFilesManager.class.getClassLoader());

		try {
			final Resource[] aliasesResource = resolver.getResources("classpath:/org/citationstyles/styles/renamed-styles.json");
			final BufferedReader jsonReader = new BufferedReader(new InputStreamReader(aliasesResource[0].getInputStream()));
			final StringBuilder jsonBuilder = new StringBuilder();
			while (jsonReader.ready()) {
				jsonBuilder.append(jsonReader.readLine());
			}
			//"inverting" hashmap
			JSONObject aliasesObj = JSONObject.fromObject(jsonBuilder.toString());
			for(Object keyObj : aliasesObj.keySet()){
				String key = (String) keyObj;
				String value = (String) aliasesObj.get(key);
				
				if(!aliases.containsKey(value)){
					aliases.put(value, new HashSet<String>());
				}
				aliases.get(value).add(key);
			}
			final Resource[] resources = resolver.getResources("classpath:/org/citationstyles/styles/*.csl");
//			final Resource[] resourcesDependent = resolver.getResources("classpath:/org/citationstyles/styles/dependent/*.csl");
//			LinkedList<Resource> resources = new LinkedList<>();
//			resources.addAll(Arrays.asList(resourcesTopLevel));
//			resources.addAll(Arrays.asList(resourcesDependent));
			
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
						if(aliases.containsKey(fileName.toLowerCase().replace(".csl", ""))){
							for(String alias : aliases.get(fileName.toLowerCase().replace(".csl", ""))){
								this.cslFilesIncludingAliases.put(alias, new CSLStyle(fileName, extractTitle(cslStyleSource), cslStyleSource));
							}
						}
					} catch (final ParserConfigurationException | SAXException | IOException e) {
						log.error("error reading file " + resource.getFilename(), e);
					}
					
				}
			}
			cslFilesIncludingAliases.putAll(cslFiles);
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
		return title.trim();
	}
	
	/**
	 * @param cslName
	 * @return the csl style
	 */
	public CSLStyle getStyleByName(final String cslName) {
		if(!cslFiles.containsKey(cslName)){
			return cslFilesIncludingAliases.get(cslName);
		}
		return this.cslFiles.get(cslName);
	}

	/**
	 * @return the cslFiles
	 */
	public Map<String, CSLStyle> getCslFiles() {
		return Collections.unmodifiableMap(this.cslFiles);
	}
	/**
	 * ATTENTION
	 * should only be used if {@link #getCslFiles()} does NOT contain the correct key.
	 * @return all cslFiles INCLUDING the ones which have been renamed or moved.
	 * Can and will contain duplicates!! {@link #getCslFiles()} will not.
	 */
	public Map<String, CSLStyle> getCslFilesIncludingAliases() {
		return Collections.unmodifiableMap(this.cslFilesIncludingAliases);
	}
}
