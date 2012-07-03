package de.unikassel.puma.openaccess.classification;

import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

/**
 * @author philipp
 * @version $Id$
 */
public abstract class ClassificationXMLParser extends DefaultHandler implements ClassificationParser {

	protected Map<String , ClassificationObject> classifications = null;

	@Override
	public Map<String, ClassificationObject> getList() {
		return this.classifications;
	}

}
