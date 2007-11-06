package org.bibsonomy.importer.event.iswc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.bibsonomy.importer.event.iswc.exceptions.RepositoryException;
import org.bibsonomy.importer.event.iswc.rdf.RDFRepository;

/**
 * With this class you can build an tag cloud, which counts every tag
 * and prints the results to a file.
 * @author tst
 *
 */
public class TagCloudBuilder {

	/**
	 * Build the tag cloud and print it into file tag_cloud.txt
	 * @param args first param must be the path to the rdf file
	 */
	public static void main(String[] args) {
		try {

			// init parameters
			String rdfPath = null;
			rdfPath = args[0];
			
			// map which use the tagname as key and counts the Frequency of the tag as its value
			HashMap<String, Integer> tagMap = new HashMap<String, Integer>();
			// read rdf file and build the repository
			RDFRepository repository = new RDFRepository(rdfPath);
			
			// extract topics from RDF and put it into the tagMap
			Map<String, String> keywords = repository.getKeywords();
			for(String keywordString: keywords.values()){
				StringTokenizer tokenizer = new StringTokenizer(keywordString);
				while(tokenizer.hasMoreTokens()){
					String tag = tokenizer.nextToken();
					
					// put new tag or incerement old tag
					if(tagMap.containsKey(tag))
						tagMap.put(tag, tagMap.get(tag).intValue()+1);
					else
						tagMap.put(tag, 1);
				}
			}
			
			// extract sessions from RDF and put it into tagMap
			Map<String, String> sessions = repository.getSessions();
			for(String sessionString: sessions.values()){
				StringTokenizer tokenizer = new StringTokenizer(sessionString);
				while(tokenizer.hasMoreTokens()){
					String tag = tokenizer.nextToken();
					
					// put new tag or incerement old tag
					if(tagMap.containsKey(tag))
						tagMap.put(tag, tagMap.get(tag).intValue()+1);
					else
						tagMap.put(tag, 1);
				}
			}
			
			// write file tag_cloud.txt
			FileWriter writer = new FileWriter("tag_cloud.txt");
			for(String tag: tagMap.keySet()){
				writer.write(tag + " " + tagMap.get(tag) + "\n");
			}
			
			// cleanup
			writer.flush();
			writer.close();
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
