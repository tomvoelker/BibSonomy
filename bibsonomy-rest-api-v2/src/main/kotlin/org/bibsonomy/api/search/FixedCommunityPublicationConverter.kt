package org.bibsonomy.api.search

import org.bibsonomy.model.BibTex
import org.bibsonomy.model.GoldStandardPublication
import org.bibsonomy.model.Post
import org.bibsonomy.search.util.Converter
import org.bibsonomy.search.index.utils.FileContentExtractorService
import java.net.URI

/**
 * Replacement for the broken CommunityPublicationConverter bytecode in the
 * legacy search module. Minimal converter to keep the search wiring alive
 * without relying on legacy compiled classes.
 */
class FixedCommunityPublicationConverter(
    @Suppress("UNUSED_PARAMETER") systemURI: URI,
    @Suppress("UNUSED_PARAMETER") fileContentExtractorService: FileContentExtractorService
) : Converter<Post<BibTex>, Map<String, Any>, Set<String>> {

    override fun convert(source: Post<BibTex>): Map<String, Any> {
        val postId = source.contentId ?: "unknown"
        val title = source.resource?.title ?: "unknown"
        throw UnsupportedOperationException(
            "convert(Post<BibTex> -> Map) is not implemented in FixedCommunityPublicationConverter stub " +
            "(post id: $postId, title: $title). This converter is a minimal placeholder for broken legacy search bytecode."
        )
    }

    override fun convert(source: Map<String, Any>, options: Set<String>): Post<BibTex> = Post<BibTex>().apply {
        resource = GoldStandardPublication().apply {
            title = source["title"] as? String
            abstract = source["abstract"] as? String
        }
        isApproved = source["approved"] as? Boolean ?: false
    }
}
