package org.bibsonomy.layout.csl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 *
 * @author jp
 */
public class CSLFilesManager {
	final String directory = "org/bibsonomy/layout/csl/";
	final String cslFolderDirec = this.getClass().getClassLoader().getResource(directory).getPath();
	final File CSLFolder = new File(cslFolderDirec);

	// mapping from id to CSLStyle which contains the id itself, a display name and the content of the file
	HashMap<String, CSLStyle> CSLFiles = new HashMap<String, CSLStyle>();
	// complete JSON String ready to be displayed
	String json;

	private class CSLStyle {
		private String id;
		private String displayName;
		private String content;

		/**
		 * @param name
		 * @param nameToTitle
		 * @param nameToXML
		 */
		public CSLStyle(String id, String displayName, String content) {
			this.id = id;
			this.displayName = displayName;
			this.content = content;
		}

		/**
		 * @return the id
		 */
		private String getId() {
			return this.id;
		}

		/**
		 * @param id
		 *            the id to set
		 */
		private void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the displayName
		 */
		private String getDisplayName() {
			return this.displayName;
		}

		/**
		 * @param displayName
		 *            the displayName to set
		 */
		private void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		/**
		 * @return the content
		 */
		private String getContent() {
			return this.content;
		}

		/**
		 * @param content
		 *            the content to set
		 */
		private void setContent(String content) {
			this.content = content;
		}
	}

	final FilenameFilter CSLFilter = new FilenameFilter() {
		@Override
		/**
		 * @return only allows .csl files
		 */
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".csl");
		}
	};

	/**
	 * Spring init
	 */
	public void init() {
		for (File f : CSLFolder.listFiles(CSLFilter)) {
			try {
				CSLFiles.put(f.getName(), new CSLStyle(f.getName(), nameToTitle(f), nameToXML(f)));
			} catch (ParserConfigurationException | SAXException | IOException e) {
				//TODO logging
				continue;
			}
		}
		//TODO read custom user uploads
		jsonInit();
	}

	/**
	 * @param CSLFile
	 * @return content of .csl file with given name
	 * @throws IOException
	 */
	private static String nameToXML(final File CSLFile) throws IOException {
		if (CSLFile == null || !CSLFile.exists()) {
			if (CSLFile != null)
				throw new FileNotFoundException("No such file: " + CSLFile.getName());
			throw new FileNotFoundException("No such file");
		}
		List<String> content;
		try {
			content = Files.readAllLines(CSLFile.toPath());
		} catch (IOException e) {
			throw new IOException("Problem while reading: " + CSLFile.getName() + "\n" + e);
		}
		StringBuilder returner = new StringBuilder();
		for (String line : content) {
			returner.append("\n" + line);
		}
		return returner.toString();
	}

	/**
	 * initializes the json and saves it to a string which can be returned
	 */
	private void jsonInit() {
		// only reading .csl files
		StringBuilder jsonSB = new StringBuilder();
		jsonSB.append("{\"layouts\":[");
		for (String key : CSLFiles.keySet()) {
			String filename = CSLFiles.get(key).getId();
			filename = filename.replaceAll(".csl", "");
			String displayname = CSLFiles.get(key).getDisplayName();
			jsonSB.append("{\"source\":\"CSL\",\"name\":\"" + filename + "\",\"displayName\":\"" + displayname + "\"},");
		}
		String jsonString = jsonSB.toString();
		if (jsonString.endsWith(",")) {
			jsonString = jsonString.substring(0, jsonString.lastIndexOf(','));
		}
		jsonString += "]}";
		json = jsonString;
	}

	/**
	 * @param CSLFile
	 * @return returns a display name to a given csl style, which will be loaded
	 *         from <arg>.csl throws something if it didn't work
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String nameToTitle(final File CSLFile) throws ParserConfigurationException, SAXException, IOException {
		if (CSLFile == null || !CSLFile.exists()) {
			if (CSLFile != null)
				throw new FileNotFoundException("No such file: " + CSLFile.getName());
			throw new FileNotFoundException("No such file");
		}
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(CSLFile);
		String title = document.getElementsByTagName("title").item(0).getTextContent();
		title = title.replaceAll("\"", "'");
		return title;
	}
	
	/**
	 * @return the prewritten jsonString
	 */
	public String getJSONString(){
		return json;
	}
	
	/**
	 * @param CSLFileName
	 * @return the correct display name for this CSL - style
	 */
	public String getDisplayName(final String CSLFileName){
		String returner = CSLFiles.get(CSLFileName).getDisplayName();
		if(returner == null || returner.isEmpty()){
			return "";
		}
		return returner;
	}
	
	/**
	 * @param CSLFileName
	 * @return the correct XML file content for this CSL - style
	 */
	public String getXML(final String CSLFileName){
	String returner = CSLFiles.get(CSLFileName).getContent();
	if(returner == null || returner.isEmpty()){
		return "";
	}
	return returner;
}
}
