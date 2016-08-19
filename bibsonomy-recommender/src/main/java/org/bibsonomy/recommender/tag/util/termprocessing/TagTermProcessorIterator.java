/**
 * BibSonomy Recommendation - Tag and resource recommender.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.recommender.tag.util.termprocessing;

import java.util.Iterator;

import org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator;
import org.bibsonomy.util.TagStringUtils;

/**
 * extends {@link TermProcessingIterator} by the
 * cleanTags function from the Discovery Challenge (e.g., remove everything
 * but letters and numbers). 
 *
 * @author jil, dzo
 */
public class TagTermProcessorIterator extends TermProcessingIterator {

	/**
	 * @param words
	 */
	public TagTermProcessorIterator(Iterator<String> words) {
		super(words);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator#cleanWord(java.lang.String)
	 */
	@Override
	protected String cleanWord(String word) {
		return TagStringUtils.cleanTag(word);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.util.termprocessing.TermProcessingIterator#acceptWord(java.lang.String)
	 */
	@Override
	protected boolean acceptsWord(String word) {
		return super.acceptsWord(word) && !TagStringUtils.isIgnoreTag(word);
	}

}
