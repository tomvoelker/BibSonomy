package org.bibsonomy.search.es.index;

import java.io.IOException;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * mapping builder for indexed {@link BibTex}
 *
 * @author dzo
 */
public class PublicationMappingBuilder extends ResourceMappingBuilder<BibTex> {

	/**
	 * @param resourceType
	 */
	public PublicationMappingBuilder(Class<BibTex> resourceType) {
		super(resourceType);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.ResourceMapping#doResourceSpecificMapping(org.elasticsearch.common.xcontent.XContentBuilder)
	 */
	@Override
	protected void doResourceSpecificMapping(XContentBuilder builder) throws IOException {
		builder
			.startObject(ESConstants.Fields.Publication.ADDRESS)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("annote")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("bKey")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("bibtexAbstract")
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject("bibtexKey")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("booktitle")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("chapter")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("crossref")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("day")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject() //
			.startObject("edition")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject() //
			.startObject("editor")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("entrytype")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("howPublished")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("institution")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("journal")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("misc")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("month")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("note")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("number")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("organization")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("pages")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("privnote")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_ANALYZED)
				.field("store", "false") // TODO: remove
			.endObject()
			.startObject("publisher")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("school")
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject("series")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("type")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("url")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("volume")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_INDEXED)
			.endObject()
			.startObject("year")
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_ANALYZED)
			.endObject()
			.startObject(ESConstants.NORMALIZED_ENTRY_TYPE_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, NOT_ANALYZED)
			.endObject()
			.startObject(ESConstants.AUTHOR_ENTITY_NAMES_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(ESConstants.AUTHOR_ENTITY_IDS_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject()
			.startObject(ESConstants.PERSON_ENTITY_NAMES_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
				.field(INDEX_FIELD, "analyzed")
			.endObject()
			.startObject(Fields.PERSON_ENTITY_IDS_FIELD_NAME)
				.field(TYPE_FIELD, STRING_TYPE)
			.endObject();
	}

}
