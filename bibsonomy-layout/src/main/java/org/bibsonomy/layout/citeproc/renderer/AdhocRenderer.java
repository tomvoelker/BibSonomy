package org.bibsonomy.layout.citeproc.renderer;

import de.undercouch.citeproc.LocaleProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.output.Bibliography;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.layout.citeproc.CSLUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated use {@link CachedCSLRenderer}. This renderer is single threaded and benchmarks have demonstrated that is
 * significant slower
 */
public class AdhocRenderer {
	private static final Log LOG = LogFactory.getLog(AdhocRenderer.class);

	/**
	 * Creates a bibliography that contains all BibTeX elements in posts. The entries in this bibliography are
	 * context sensitive and names are disambiguates if the style dictates it so.
	 *
	 * @param posts       list of posts with bibtex resources to render as html
	 * @param cslTemplate the citation style to use. May either be a serialized XML representation of the style or a
	 *                    style's name such as {@code ieee}. In the latter case, the processor loads the style
	 *                    from the classpath (e.g. {@code /ieee.csl})
	 * @return a map of postIDs to their corresponding html
	 */
	public static Map<String, String> renderPosts(final List<Post<? extends BibTex>> posts, final String cslTemplate, LocaleProvider localeProvider, boolean addSurroundingTextTags) {

		final CSLItemData[] cslItems = Arrays.stream(CSLUtils.convertToCslItemData(posts, addSurroundingTextTags))
				.map(CSLUtils.CSLItemDataConversionResult::getItemData)
				.toArray(CSLItemData[]::new);

		Bibliography fullbib = null;
		try {
			fullbib = CSLWithLinks.makeAdhocBibliography(cslTemplate, localeProvider, "html", cslItems);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Each element of the entryIds array correspond with the same element of the entries array.
		final String[] ids = fullbib.getEntryIds();
		final String[] entries = fullbib.getEntries();
		final Map<String, String> idToEntryMap = new HashMap<>();
		for (int i = 0; i < ids.length; i++) {
			idToEntryMap.put(ids[i], entries[i]);
		}

		if (LOG.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("posts: [");
			for (final Post<? extends BibTex> p : posts) {
				sb.append("\"" + p.getResource().getTitle() + "\", ");
			}
			sb.append("]");
			LOG.debug(sb.toString());

			sb = new StringBuffer();
			sb.append("posts converted to cslItems: [");
			for (final CSLItemData cslItem : cslItems) {
				sb.append(cslItem.getId() + " (\"" + cslItem.getTitle() + "\"), ");
			}
			sb.append("]");
			LOG.debug(sb.toString());

			LOG.debug("entryIds: " + Arrays.toString(ids));
			LOG.debug("entries: " + Arrays.toString(entries));
		}
		return idToEntryMap;
	}

}
