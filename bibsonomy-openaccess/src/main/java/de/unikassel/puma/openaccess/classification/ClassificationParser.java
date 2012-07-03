package de.unikassel.puma.openaccess.classification;

import java.util.Map;

/**
 * @author philipp
 * @version $Id$
 */
public interface ClassificationParser {
	
	public Map<String, ClassificationObject> getList();
	
	public abstract String getName();
	
	public abstract String getDelimiter();
}
