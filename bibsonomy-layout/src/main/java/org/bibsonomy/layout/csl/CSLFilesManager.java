package org.bibsonomy.layout.csl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
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
	
	final FilenameFilter CSLFilter = new FilenameFilter(){
		@Override
		public boolean accept(File dir, String name) {
	        return name.toLowerCase().endsWith(".csl");
	    }
	};
	
	
	private File readStyle(final String CSLFileName){
		String searchFor = CSLFileName + ".csl";
		for(File f : CSLFolder.listFiles(CSLFilter)){
			if((f.getName().compareToIgnoreCase(searchFor)) == 0){
				return f;	
			}	
		}
		return null;
	}
	
	public String nameToXML(String CSLFileName){
		final File CSLFile = readStyle(CSLFileName);
		if (CSLFile == null || !CSLFile.exists()){
			return "No such file: " + CSLFileName;			
		}
		
		List<String> content;
		try {
			content = Files.readAllLines(CSLFile.toPath());
		} catch (IOException e) {
			return "Problem while reading: " + CSLFileName;	
		}
		String returner = "";
		for (String line : content){
			returner = returner + "\n" + line;
		}
		return returner;
	}
	
	public String allToJson(){
		//only reading .csl files
		String jsonString = "{\"layouts\":[";
		for(File f : CSLFolder.listFiles(CSLFilter)){
			String filename = f.getName().trim().toUpperCase();
			filename = filename.replaceAll(".CSL", "");
			String displayname;
			try {
				displayname = nameToTitle(filename);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				return  "FAILED AT FILE:" + filename;
			}
			jsonString = jsonString + "{\"source\":\"CSL\",\"name\":\"" + filename + "\",\"displayName\":\"" + displayname + "\"},";
		}
		if(jsonString.endsWith(",")){
			jsonString = jsonString.substring(0, jsonString.lastIndexOf(','));
		}
		jsonString += "]}";
		return jsonString;
	}
	public String nameToTitle(final String CSLFileName) throws ParserConfigurationException, SAXException, IOException{
		final File CSLFile = readStyle(CSLFileName);
		if (CSLFile == null || !CSLFile.exists()){
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
