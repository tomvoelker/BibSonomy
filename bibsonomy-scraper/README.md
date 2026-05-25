# BibSonomy Scraper

## What is it?

The BibSonomy scrapers facilitate the extraction of bibliographic
metadata from web pages. The receive as input a URL or a short piece
of text and try to find the metadata of the publication described at
the URL or within the text snippet. The output is provided as a
[BibTeX entry](https://en.wikipedia.org/wiki/BibTeX) which can be
parsed using the [BibTeX parser](../bibsonomy-bibtex-parser).

The module is part of the [BibSonomy](https://www.bibsonomy.org) social bookmarking system and is maintained by
the [Data Science Chair](https://www.informatik.uni-wuerzburg.de/datascience/home/) at the University of Würzburg, Germany,
the [Information Processing and Analytics Group](https://www.ibi.hu-berlin.de/en/research/Information-processing/) at the Humboldt-Universität zu Berlin, Germany,
the [Knowledge & Data Engineering Group](https://www.kde.cs.uni-kassel.de/) at the University of Kassel, Germany, and
the [L3S Research Center](https://www.l3s.de/) at Leibniz University Hannover, Germany.

## Documentation

The
[BibSonomy wiki page](https://bitbucket.org/bibsonomy/bibsonomy/wiki/development/modules/scraper/Scraper)
provides a good overview on the structure and functionality of the
scrapers. More documentation is included in the form of JavaDoc
annotations in the source code. A
[list of active scrapers](https://www.bibsonomy.org/scraperinfo) is
provided in BibSonomy. The scrapers can also be tested using
BibSonomy's [scraping service](http://scraper.bibsonomy.org/).

## Zotero Translation Server Migration

BibSonomy can use a self-hosted
[Zotero translation-server](https://github.com/zotero/translation-server)
as the primary URL and identifier scraper. Configure it with:

* `bibsonomy.scraper.zotero.url` or `BIBSONOMY_SCRAPER_ZOTERO_URL`
* `bibsonomy.scraper.zotero.enabled` or `BIBSONOMY_SCRAPER_ZOTERO_ENABLED`
* `bibsonomy.scraper.legacyFallback.enabled` or `BIBSONOMY_SCRAPER_LEGACY_FALLBACK_ENABLED`

The production service URL is expected to be
`http://bibsonomy-zotero-translation-server.extsonomy.svc.cluster.local:1969`.
Zotero is enabled by default when the URL is set. Legacy fallback remains
enabled by default during rollout.

The Zotero scraper calls `/web`, `/search`, and `/export?format=bibtex`.
For HTTP 300 multiple-choice responses from `/web`, it posts the complete
choice response back to Zotero and exports all returned items. Bulk import
therefore receives all BibTeX entries. The single-publication edit flow keeps
the existing parser behavior and uses the first parsed entry; it also records a
warning that a chooser UI is still missing.

Deletion candidates after a stable rollout:

* URL-specific scraper packages currently collected by `KDEUrlCompositeScraper`
* legacy generic metadata scrapers replaced by Zotero
* `ReferencesScraper`, `CitedbyScraper`, and their implementations if no active
  consumer remains
* old scraper info page behavior that lists the legacy chain as active runtime
  behavior

Converters in this module are retained. EndNote/RIS/ORCID and related import
flows still use them and should move to a smaller importer/converter module in
a separate PR before any scraper-module deletion.


## Release Notes

Please see the [release log](https://bitbucket.org/bibsonomy/bibsonomy/wiki/documentation/releases/Release%20Log).


## System Requirements

* JDK: 1.7 or above.
* Memory: No minimum requirement.
* Disk: No minimum requirement.
* Operating System: No minimum requirement.

## Licensing

* Please see the file [LICENSE.txt](https://bitbucket.org/bibsonomy/bibsonomy/src/tip/bibsonomy-scraper/LICENSE.txt?at=stable)


## Maven URLS

* [Home Page](https://bitbucket.org/bibsonomy/bibsonomy)
* [Maven Repository](http://dev.bibsonomy.org/maven2/)
* [Issue Tracking](https://bitbucket.org/bibsonomy/bibsonomy/issues)
