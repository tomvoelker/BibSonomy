package DBLP.constants;

import java.io.FileReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/*
 * this class prepare and starts the parse of the constants XML
 */
public class DBLPConstantsReader{
	
	private DBLPConstantsHandler handler;
	
	public DBLPConstantsReader(){
		handler = null;
	}

	public void readConstants(String constantsXML) throws Exception{
		//prepare parse xml
		XMLReader xmlreader = XMLReaderFactory.createXMLReader(); 	
		handler = new DBLPConstantsHandler();
		xmlreader.setContentHandler(handler);
		xmlreader.setErrorHandler(handler);
			
		// parse XML
		xmlreader.parse(new InputSource(new FileReader(constantsXML)));
	}
	
	public DBLPConstantsResult getConResult(){
		return handler.getConResult();
	}
	
}