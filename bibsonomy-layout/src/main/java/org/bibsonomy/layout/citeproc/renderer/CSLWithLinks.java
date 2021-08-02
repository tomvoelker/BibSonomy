package org.bibsonomy.layout.citeproc.renderer;

import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.ListItemDataProvider;
import de.undercouch.citeproc.LocaleProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.output.Bibliography;

import java.io.IOException;

/**
 * @author ???
 */
public class CSLWithLinks {

	/**
	 * Creates an ad hoc bibliography from the given citation items. Calling
	 * this method is rather expensive as it initializes the CSL processor.
	 * If you need to create bibliographies multiple times in your application
	 * you should create the processor yourself and cache it if necessary.
	 * <p>
	 * This version of {@link CSL#makeAdhocBibliography(String, String, CSLItemData...)} sets the option
	 * {@code development_extensions.wrap_url_and_doi = true}, resulting in the rendering of urls as html anchors.
	 *
	 * @param style        the citation style to use. May either be a serialized
	 *                     XML representation of the style or a style's name such as <code>ieee</code>.
	 *                     In the latter case, the processor loads the style from the classpath (e.g.
	 *                     <code>/ieee.csl</code>)
	 * @param outputFormat the processor's output format (one of
	 *                     <code>"html"</code>, <code>"text"</code>, <code>"asciidoc"</code>,
	 *                     <code>"fo"</code>, or <code>"rtf"</code>)
	 * @param items        the citation items to add to the bibliography
	 * @return the bibliography
	 * @throws IOException if the underlying JavaScript files or the CSL style
	 *                     could not be loaded
	 */
	static Bibliography makeAdhocBibliography(final String style, final LocaleProvider localeProvider, final String outputFormat, final CSLItemData... items) throws IOException {
		final ItemDataProvider provider = new ListItemDataProvider(items);
		try (final CSL csl = new CSL(provider, localeProvider, style, "en-US", false)) {
			csl.setConvertLinks(true);
			csl.setOutputFormat(outputFormat);

			final String[] ids = new String[items.length];
			for (int i = 0; i < items.length; ++i) {
				ids[i] = items[i].getId();
			}
			csl.registerCitationItems(ids);

			return csl.makeBibliography();
		}
	}
}
