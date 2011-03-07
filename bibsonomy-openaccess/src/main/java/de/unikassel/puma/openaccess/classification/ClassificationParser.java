package de.unikassel.puma.openaccess.classification;

import java.util.LinkedHashMap;

import org.xml.sax.helpers.DefaultHandler;

public abstract class ClassificationParser extends DefaultHandler {

	protected LinkedHashMap<String , ClassificationObject> classifications;
	
	public LinkedHashMap<String, ClassificationObject> getList() {
		return classifications;
	}
	
	public abstract String getName();
	
	public abstract String getDelimiter();
	
	
}
