/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
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
package de.unikassel.puma.openaccess.classification;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.unikassel.puma.openaccess.classification.chain.ClassificationTextChainElement;
import de.unikassel.puma.openaccess.classification.chain.ClassificationXMLChainElement;
import de.unikassel.puma.openaccess.classification.chain.parser.ACMClassification;
import de.unikassel.puma.openaccess.classification.chain.parser.DDCClassification;
import de.unikassel.puma.openaccess.classification.chain.parser.JELClassification;

/**
 * @author philipp
 */
public class PublicationClassificator {
	private static final Log log = LogFactory.getLog(PublicationClassificator.class);
	
	private final Map<org.bibsonomy.model.Classification, Classification> classifications = new HashMap<org.bibsonomy.model.Classification, Classification>();
	
	private Classification getClassificationByName(final String name) {
		for (final org.bibsonomy.model.Classification c : this.classifications.keySet()) {
			if (c.getName().equals(name)) {
				return this.classifications.get(c);
			}
		}
		
		return null;
	}
	
	public PublicationClassificator() {
		this.initialise();
	}
	
	public final List<PublicationClassification> getChildren(final String classification, final String name) {
		final Classification c = this.getClassificationByName(classification);
		if (present(c)) {
			return c.getChildren(name);
		}
		return new ArrayList<PublicationClassification>();
	}
	
	public Set<org.bibsonomy.model.Classification> getAvailableClassifications() {
		return this.classifications.keySet();
	}
	
	public String getDescription(final String classification, final String name) {
		final Classification c = this.getClassificationByName(classification);
		
		if (present(c)) {
			return c.getDescription(name);
		}
		
		return "";
	}
	
	private void initialise() {
		final List<ClassificationSource> cceList = new ArrayList<ClassificationSource>();
		cceList.add(new ClassificationXMLChainElement(new JELClassification()));
		cceList.add(new ClassificationXMLChainElement(new ACMClassification()));
		cceList.add(new ClassificationTextChainElement(new DDCClassification()));
		
		
		final String absolutePath = PublicationClassificator.class.getClassLoader().getResource("classifications").toExternalForm();
		final File path = new File(absolutePath);
		
		if (path.isDirectory()) {
			final File[] files = path.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(final File file) {
					if (file.isDirectory()) {
						return false;
					}
					
					if (!file.toString().endsWith(".properties")) {
						return true;
					}
					
					return false;
				}
			});
			
			for (final File f : files) {
				try {
					Classification c = null;
					
					for (int i = 0; (i < cceList.size()) && !present(c); ++i) {
						c = cceList.get(i).getClassification(f.toURI().toURL());
					}
					
					if (!present(c)) {
						log.error("Unable to parse " + f.getName());
						continue;
					}
					
					log.info("Found Classification " + c.getClassName());
					
					//try to read values from .properties file
					try {
						final Properties properties = new Properties();
						final org.bibsonomy.model.Classification classification = new org.bibsonomy.model.Classification();
						
						properties.load(new FileReader(f.getAbsolutePath().substring(0,f.getAbsolutePath().length() - 4) + ".properties"));
						
						classification.setName(properties.getProperty("name"));
						classification.setDesc(properties.getProperty("desc"));
						classification.setUrl(properties.getProperty("url"));
						
						this.classifications.put(classification, c);
						
					} catch (final IOException e) {
						//no .properties file found, use the file name
						final org.bibsonomy.model.Classification classification = new org.bibsonomy.model.Classification();
						classification.setName(f.getName().substring(0,f.getName().length()-4));
						this.classifications.put(classification, c);
					}
				} catch (final MalformedURLException e) {
					log.error("error converting file url", e);
				} catch (final IOException e) {
					log.error("error loading classification", e);
				}
			}
		}
	}
	
	public int getNumberOfClassifications() {
		return this.classifications.size();
	}

}
