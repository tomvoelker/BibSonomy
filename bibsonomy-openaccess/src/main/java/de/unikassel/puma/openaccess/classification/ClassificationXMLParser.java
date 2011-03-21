package de.unikassel.puma.openaccess.classification;

import java.util.LinkedHashMap;

import org.xml.sax.helpers.DefaultHandler;

public abstract class ClassificationXMLParser extends DefaultHandler implements ClassificationParser {

	protected LinkedHashMap<String , ClassificationObject> classifications = null;

	@Override
	public abstract String getDelimiter();

	@Override
	public LinkedHashMap<String, ClassificationObject> getList() {
		return classifications;
	}

	@Override
	public abstract String getName();

}
