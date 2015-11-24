/**
 * BibSonomy-Lucene - Fulltext search facility of BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.lucene.util;


/**
 * this class is a temporary hack for collecting all constants which should be consistent 
 * throughout the module
 * 
 *  FIXME: this should be consistent with the spring configuration
 *  
 * @author fei
 */
@Deprecated // TODO: remove Lucene
public class LuceneBase {
	/** TODO: improve documentation */
	public static final String PARAM_RELEVANCE = "relevance";

	/** TODO: improve documentation */
	public static final String CFG_LUCENENAME = "luceneName";
	/** TODO: improve documentation */
	public static final String CFG_ANALYZER = "fieldAnalyzer";
	/** TODO: improve documentation */
	public static final String CFG_TYPEHANDLER = "typeHandler";
	/** TODO: improve documentation */
	public static final String CFG_LIST_DELIMITER = " ";
	/** TODO: improve documentation */
	public static final String CFG_FLDINDEX = "luceneIndex";
	/** TODO: improve documentation */
	public static final String CFG_FLDSTORE = "luceneStore";
	/** TODO: improve documentation */
	public static final String CFG_FULLTEXT_FLAG = "fulltextSearch";
	/** TODO: improve documentation */
	public static final String CFG_PRIVATE_FLAG = "privateSearch";
	/** delimiter to specify which field to search for */
	public static final String CFG_LUCENE_FIELD_SPECIFIER = ":";
}
