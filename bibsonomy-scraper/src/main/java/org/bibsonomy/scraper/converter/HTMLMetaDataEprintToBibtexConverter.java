package org.bibsonomy.scraper.converter;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.id.DOIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLMetaDataEprintToBibtexConverter extends AbstractDublinCoreToBibTeXConverter {

	private static final Pattern EPRINT_PATTERN = Pattern.compile("<meta\\s*name=\"eprints\\.(.*?)\"\\s*content=\"([\\s\\S]*?)\"\\s*/?>", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

	@Override
	protected Map<String, String> extractData(String pageContent) {
		HashMap<String, String> bibtexFields = new HashMap<>();
		Matcher m_eprint = EPRINT_PATTERN.matcher(pageContent);
		while (m_eprint.find()) {
			String key = m_eprint.group(1);
			String content = m_eprint.group(2).replaceAll("\\n", " ");
			switch (key) {
				case "editors_name":
					StringUtils.appendIfPresent(bibtexFields, "editor", content, " and ");
					break;
				case "creators_name":
					StringUtils.appendIfPresent(bibtexFields, "author", content, " and ");
					break;
				case "title":
					bibtexFields.put("title", content);
					break;
				case "keywords":
					bibtexFields.put("keywords", content);
					break;
				case "abstract":
					bibtexFields.put("abstract", content);
					break;
				case "date":
					bibtexFields.put("year", extractYear(content));
					break;
				case "series":
					bibtexFields.put("series", content);
					break;
				case "publisher":
					bibtexFields.put("publisher", content);
					break;
				case "pagerange":
					bibtexFields.put("pagerange", content);
					break;
				case "isbn":
					bibtexFields.put("isbn", content);
					break;
				case "book_title":
					bibtexFields.put("booktitle", content);
					break;
				case "official_url":
					bibtexFields.put("url", content);
					break;
				case "type":
					bibtexFields.put("type", content);
					break;
				case "volume":
					bibtexFields.put("volume", content);
					break;
				case "issn":
					bibtexFields.put("issn", content);
					break;
				case "number":
					bibtexFields.put("number", content);
					break;
				case "id_number":
					if (DOIUtils.isDOI(content)) {
						bibtexFields.put("doi", content);
					}
					break;
				case "publication":
					bibtexFields.put("journal", content);
					break;
				case "note":
					bibtexFields.put("note", content);
					break;
			}
		}
		return bibtexFields;
	}

	@Override
	protected String getEntrytype(Map<String, String> data) {
		String ePrintType = data.get("type");
		//this is not an exhaustive list of all ePrintTypes, because I couldn't an official list of all types
		switch (ePrintType) {
			case "conference_item":
				return BibTexUtils.INPROCEEDINGS;
			case "article":
				return BibTexUtils.ARTICLE;
			case "book_section":
				return BibTexUtils.INBOOK;
			case "book":
				return BibTexUtils.BOOK;
			case "thesis":
				return BibTexUtils.PHD_THESIS;
			default:
				return BibTexUtils.MISC;
		}
	}
}
