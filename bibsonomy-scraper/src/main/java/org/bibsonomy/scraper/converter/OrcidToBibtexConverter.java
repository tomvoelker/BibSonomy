package org.bibsonomy.scraper.converter;


import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
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
        this.fieldMap.put("abstract", "abstract");
        this.fieldMap.put("pages", "pages");
        this.fieldMap.put("page", "pages");
        this.fieldMap.put("publisher-place", "address");
        this.fieldMap.put("event-place", "location");
        this.fieldMap.put("url", "url");
        this.fieldMap.put("website", "url");
        this.fieldMap.put("keyword", "keywords");
    }

    public String toBibtex(JSONObject citation) {
        if (this.checkForBibtexSource(citation)) {
            return this.extractBibtexSource(citation);
        }

        final List<Pair<String, String>> persons = extractPersons(citation);

        final String authors = filterPersonsByRole(persons, "author");
        final String editors = filterPersonsByRole(persons, "editor");

        final JSONObject pubDateObj = (JSONObject) citation.get("publication-date");
        final String year = extractYear(pubDateObj);
        final String month = extractMonth(pubDateObj);
        final String day = extractDay(pubDateObj);

        final String citationKey = getBibtexKey(authors, editors, year);
        final String entrytype = getEntrytype(citation);

        final StringBuilder builder = new StringBuilder("@");
        builder.append(entrytype).append("{").append(citationKey).append(",\n");

        final String title = extractTitle(citation);
        if (present(title)) {
            builder.append(getBibTeX("title", title));
        }

        final String journal = extractJournal(citation);
        if (present(journal)) {
            builder.append(getBibTeX("journal", journal));
        }

        if (present(authors)) {
            builder.append(getBibTeX("author", authors));
        }

        if (present(editors)) {
            builder.append(getBibTeX("editor", editors));
        }

        if (present(year)) {
            builder.append(getBibTeX("year", year));
            if (present(month)) {
                builder.append(getBibTeX("month", month));
                if (present(day)) {
                    builder.append(getBibTeX("day", day));
                }
            }
        }

        // Apply general fields, such as abstract, URL, etc.
        this.applyMapping(citation, this.fieldMap, builder);

        // Apply external IDs, such as DOI, ISBN, etc.
        JSONObject externalIds = (JSONObject) citation.get("external-ids");
        if (present(externalIds)) {
            this.applyExternalIds((JSONArray) externalIds.get("external-id"), builder);
        }

        builder.append("}");
        return builder.toString();
    }

    private void applyMapping(final JSONObject citation, final Map<String, String> mapping, final StringBuilder builder) {
        mapping.forEach((key, field) -> {
            if (citation.containsKey(key)) {
                final String valueToCopy = this.getValue((JSONObject) citation.get(key));
                builder.append(getBibTeX(field, valueToCopy));
            }
        });
    }

    private void applyExternalIds(final JSONArray externalIds, final StringBuilder builder) {
        externalIds.forEach(item -> {
            JSONObject extIdObj = (JSONObject) item;
            String type = (String) extIdObj.get("external-id-type");
            String value = (String) extIdObj.get("external-id-value");
            builder.append(getBibTeX(type, value));
        });
    }

    private String getBibtexKey(String authors, String editors, String year) {
        final String name;
        if (authors.length() > 0) {
            name = getFirstSurname(authors);
        } else if (editors.length() > 0) {
            name = getFirstSurname(editors);
        } else {
            name = "";
        }
        String key = name + year;
        return key.replaceAll("\\s+","");
    }

    private String getEntrytype(JSONObject obj) {
        String type = "";
        if (obj.containsKey("type")) {
            type = (String) obj.get("type");
        }

        return this.entrytypeMap.getOrDefault(type, "misc");
    }

    private String extractTitle(JSONObject obj) {
        if (obj.containsKey("title")) {
            JSONObject titleGroup = (JSONObject) obj.get("title");
            return this.getValue((JSONObject) titleGroup.get("title"));
        }
        return "";
    }

    private String extractJournal(JSONObject obj) {
        return this.getFieldIfPresent(obj, "journal-title");
    }

    private String extractYear(JSONObject pubDateObj) {
        return this.getFieldIfPresent(pubDateObj, "year");
    }

    private String extractMonth(JSONObject pubDateObj) {
        return this.getFieldIfPresent(pubDateObj, "month");
    }

    private String extractDay(JSONObject pubDateObj) {
        return this.getFieldIfPresent(pubDateObj, "day");
    }

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

    private String getFieldIfPresent(JSONObject obj, String field) {
        if (obj.containsKey(field)) {
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

    private boolean checkForBibtexSource(JSONObject obj) {
        if (obj.containsKey("citation")) {
            JSONObject citationObj = (JSONObject) obj.get("citation");
            if (present(citationObj) && "bibtex".equals(citationObj.get("citation-type"))) {
                return true;
            }
        }
        return false;
    }

    private String extractBibtexSource(JSONObject obj) {
        if (obj.containsKey("citation")) {
            JSONObject citationObj = (JSONObject) obj.get("citation");
            if ("bibtex".equals(citationObj.get("citation-type"))) {
                return (String) citationObj.get("citation-value");
            }
        }
        return "";
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
