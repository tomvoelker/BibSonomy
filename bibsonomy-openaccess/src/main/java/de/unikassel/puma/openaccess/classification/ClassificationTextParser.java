package de.unikassel.puma.openaccess.classification;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author philipp
 * @version $Id$
 */
public abstract class ClassificationTextParser implements ClassificationParser {

	protected Map<String , ClassificationObject> classifications = null;
	
	public abstract void parse(BufferedReader in) throws IOException;
	
}
