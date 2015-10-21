package org.bibsonomy.search.es.index;

import java.io.IOException;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * mappping builder for indexed {@link Bookmark}s
 *
 * @author dzo
 */
public class BookmarkMappingBuilder extends ResourceMappingBuilder<Bookmark> {
	
	/**
	 * @param resourceType
	 */
	public BookmarkMappingBuilder(Class<Bookmark> resourceType) {
		super(resourceType);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceMapping#doResourceSpecificMapping(org.elasticsearch.common.xcontent.XContentBuilder)
	 */
	@Override
	protected void doResourceSpecificMapping(XContentBuilder builder) throws IOException {
		builder.startObject(Fields.Bookmark.URL)
			.field(TYPE_FIELD, STRING_TYPE)
		.endObject();
	}
}
