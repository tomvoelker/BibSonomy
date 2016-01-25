/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package de.unikassel.puma.openaccess.classification.chain;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import de.unikassel.puma.openaccess.classification.Classification;
import de.unikassel.puma.openaccess.classification.ClassificationSource;
import de.unikassel.puma.openaccess.classification.ClassificationTextParser;

/**
 * @author philipp
 */
public class ClassificationTextChainElement implements ClassificationSource {

	private final ClassificationTextParser classificationParser;
	
	private ClassificationSource next = null;

	/**
	 * constructor to set the parser
	 * @param cParser
	 */
	public ClassificationTextChainElement(final ClassificationTextParser cParser) {
		this.classificationParser = cParser;
	}
	
	/**
	 * @param next the next classification source to set
	 */
	public void setNext(final ClassificationSource next) {
		this.next = next;
	}
	
	@Override
	public Classification getClassification(final URL url) throws IOException {
		final BufferedReader in =
			new BufferedReader(new FileReader(url.getPath()));
		
		this.classificationParser.parse(in);
		
		if (!present(this.classificationParser.getList())) {
			if (!present(this.next)) {
				return null;
			}
			
			return this.next.getClassification(url);
		}
		
		return new Classification(this.classificationParser.getName(), this.classificationParser.getList(), this.classificationParser.getDelimiter());
	}
}
