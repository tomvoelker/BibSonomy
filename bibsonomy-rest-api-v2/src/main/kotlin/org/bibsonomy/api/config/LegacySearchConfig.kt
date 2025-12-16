package org.bibsonomy.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

/**
 * Wires the legacy search stack (DB search + Elasticsearch searchers) without
 * the webapp servlet layer. Uses a trimmed search context to avoid re-importing
 * the full legacy database configuration.
 */
@Configuration
@ImportResource("classpath:org/bibsonomy/api/bibsonomy-search-elasticsearch-lite-context.xml")
class LegacySearchConfig
