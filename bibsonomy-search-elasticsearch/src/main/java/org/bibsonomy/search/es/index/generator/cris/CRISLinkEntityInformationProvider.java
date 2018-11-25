package org.bibsonomy.search.es.index.generator.cris;

import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

/**
 * entity information provider for
 *
 * @author dzo
 */
public class CRISLinkEntityInformationProvider extends EntityInformationProvider<CRISLink> {
	public static final String CRISLINK_TYPE = "CRISLink";

	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 */
	protected CRISLinkEntityInformationProvider(Converter<CRISLink, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder) {
		super(converter, mappingBuilder);
	}

	@Override
	public int getContentId(final CRISLink crisLink) {
		return crisLink.getId();
	}

	@Override
	public String getEntityId(final CRISLink entity) {
		return "crislink_" + entity.getId();
	}

	@Override
	public String getType() {
		return CRISLINK_TYPE;
	}

	@Override
	public String getRouting(final CRISLink entity) {
		return entity.getSource().getLinkableId();
	}
}
