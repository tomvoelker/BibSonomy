package org.bibsonomy.layout.csl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
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
	final File cachedJSON = new File(cslFolderDirec + "cache.cached");

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
	 * @param CSLFileName
	 * @return the .csl file with given name
	 */
	private File readStyle(final String CSLFileName) {
		String searchFor;
		if(!CSLFileName.endsWith(".csl")){
			searchFor = CSLFileName + ".csl";
		}else{
			searchFor = CSLFileName;
		}
		for (File f : CSLFolder.listFiles(CSLFilter)) {
			if ((f.getName().compareToIgnoreCase(searchFor)) == 0) {
				return f;
			}
		}
		return null;
	}

	/**
	 * @param CSLFileName
	 * @return content of .csl file with given name
	 */
	public String nameToXML(String CSLFileName) {
		final File CSLFile = readStyle(CSLFileName);
		if (CSLFile == null || !CSLFile.exists()) {
			return "No such file: " + CSLFileName;
		}

		List<String> content;
		try {
			content = Files.readAllLines(CSLFile.toPath());
		} catch (IOException e) {
			return "Problem while reading: " + CSLFileName;
		}
		StringBuilder returner = new StringBuilder();
		for (String line : content) {
			returner.append("\n" + line);
		}
		return returner.toString();
	}

	/**
	 * @return returns either a cached version of all .csl files or creates a new String in the format "layouts":[{"source":"CSL","name":"bla_bla","displayName":"Bla Bla"}]
	 */
	public String allToJson() {
		//TODO: check. Files are being copied on server launch. so not 100% sure here. hard to check
		if (cachedJSON != null && cachedJSON.exists() && cachedJSON.isFile()) {
			Date cachedLastModified = new Date(cachedJSON.lastModified());
			Date today = new Date();
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(cachedLastModified);
			cal2.setTime(today);
			boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
					&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
			// checking whether cache is older than one day
			if (!sameDay) {
				cachedJSON.delete();
			} else {
				// checking whether everything has already been cached.
				// check whether cache is up to date
				long lastModified = Long.MIN_VALUE;
				for (File f : CSLFolder.listFiles(CSLFilter)) {
					long fileLastModified = f.lastModified();
					if (lastModified < fileLastModified) {
						lastModified = fileLastModified;
					}
				}
				//check which version is more up to date
				if (lastModified > cachedJSON.lastModified()) {
					cachedJSON.delete();
				} else {
					try {
						List<String> allLines = Files.readAllLines(cachedJSON.toPath());
						StringBuilder returner = new StringBuilder();
						for (String line : allLines) {
							returner.append("\n" + line);
						}
						return returner.toString();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}

		// only reading .csl files
		StringBuilder json = new StringBuilder();
		json.append("{\"layouts\":[");
		for (File f : CSLFolder.listFiles(CSLFilter)) {
			String filename = f.getName().trim().toUpperCase();
			filename = filename.replaceAll(".CSL", "");
			String displayname;
			try {
				displayname = nameToTitle(filename);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				return "FAILED AT FILE:" + filename;
			}
			json.append("{\"source\":\"CSL\",\"name\":\"" + filename + "\",\"displayName\":\"" + displayname + "\"},");
		}
		String jsonString = json.toString();
		if (jsonString.endsWith(",")) {
			jsonString = jsonString.substring(0, jsonString.lastIndexOf(','));
		}
		jsonString += "]}";
		try {
			PrintWriter writer = new PrintWriter(cachedJSON, "UTF-8");
			writer.println(jsonString);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		}
		return jsonString;
	}

	/**
	 * @param CSLFileName
	 * @return returns a display name to a given csl style, which will be loaded from <arg>.csl throws something if it didn't work
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public String nameToTitle(final String CSLFileName) throws ParserConfigurationException, SAXException, IOException {
		final File CSLFile = readStyle(CSLFileName);
		if (CSLFile == null || !CSLFile.exists()) {
			return "No such file: " + CSLFileName;
		}
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(CSLFile);
		String title = document.getElementsByTagName("title").item(0).getTextContent();
		title = title.replaceAll("\"", "'");
		return title;
	}
}
