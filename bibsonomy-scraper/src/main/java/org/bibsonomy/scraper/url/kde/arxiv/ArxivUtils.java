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
package org.bibsonomy.scraper.url.kde.arxiv;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bibsonomy.util.ValidationUtils.present;

public class ArxivUtils {

    protected static final String SITE_NAME = "arXiv";
    protected static final String SITE_URL = "https://arxiv.org/";
    protected static final String ARXIV_HOST = "arxiv.org";

    protected static final Pattern PATTERN_HOST = Pattern.compile(".*" + ARXIV_HOST);
    protected static final Pattern PATTERN_URL_ID = Pattern.compile("(abs|pdf)/(.+)");
    protected static final Pattern PATTERN_VER = Pattern.compile("(.+?)v\\d+");
    protected static final Pattern PATTERN_PDF_SUFFIX = Pattern.compile("\\.pdf");

    // for more info, see: https://arxiv.org/help/arxiv_identifier
    protected static final String ARXIV_STRICT_PREFIX = "arxiv:";
    protected static final String ARXIV_ID = "(\\d{4}\\.\\d+)";
    protected static final String ARXIV_ID_OLD = "(\\w+[\\.-]\\w+\\/\\d+)";
    protected static final Pattern PATTERN_ID = Pattern.compile("(arxiv:)?(" + ARXIV_ID + "|" + ARXIV_ID_OLD + ")", Pattern.CASE_INSENSITIVE);
    protected static final Pattern PATTERN_STRICT_ID = Pattern.compile("(arxiv:)(" + ARXIV_ID + "|" + ARXIV_ID_OLD + ")", Pattern.CASE_INSENSITIVE);

    public static boolean isArxivUrl(final String url) {
        try {
            return isArxivUrl(new URL(url));
        } catch (MalformedURLException e) {
            // ignored
        }
        return false;
    }

    public static boolean isArxivUrl(final URL url) {
        return present(url) && PATTERN_HOST.matcher(url.getHost()).find();
    }

    /**
     * Checks, whether the selection contains a DOI and is not too long (i.e.,
     * hopefully only contains the DOI and nothing else.
     *
     * @param selection
     * @return
     */
    public static boolean isSupportedSelection(final String selection) {
        return present(selection) && containsStrictArxivIdentifier(selection);
    }

    /**
     * check if the selection contains a strict arxiv identifier
     * example: arxiv:2106.123456
     *
     * need a strict one to not also match doi identifiers
     * @param selection
     * @return
     */
    protected static boolean containsStrictArxivIdentifier(final String selection) {
        return present(selection) && PATTERN_STRICT_ID.matcher(selection).find();
    }

    public static String extractArxivIdentifier(String selection) {
        // extract from URL
        if (isArxivUrl(selection)) {
            final Matcher matcherUrlID = PATTERN_URL_ID.matcher(selection);
            if (matcherUrlID.find()) {
                return normIdForOAI(matcherUrlID.group(2));
            }
        }

        // extract from selection, not using strict pattern here
        final Matcher matcherArxivID = PATTERN_ID.matcher(selection);
        if (matcherArxivID.find()) {
            return normIdForOAI(matcherArxivID.group(2));
        }

        return null;
    }

    public static String extractStrictArxivIdentifier(String selection) {
        if (isArxivUrl(selection)) {
            final Matcher matcherUrlID = PATTERN_URL_ID.matcher(selection);
            if (matcherUrlID.find()) {
                return ARXIV_STRICT_PREFIX + matcherUrlID.group(2);
            }
        }

        final Matcher matcherStrictID = PATTERN_STRICT_ID.matcher(selection);
        if (matcherStrictID.find()) {
            return ARXIV_STRICT_PREFIX + matcherStrictID.group(2);
        }

        return null;
    }

    /**
     * OAI interface supports only the notion of an arXiv article and not access to individual versions.
     * If an id is with version(eg. 1304.7984v1), the version part has to be removed(eg. 1304.7984).
     *
     * @param id
     * @return
     */
    private static String normIdForOAI(final String id) {
        final String vId;
        final Matcher verID = PATTERN_VER.matcher(id);
        if (verID.find()) {
            vId = verID.group(1);
        } else {
            vId = id;
        }

        return PATTERN_PDF_SUFFIX.matcher(vId).replaceAll("");
    }
}
