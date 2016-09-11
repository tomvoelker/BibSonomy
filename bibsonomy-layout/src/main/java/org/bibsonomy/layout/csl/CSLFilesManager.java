package org.bibsonomy.layout.csl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * class for manaaging all csl files
 * 
 * @author jp
 */
public class CSLFilesManager {
	private static final Log log = LogFactory.getLog(CSLFilesManager.class);
	
	private static final FilenameFilter CSLFilter = new FilenameFilter() {
		/**
		 * @return <code>true</code> iff file extension equals csl
		 */
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".csl");
		}
	};
	
	private static final String directory = "org/bibsonomy/layout/csl/";
	private static final String cslFolderDirec = CSLFilesManager.class.getClassLoader().getResource(directory).getPath();
	private static final File CSL_FOLDER = new File(cslFolderDirec);

	// mapping from id to CSLStyle which contains the id itself, a display name and the content of the file
	private Map<String, CSLStyle> cslFiles = new HashMap<String, CSLStyle>();

	/**
	 * init this manager
	 * reads all csl files from {@link #CSL_FOLDER}
	 */
	public void init() {
		for (final File cslFile : CSL_FOLDER.listFiles(CSLFilter)) {
			try {
				final String fileName = cslFile.getName();
				this.cslFiles.put(fileName.toLowerCase().replace(".csl", ""), new CSLStyle(fileName, nameToTitle(cslFile), nameToXML(cslFile)));
			} catch (final ParserConfigurationException | SAXException | IOException e) {
				log.error("error reading file " + cslFile.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * @param cslFile
	 * @return content of .csl file with given name
	 * @throws IOException
	 */
	private static String nameToXML(final File cslFile) throws IOException {
		if (cslFile == null) {
			throw new IllegalArgumentException("CSL File is null");
		}
		
		if (!cslFile.exists()) {
			throw new FileNotFoundException("No such file: " + cslFile.getName());
		}
		
		try {
			final List<String> contentAsList = Files.readAllLines(cslFile.toPath(), Charset.forName(StringUtils.DEFAULT_CHARSET));
			final StringBuilder content = new StringBuilder();
			for (final String line : contentAsList) {
				content.append("\n" + line);
			}
			return content.toString().trim();
		} catch (final IOException e) {
			throw new IOException("Problem while reading: " + cslFile.getName() + "\n" + e);
		}
	}
	
	/**
	 * @param cslFile
	 * @return returns a display name to a given csl style, which will be loaded
	 *         from <arg>.csl throws something if it didn't work
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String nameToTitle(final File cslFile) throws ParserConfigurationException, SAXException, IOException {
		if (cslFile == null) {
			throw new IllegalArgumentException("CSL File is null");
		}
		if (!cslFile.exists()) {
			throw new FileNotFoundException("No such file: " + cslFile.getName());
		}
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		final Document document = documentBuilder.parse(cslFile);
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
}
