package org.bibsonomy.search.es.index.generator.cris;

import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.search.es.index.converter.project.ProjectFields;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.util.Converter;

import java.util.Map;

/**
 * entity information provider for
 *
 * @author dzo
 */
public class CRISLinkEntityInformationProvider extends EntityInformationProvider<CRISLink> {

	/**
	 * the entity information provider
	 *
	 * @param converter
	 */
	protected CRISLinkEntityInformationProvider(final Converter<CRISLink, Map<String, Object>, ?> converter) {
		super(converter, null);
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
		return ProjectFields.PROJECT_DOCUMENT_TYPE;
	}

	@Override
	public String getRouting(final CRISLink entity) {
		return entity.getSource().getLinkableId();
	}
}
