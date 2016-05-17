package org.bibsonomy.layout.jabref;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * TODO: add documentation to this class
 *
 * @author jp
 */
public class JabRefFilesManager {
	final String directory = "org/bibsonomy/layout/jabref/";
	final String cslFolderDirec = this.getClass().getClassLoader().getResource(directory).getPath();
	final File CSLFolder = new File(cslFolderDirec);
	final String XMLFileDirec = this.getClass().getClassLoader().getResource(directory + "JabrefLayouts.xml").getPath();
	final File XMLFile = new File(XMLFileDirec);
	
	public String nameToTitle(final String JabrefID) {
		if (XMLFile == null || !XMLFile.exists()){
			return "JabRef indexing XML File has been moved: " + XMLFile;			
		}
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		Document document = null;
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = null;
		String title = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			//TODO
		}
		try {
			document = documentBuilder.parse(XMLFile);
		} catch (SAXException | IOException e) {
			// TODO 
		}
		
		try {
			expr = xpath.compile("/layouts/layout[@name='" + JabrefID + "']/title");
		} catch (XPathExpressionException e) {
			// TODO
		}
		try {
			title = expr.evaluate(document, XPathConstants.STRING).toString();
		} catch (XPathExpressionException e) {
			// TODO
		}
		title = title.replaceAll("\"", "'");
		return title;
	}
}
