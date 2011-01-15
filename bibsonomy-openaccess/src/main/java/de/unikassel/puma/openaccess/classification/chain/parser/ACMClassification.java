package de.unikassel.puma.openaccess.classification.chain.parser;

import java.util.LinkedHashMap;

import de.unikassel.puma.openaccess.classification.ClassificationObject;
import de.unikassel.puma.openaccess.classification.ClassificationParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class ACMClassification extends ClassificationParser {

private static final String NAME = "ACM";
	
	private StringBuffer buf = new StringBuffer();
	
	private String code;
	private String description;
	
	@Override
	public void startDocument() {
		classifications = new LinkedHashMap<String, ClassificationObject>();
		buf = new StringBuffer();
		code = "";
		description = "";
	}

	@Override
	public void endDocument() {
	}

	@Override
	public void startElement (final String uri, final String name, final String qName, final Attributes atts) throws SAXException {
		if ("node".equals(qName)) {

		} else if("isComposedBy".equals(qName)) {

		} else if("isRelatedTo".equals(qName) || "hasNote".equals(qName)) {
			//no op
		} else {
			throw new SAXException("Unable to parse");
		}
		buf = new StringBuffer();
	}

	/** Collect characters.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters (final char ch[], final int start, final int length) {
		buf.append(ch, start, length);
	}

	@Override
	public void endElement (final String uri, final String name, final String qName) throws SAXException {
		
	}
	
	private void requClassificate(String name, String description, ClassificationObject object) {
		String actual = name.charAt(0) +"";
		name = name.substring(1);
	
		if(object.getChildren().containsKey(actual)) {
			requClassificate(name, description, object.getChildren().get(actual));
		
		} else {
			//TODO might be cool for fatherdescriptions
			if(name.isEmpty()) {
				ClassificationObject co = new ClassificationObject(actual, description);
				object.addChild(actual, co);
				
			} else {
				ClassificationObject co = new ClassificationObject(actual, "FatherNode");
				object.addChild(actual, co);
				requClassificate(name, description, co);
			}
		}
	}
	
	//TODO what if only 1 char in name?
	private void classificate(String name, String description) {
		String actual = name.charAt(0) +"";
		name = name.substring(1);
	
		if(classifications.containsKey(actual)) {
			requClassificate(name, description, classifications.get(actual));
		} else {
			//TODO might be cool for fatherdescriptions
			ClassificationObject co = new ClassificationObject(actual, "FatherNode");
			classifications.put(actual, co);
			requClassificate(name, description, co);
		}
	}
	
	public String getName() {
		return NAME;
	}


}
