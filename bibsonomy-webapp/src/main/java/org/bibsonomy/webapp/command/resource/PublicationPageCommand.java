package org.bibsonomy.webapp.command.resource;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;

/**
 * @author dzo
 * @version $Id$
 */
public class PublicationPageCommand extends ResourcePageCommand<BibTex> {
	/**
	 * additional metadata for bibtex resource
	 * e.g.
	 * <code>
	 * additionalMetadataMap
	 *  {
	 *  	DDC=[010, 050, 420, 422, 334, 233], 
	 *  	post.resource.openaccess.additionalfields.additionaltitle=[FoB], 
	 *  	post.resource.openaccess.additionalfields.phdreferee2=[Petra Musterfrau], 
	 *  	post.resource.openaccess.additionalfields.phdreferee=[Peter Mustermann], 
	 *  	ACM=[C.2.2], 
	 *  	JEL=[K12], 
	 *  	post.resource.openaccess.additionalfields.sponsor=[DFG, etc..], 
	 *  	post.resource.openaccess.additionalfields.phdoralexam=[17.08.2020], 
	 *  	post.resource.openaccess.additionalfields.institution=[Uni KS tEST ]
	 *  }
	 *  </code>
	 */
	private Map<String, List<String>> additionalMetadata;

	/**
	 * @return the additionalMetadata
	 */
	public Map<String, List<String>> getAdditionalMetadata() {
		return this.additionalMetadata;
	}

	/**
	 * @param additionalMetadata the additionalMetadata to set
	 */
	public void setAdditionalMetadata(final Map<String, List<String>> additionalMetadata) {
		this.additionalMetadata = additionalMetadata;
	}
}
