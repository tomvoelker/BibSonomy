package de.unikassel.puma.openaccess.classification;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.unikassel.puma.openaccess.classification.chain.ClassificationTextChainElement;
import de.unikassel.puma.openaccess.classification.chain.ClassificationXMLChainElement;
import de.unikassel.puma.openaccess.classification.chain.parser.ACMClassification;
import de.unikassel.puma.openaccess.classification.chain.parser.DDCClassification;
import de.unikassel.puma.openaccess.classification.chain.parser.JELClassification;

public class PublicationClassificator {
	
	private static final Log log = LogFactory.getLog(PublicationClassificator.class);
	
	private final String xmlPath;
	
	private HashMap<String, Classification> classifications = new HashMap<String, Classification>();
	
	public PublicationClassificator(String xmlPath) {
		this.xmlPath = xmlPath;
		initialise();
	}
	
	public final List<PublicationClassification> getChildren(String classification, String name) {
		Classification c = classifications.get(classification);
		
		if(present(c)) {
			return c.getChildren(name);
		} else {
			return new ArrayList<PublicationClassification>();
		}
	}
	
	public Set<String> getAvailableClassifications() {
		return classifications.keySet();
	}
	
	public String getDescription(String classification, String name) {
		Classification c = classifications.get(classification);
		
		if(present(c)) {
			return c.getDescription(name);
		} else {
			return "";
		}	
	}
	
	private void initialise() {
		ArrayList<ClassificationSource> cceList = new ArrayList<ClassificationSource>();
		cceList.add(new ClassificationXMLChainElement(new JELClassification()));
		cceList.add(new ClassificationXMLChainElement(new ACMClassification()));
		cceList.add(new ClassificationTextChainElement(new DDCClassification()));
		
		
		File path = new File(xmlPath);
		
		if(path.isDirectory()) {
			
			File[] files = path.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					if(file.isDirectory()) {
						return false;
					}
					
					if(!file.toString().endsWith(".properties")) {
						return true;
					}
					
					return false;
				}
			});
			
			for(File f : files) {
				try {
					Classification c = null;
					
					for(int i = 0; i < cceList.size() && !present(c); ++i) {
						c = cceList.get(i).getClassification(f.toURI().toURL());
					}
					
					if(!present(c)) {
						log.error("Unable to parse " +f.getName());
						continue;
					}
					
					
					log.info("Found Classification " +c.getClassName());
					
					//try to read values from .properties file
					try {
						BufferedReader propertiesReader = new BufferedReader(new FileReader(f.getAbsolutePath().substring(0,f.getAbsolutePath().length()-4) +".properties"));
						String firstLine = propertiesReader.readLine();
						
						String[] name = firstLine.split(" ");
						
						if(name.length >= 2) {
							String n = name[1];
							for(int i = 2; i < name.length; ++i) {
								n += " " + name[i];
							}
							
							classifications.put(n, c);
						}
						
					} catch (FileNotFoundException e) {
						
						//no .properties file found, use the file name
						String fileName = f.getName().substring(0,f.getName().length()-4);
						classifications.put(fileName, c);
					}
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	public int getNumberOfClassifications() {
		return classifications.size();
	}

}
