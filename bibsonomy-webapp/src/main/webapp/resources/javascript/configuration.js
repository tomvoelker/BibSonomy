/**
 * IMPORTANT: This script will contain configurations, that are commonly overwritten by PUMA instances.
 */

/**
 * Configuration for additional entrytypes
 */

var removedEntrytypes = [];
var extraEntrytypes = [];

/**
 * Configuration for edit publication fields
 */

var fields = ["booktitle", "journal", "volume", "number", "pages", "publisher", "address",
    "month", "day", "edition", "chapter", "key", "type", "annote", "note",
    "howpublished", "institution", "organization",
    "school", "series", "crossref", "misc"];

var inproceedingsField = ["publisher", "booktitle", "volume", "number", "series", "pages", "address", "month", "organization", "misc.language", "misc.DOI", "misc.ISBN", "misc.ISSN", "misc.eventdate", "misc.eventtitle", "misc.venue", "note"];

var requiredForType = {
    "article": ["misc.identifier", "misc.subjectarea", "journal", "volume", "number", "pages", "misc.publisher", "month", "misc.language", "misc.DOI", "misc.ISBN", "misc.ISSN", "note"],
    "book": ["misc.identifier", "misc.subjectarea", "publisher", "volume", "number", "series", "address", "edition", "month", "misc.language", "misc.DOI", "misc.ISBN", "note"],
    "booklet": ["misc.identifier", "misc.subjectarea", "howpublished", "address", "month", "misc.language", "misc.DOI", "misc.ISBN", "note"],
    "conference": inproceedingsField,
    "dataset": ["misc.identifier", "misc.subjectarea", "misc.DOI", "url"],
    "inbook": ["misc.identifier", "misc.subjectarea", "chapter", "pages", "publisher", "volume", "number", "series", "type", "address", "edition", "month", "misc.language", "misc.DOI", "misc.ISBN", "note"],
    "incollection": ["misc.identifier", "misc.subjectarea", "publisher", "booktitle", "volume", "number", "series", "type", "chapter", "pages", "address", "edition", "month", "misc.language", "misc.DOI", "misc.ISBN", "note"],
    "inproceedings": inproceedingsField,
    "manual": ["misc.identifier", "misc.subjectarea", "organization", "address", "edition", "month", "misc.language", "misc.DOI", "misc.ISBN", "note"],
    "masterthesis": ["misc.identifier", "misc.subjectarea", "school", "type", "address", "month", "misc.language", "misc.DOI", "misc.ISBN", "note"],
    "misc": ["misc.identifier", "misc.subjectarea", "howpublished", "month", "misc.language", "misc.DOI", "note"],
    "phdthesis": ["misc.identifier", "misc.subjectarea", "school", "address", "type", "month", "misc.language", "misc.DOI", "misc.ISBN", "note"],
    "proceedings": ["misc.identifier", "misc.subjectarea", "publisher", "volume", "number", "series", "address", "month", "misc.language", "misc.DOI", "misc.ISBN", "misc.eventdate", "misc.eventtitle", "misc.venue", "organization", "note"],
    "techreport": ["misc.identifier", "misc.subjectarea", "institution", "number", "type", "address", "month", "misc.language", "misc.DOI", "note"],
    "unpublished": ["misc.identifier", "misc.subjectarea", "misc.language", "misc.DOI", "misc.ISBN", "misc.ISSN", "misc.eventdate", "misc.eventtitle", "misc.venue", "note"],
    "periodical": ["misc.identifier", "misc.subjectarea", "misc.language", "misc.DOI", "misc.ISSN", "note"],
    "presentation": ["misc.identifier", "misc.subjectarea", "misc.language", "misc.eventdate", "misc.eventtitle", "misc.venue", "note"],
    "electronic": ["misc.identifier", "misc.subjectarea", "misc.repository", "misc.language", "misc.DOI", "note"]
}