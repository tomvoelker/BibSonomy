package org.bibsonomy.services.importer;


/**
 * Creates a new instance of the {@link RemoteServiceBookmarkImporter}.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public interface RemoteServiceBookmarkImporterFactory {
	
	/**
	 * @return An instance of remote service bookmark importer
	 */
	public RemoteServiceBookmarkImporter getImporter();

}

