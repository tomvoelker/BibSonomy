package de.unikassel.puma.openaccess.classification;

import java.io.IOException;
import java.net.URL;

/**
 * @author philipp
  */
public interface ClassificationSource {

	public Classification getClassification(URL url) throws IOException; 
	
}
