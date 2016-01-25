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
package de.unikassel.puma.openaccess.classification.chain.parser;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import de.unikassel.puma.openaccess.classification.ClassificationObject;
import de.unikassel.puma.openaccess.classification.ClassificationTextParser;

/**
 * @author philipp
 */
public class DDCClassification extends ClassificationTextParser {

	private static final String NAME = "DDC";
	
	private int currentDepth = 0;
	
	@Override
	public void parse(final BufferedReader bf) throws IOException {
		this.classifications = new LinkedHashMap<String, ClassificationObject>();
		
		while(bf.ready()) {
			
			String line = bf.readLine();
			
			if(!present(line)) {
				this.currentDepth++;
				continue;
			}
			
			line = line.trim();
			final String [] lineArray = line.split(" ", 2);
			try {
				this.classificate(lineArray[0], lineArray[1]);
			} catch (final ArrayIndexOutOfBoundsException e) {
				//unable to parse
				this.classifications = null;
				return;
			}
		}
	}
	
	private void requClassificate(String name, final String description, final ClassificationObject object, final int current) {
		if(!present(name)) {
			return;
		}
		
		final String actual = name.charAt(0) +"";
		name = name.substring(1);
		
		if(current >= this.currentDepth) {
			if(object.getChildren().containsKey(actual)) {
				object.getChildren().get(actual).setDescription(description);
				this.requClassificate(name, description, object.getChildren().get(actual), current +1);
			
			} else {
				if(name.isEmpty()) {
					final ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					
				} else {
					final ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					this.requClassificate(name, description, co, current +1);
				}
			}
		} else {
			
			if(object.getChildren().containsKey(actual)) {
				this.requClassificate(name, description, object.getChildren().get(actual), current +1);
			
			} else {
				if(name.isEmpty()) {
					final ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					
				} else {
					final ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					this.requClassificate(name, description, co, current +1);
				}
			}
			
		}
	}
	

	private void classificate(String name, final String description) {
		final String actual = name.charAt(0) +"";
		name = name.substring(1);
	
		if(this.classifications.containsKey(actual)) {
			this.requClassificate(name, description, this.classifications.get(actual), 1);
		} else {
			final ClassificationObject co = new ClassificationObject(actual, description);
			this.classifications.put(actual, co);
			this.requClassificate(name, description, co, 1);
		}
	}
	
	@Override
	public String getDelimiter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Map<String, ClassificationObject> getList() {
		return this.classifications;
	}

}
