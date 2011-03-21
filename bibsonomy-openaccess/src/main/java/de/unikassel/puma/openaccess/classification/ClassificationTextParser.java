package de.unikassel.puma.openaccess.classification;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;

public abstract class ClassificationTextParser implements ClassificationParser {

	protected LinkedHashMap<String , ClassificationObject> classifications = null;
	
	public abstract void parse(BufferedReader in) throws IOException;
	
}
