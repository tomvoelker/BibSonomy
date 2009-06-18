package org.bibsonomy.services.importer;

import java.util.List;

import org.bibsonomy.model.Tag;

/**
 * Imports relations from a remote service or file. Additional interface, classes
 * can implement together with {@link FileBookmarkImporter} or {@link RemoteServiceBookmarkImporter}.
 * 
 * @author rja
 * @version $Id$
 */
public interface RelationImporter {

	/**
	 * @return The imported relations. 
	 */
	public List<Tag> getRelations();
	
}
