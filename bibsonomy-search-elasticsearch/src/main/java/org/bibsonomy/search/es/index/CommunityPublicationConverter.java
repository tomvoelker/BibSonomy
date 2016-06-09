package org.bibsonomy.search.es.index;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;

/**
 * converter for {@link GoldStandardPublication}
 *
 * @author dzo
 */
public class CommunityPublicationConverter extends PublicationConverter {

	/**
	 * @param systemURI
	 * @param fileContentExtractorService
	 */
	public CommunityPublicationConverter(URI systemURI) {
		super(systemURI, null);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.PublicationConverter#createNewResource()
	 */
	@Override
	protected BibTex createNewResource() {
		return new GoldStandardPublication();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.PublicationConverter#convertDocuments(java.util.List)
	 */
	@Override
	public List<Map<String, String>> convertDocuments(List<Document> documents) {
		// nothing to do
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#fillUser(org.bibsonomy.model.Post, java.lang.String)
	 */
	@Override
	protected void fillUser(Post<BibTex> post, String userName) {
		// nothing to do
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceConverter#fillIndexDocument(org.bibsonomy.model.Post, java.util.Map)
	 */
	@Override
	protected void fillIndexDocument(Post<BibTex> post, Map<String, Object> jsonDocument) {
		// nothing to do
	}
}
