/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class OrcidToBibtexConverter implements BibtexConverter{

    private Map<String, String> entrytypeMap;
    private Map<String, String> fieldMap;

    public OrcidToBibtexConverter() {
        this.entrytypeMap = new HashMap<>();
        this.entrytypeMap.put("journal-article", "article");
        this.entrytypeMap.put("book", "book");
        this.entrytypeMap.put("book-chapter", "incollection");
        this.entrytypeMap.put("conference-paper", "inproceedings");
        this.entrytypeMap.put("manual", "manual");
        this.entrytypeMap.put("supervised-student-publication", "mastersthesis");
        this.entrytypeMap.put("dissertation-thesis", "phdthesis");
        this.entrytypeMap.put("report", "techreport");
        this.entrytypeMap.put("other", "misc");

        // some general field mappings
        this.fieldMap = new HashMap<>();
        this.fieldMap.put("url", "url");

        this.fieldMap.put("pages", "pages");
        this.fieldMap.put("page", "pages");
        this.fieldMap.put("publisher-place", "address");
        this.fieldMap.put("event-place", "location");
        this.fieldMap.put("keyword", "keywords");
    }

    public String toBibtex(JSONObject citation) {
        String bibtexSource = this.extractBibtexSource(citation);
        if (present(bibtexSource)) {
            return bibtexSource;
        }

        final StringBuilder builder = new StringBuilder("@");

        // get author and editors
        final List<Pair<String, String>> persons = extractPersons(citation);
        final String authors = filterPersonsByRole(persons, "author");
        final String editors = filterPersonsByRole(persons, "editor");

        // get publication date
        final JSONObject pubDateObj = (JSONObject) citation.get("publication-date");
        String year = extractYear(pubDateObj);

        // get title, entrytype, bibtexkey
        final String title = extractTitle(citation);
        final String entrytype = getEntrytype(citation);
        final String citationKey = BibTexUtils.generateBibtexKey(authors, editors, year, title);

        // begin building bibtex with entrytype, bibtexkey and title
        builder.append(entrytype).append("{").append(citationKey).append(",\n");
        if (present(title)) {
            builder.append(getBibTeX("title", title));
        }

        final String workAbstract = extractAbstract(citation);
        if (present(workAbstract)) {
            builder.append(getBibTeX("abstract", workAbstract));
        }

        final String journal = this.getFieldIfPresent(citation, "journal-title");
        if (present(journal)) {
            builder.append(getBibTeX("journal", journal));
        }

        if (present(authors)) {
            builder.append(getBibTeX("author", authors));
        }

        if (present(editors)) {
            builder.append(getBibTeX("editor", editors));
        }

        builder.append(getBibTeX("year", year));
        if (present(pubDateObj)) {
            String month = this.getFieldIfPresent(pubDateObj, "month");
            if (present(month)) {
                builder.append(getBibTeX("month", month));
                String day = this.getFieldIfPresent(pubDateObj, "day");
                if (present(day)) {
                    builder.append(getBibTeX("day", day));
                }
            }
        }

        // Apply general fields, such as abstract, URL, etc.
        this.applyMapping(citation, this.fieldMap, builder);

        // Apply external IDs, such as DOI, ISBN, etc.
        JSONObject externalIds = (JSONObject) citation.get("external-ids");
        if (present(externalIds) && containsAndPresent(externalIds, "external-id")) {
            this.applyExternalIds((JSONArray) externalIds.get("external-id"), builder);
        }

        builder.append("}");
        return builder.toString();
    }

    /**
     * Extracts further BibTeX attributes and applies it to the BibTeX builder
     * @param citation
     * @param mapping
     * @param builder
     */
    private void applyMapping(final JSONObject citation, final Map<String, String> mapping, final StringBuilder builder) {
        mapping.forEach((key, field) -> {
            if (citation.containsKey(key)) {
                final String valueToCopy = this.getValue((JSONObject) citation.get(key));
                builder.append(getBibTeX(field, valueToCopy));
            }
        });
    }

    /**
     * Extract external IDs of the ORCID works object. For example: ISBN, ISSN, etc.
     * @param externalIds
     * @param builder
     */
    private void applyExternalIds(final JSONArray externalIds, final StringBuilder builder) {
        externalIds.forEach(item -> {
            JSONObject extIdObj = (JSONObject) item;
            String type = (String) extIdObj.get("external-id-type");
            String value = (String) extIdObj.get("external-id-value");
            builder.append(getBibTeX(type, value));
        });
    }

    /**
     * Extracts the entrytype of the ORCID Works object.
     * @param obj
     * @return the entrytype
     */
    private String getEntrytype(JSONObject obj) {
        String type = "";
        if (obj.containsKey("type")) {
            type = (String) obj.get("type");
        }

        return this.entrytypeMap.getOrDefault(type, "misc");
    }

    /**
     * Extracts the title from the ORCID Works object.
     * @param obj
     * @return the title
     */
    private String extractTitle(JSONObject obj) {
        if (obj.containsKey("title")) {
            JSONObject titleGroup = (JSONObject) obj.get("title");
            return this.getValue((JSONObject) titleGroup.get("title"));
        }
        return "";
    }

    /**
     * Extracts the abstract from the ORCID Works object.
     * @param obj
     * @return the abstract
     */
    private String extractAbstract(JSONObject obj) {
        if (containsAndPresent(obj, "short-description")) {
            return (String) obj.get("short-description");
        }
        return "";
    }

    /**
     * Extracts the year of ORCID Works object.
     * ORCID has works without year as well, if it's not present we simply set the year to unknown.
     * @param pubDateObj
     * @return the year
     */
    private String extractYear(JSONObject pubDateObj) {
        if (present(pubDateObj)) {
            String year = this.getFieldIfPresent(pubDateObj, "year");
            if (present(year)) {
                return year;
            }
        }

        return "unknown";
    }


    /**
     * Filters the list of pair of name and role by the given role.
     * @param persons
     * @param role
     * @return the filtered list for the role
     */
    private String filterPersonsByRole(List<Pair<String, String>> persons, String role) {
        List<String> result = new ArrayList<>();
        for (Pair<String, String> person : persons) {
            if (person.getSecond().equals(role)) {
                result.add(person.getFirst());
            }
        }

        // No persons with given role found
        if (result.isEmpty()) {
            return "";
        }

        // Check, if multiple persons
        if (result.size() > 1) {
            return String.join(" and ", result);
        } else {
            return result.get(0);
        }

    }

    /**
     * Extracts all the persons and contributers of the ORCID Works object.
     * @param obj
     * @return the person list
     */
    private List<Pair<String, String>> extractPersons(JSONObject obj) {
        List<Pair<String, String>> persons = new ArrayList<>();

        JSONObject contributorsObj = (JSONObject) obj.get("contributors");
        JSONArray contributorArr = (JSONArray) contributorsObj.get("contributor");
        contributorArr.forEach(item -> {
            JSONObject personObj = (JSONObject) item;
            persons.add(getPersonWithRole(personObj));
        });

        return persons;
    }

    /**
     * Extracts the person name and role of the ORCID person object.
     * @param personObj
     * @return the person with role
     */
    private Pair<String, String> getPersonWithRole(JSONObject personObj) {
        String name = this.getValue((JSONObject) personObj.get("credit-name"));
        String role = "author";
        JSONObject attributes = (JSONObject) personObj.get("contributor-attributes");
        if (present(attributes) && attributes.containsKey("contributor-role")) {
            role = (String) attributes.get("contributor-role");
        }

        return new Pair<>(name, role);
    }

    private String getFirstSurname(final String s) {
        final int indexOfComma = s.indexOf(",");
        if (indexOfComma > 0) {
            return s.substring(0, indexOfComma);
        } else {
            String[] splits = s.split(" ");
            if (present(splits) && splits.length > 1) {
                return splits[1];
            } else {
                return s;
            }
        }
    }

    /**
     * Get the value of the field, if present. The method is necessary due to ORCID object having a nested value field.
     * @param obj
     * @param field
     * @return the field value
     */
    private String getFieldIfPresent(JSONObject obj, String field) {
        if (containsAndPresent(obj, field)) {
            return this.getValue((JSONObject) obj.get(field));
        }
        return "";
    }

    private String getValue(JSONObject obj) {
        if (present(obj)) {
            return (String) obj.get("value");
        }
        return "";
    }

    /**
     * Extract the valid BibTeX source of the ORCID Works object, if available.
     * @param obj
     * @return the BibTeX source
     */
    private String extractBibtexSource(JSONObject obj) {
        String bibtexSource = "";
        if (containsAndPresent(obj, "citation")) {
            JSONObject citationObj = (JSONObject) obj.get("citation");
            if (containsAndPresent(citationObj, "citation-type") && "bibtex".equals(citationObj.get("citation-type"))) {
                bibtexSource = (String) citationObj.get("citation-value");

                // Currently we have no option to validate bibtex strings outside of attempting to parse it
                final PostBibTeXParser parser = new PostBibTeXParser();
                parser.setDelimiter(null);
                parser.setWhitespace("_");
                parser.setTryParseAll(true);
                try {
                    parser.parseBibTeXPost(bibtexSource);
                } catch (bibtex.parser.ParseException | IOException e) {
                    return "";
                }
                if (present(parser.getCaughtExceptions()) || present(parser.getWarnings())) {
                    return "";
                }
            }
        }

        return bibtexSource;
    }

    /**
     * Check, if the JSON object has the key and the assigned value is not null.
     * @param obj
     * @param key
     * @return true, if key and value present
     */
    private boolean containsAndPresent(JSONObject obj, String key) {
        return obj.containsKey(key) && present(obj.get(key));
    }

    private String getBibTeX(final String key, final CharSequence value) {
        return "  " + key + " = {" + value + "},\n";
    }

    @Override
    public String toBibtex(String citation) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(citation);
            return this.toBibtex(obj);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
