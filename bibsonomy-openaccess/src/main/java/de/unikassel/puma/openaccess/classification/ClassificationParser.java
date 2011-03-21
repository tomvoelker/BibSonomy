package de.unikassel.puma.openaccess.classification;

import java.util.LinkedHashMap;

public interface ClassificationParser {
	
	public LinkedHashMap<String, ClassificationObject> getList();
	
	public abstract String getName();
	
	public abstract String getDelimiter();
}
