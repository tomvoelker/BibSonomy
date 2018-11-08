package org.bibsonomy.search.es.index.mapping.project;

import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * builds a mapping for elasticsarch to query {@link org.bibsonomy.model.cris.Project}
 *
 * @author dzo
 */
public class ProjectMapperBuilder implements MappingBuilder<XContentBuilder> {

	@Override
	public Mapping<XContentBuilder> getMapping() {
		return null;
	}

}
