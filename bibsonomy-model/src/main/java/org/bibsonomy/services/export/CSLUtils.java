package org.bibsonomy.services.export;

import org.bibsonomy.services.filesystem.CslFileLogic;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * helper methods for csl files
 *
 * @author jp
 * @author dzo
 */
public class CSLUtils {

	/** all user uploaded files are prefixed with custom */
	public static final String CUSTOM_PREFIX = "CUSTOM ";

	public static String normStyle(String style) {
		return style.replaceAll("." + CslFileLogic.LAYOUT_FILE_EXTENSION.toUpperCase(), "");
	}

	/**
	 * @param cslStyleSource
	 * @return the title of the csl file
	 *
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static String extractTitle(final String cslStyleSource) throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		final Document document = documentBuilder.parse(new InputSource(new StringReader(cslStyleSource)));

		return document.getElementsByTagName("title").item(0).getTextContent().trim();
	}
}

