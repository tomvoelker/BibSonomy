package org.bibsonomy.api.search

import org.bibsonomy.model.Bookmark
import org.bibsonomy.model.GoldStandardBookmark
import org.bibsonomy.model.Post
import org.bibsonomy.search.util.Converter
import java.net.URI

/**
 * Replacement for the broken CommunityBookmarkConverter bytecode in the legacy
 * search module. Minimal converter to keep the search wiring alive without
 * relying on the legacy compiled classes.
 */
class FixedCommunityBookmarkConverter(@Suppress("UNUSED_PARAMETER") systemURI: URI) :
    Converter<Post<Bookmark>, Map<String, Any>, Set<String>> {

    override fun convert(source: Post<Bookmark>): Map<String, Any> = emptyMap()

    override fun convert(source: Map<String, Any>, options: Set<String>): Post<Bookmark> = Post<Bookmark>().apply {
        resource = GoldStandardBookmark().apply { url = source["url"] as? String }
    }
}
