package org.bibsonomy.community.ranking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.importer.CSVReader;
import org.bibsonomy.community.importer.parser.DataInputParser;
import org.bibsonomy.community.importer.parser.DoubleDataInputParser;
import org.bibsonomy.community.importer.parser.IntegerDataInputParser;
import org.bibsonomy.community.importer.parser.StringDataInputParser;
import org.bibsonomy.community.util.PropertyLoader;
import org.bibsonomy.community.util.Triple;

/**
 * This class is a mess! But its only purpose is to put together informations 
 * scattered across various files generated with different libraries/programming 
 * languages.
 * 
 * So no beauty should be expected and the idea is that after we read in the
 * model into the database, everything should be clean - but beforehand any hack
 * is allowed.
 * 
 * @author fei
 *
 */
public class PopularityRanking extends CSVReader {
	public class CountComparator implements Comparator<Entry<String,Integer>> {

		public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
			if( o1==null ) {
				return -1;
			} else if( o2==null ) {
				return 1;
			} else {
				return o2.getValue().compareTo(o1.getValue());
			}
		}
		
	}
	
	private final static Log log = LogFactory.getLog(PopularityRanking.class);
	private static final int USR = 0;
	private static final int TAG = 1;
	private static final int RES = 2;

	private static final int NR_OF_TAGS      = 100;
	private static final int NR_OF_RESOURCES = 100; 
	
	/** maps user names to all containing communities */
	Map<String,Collection<Integer>> communities = new HashMap<String, Collection<Integer>>();
	/** maps communities to sorted set of ranked tags */
	Map<Integer,SortedSet<Entry<String,Integer>>> tagRankings      = new HashMap<Integer, SortedSet<Entry<String,Integer>>>();
	/** maps communities to sorted set of ranked resources */
	Map<Integer,SortedSet<Entry<String,Integer>>> resourceRankings = new HashMap<Integer, SortedSet<Entry<String,Integer>>>();
	/** maps communities to sorted set of ranked users */
	Map<Integer,SortedSet<Entry<String,Integer>>> userRankings     = new HashMap<Integer, SortedSet<Entry<String,Integer>>>();
	/** maps tags to corresponding counts per community */
	Map<Integer,Map<String,Integer>> tagsPopularityCounter     = new HashMap<Integer, Map<String,Integer>>();
	/** maps resources to corresponding counts per community */
	Map<Integer,Map<String,Integer>> resourcePopularityCounter = new HashMap<Integer, Map<String,Integer>>();
	/** maps users to corresponding counts per community */
	Map<Integer,Map<String,Integer>> userPopularityCounter     = new HashMap<Integer, Map<String,Integer>>();
	
	public static void main(String args[]) throws IOException {
		Properties config = PropertyLoader.openPropertyFile("community.properties");
		
		String clusteringFile       = config.getProperty("clusteringFile");
		String tasFile              = config.getProperty("tasFile");
		String userRankingsFile     = config.getProperty("userRankingFile");
		String tagRankingsFile      = config.getProperty("tagRankingFile");
		String resourceRankingsFile = config.getProperty("resourceRankingFile");
		
		PopularityRanking rankings = new PopularityRanking(clusteringFile, tasFile);
		rankings.rankItems();
		rankings.writeRanking(userRankingsFile, tagRankingsFile, resourceRankingsFile);
		
		rankings.getTagCount(0,"www");
		rankings.getResourceCount(0,"www");
	}
	
	public PopularityRanking(String clusteringFile, String tasFile) throws IOException {
		// temporary storage
		Collection<Triple<String,Integer,Double>> userClustering  = new ArrayList<Triple<String,Integer,Double>>();

		//--------------------------------------------------------------------
		// read in data
		//--------------------------------------------------------------------
		DataInputParser<String> stringParser   = new StringDataInputParser();
		DataInputParser<Integer> integerParser = new IntegerDataInputParser();
		DataInputParser<Double> doubleParser   = new DoubleDataInputParser();
		
		setDelimiter("\t");
		log.info("Loading community assignments...");
		loadFile(clusteringFile, 0, 1, 2, userClustering, stringParser, integerParser, doubleParser);
		
		//--------------------------------------------------------------------
		// build user clusters
		//--------------------------------------------------------------------
		buildUserClusters(userClustering, communities);

		//--------------------------------------------------------------------
		// calculate popularity counts
		//--------------------------------------------------------------------
		setDelimiter("\t");
		log.info("Processing tag assignments...");
		processTas(tasFile, communities, userPopularityCounter, tagsPopularityCounter, resourcePopularityCounter);
	}

	/**
	 * select top k items per dimension
	 */
	public void rankItems() {
		//--------------------------------------------------------------------
		// create rankings
		//--------------------------------------------------------------------
		// user rankings
		rankItems(userPopularityCounter, userRankings, NR_OF_TAGS);
		// tag rankings
		rankItems(tagsPopularityCounter, tagRankings, NR_OF_TAGS);
		// resource rankings
		rankItems(resourcePopularityCounter, resourceRankings, NR_OF_RESOURCES);		
	}
	
	/**
	 * write rankings to files
	 * 
	 * @param userRankingsFile 
	 * @param tagRankingsFile
	 * @param resourceRankingsFile
	 * @throws IOException
	 */
	public void writeRanking(String userRankingsFile, String tagRankingsFile, String resourceRankingsFile) throws IOException {
		writeRanking(userRankingsFile, userRankings);
		writeRanking(tagRankingsFile, tagRankings);
		writeRanking(resourceRankingsFile, resourceRankings);
	}
	

	public Integer getResourceCount(Integer communityId, String hash) {
		boolean testCondition = 
				resourcePopularityCounter.containsKey(communityId);
			testCondition = testCondition && 
				resourcePopularityCounter.get(communityId)!=null; 
			testCondition = testCondition && 
				resourcePopularityCounter.get(communityId).containsKey(hash);
		if( testCondition ) {
			return resourcePopularityCounter.get(communityId).get(hash);
		} else {
			return 0;
		}
	}

	public Integer getTagCount(Integer communityId, String tagName) {
		boolean testCondition = 
			tagsPopularityCounter.containsKey(communityId) && 
			tagsPopularityCounter.get(communityId)!=null && 
			tagsPopularityCounter.get(communityId).containsKey(tagName);
		if( testCondition ) {
			return tagsPopularityCounter.get(communityId).get(tagName);
		} else {
			return 0;
		}
	}




	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * build community structure from input file
	 * @param userClustering
	 * @param communities 
	 */
	private void buildUserClusters(Collection<Triple<String, Integer, Double>> userClustering, Map<String, Collection<Integer>> communities) {
		log.info("Building user clusters...");
		for( Triple<String,Integer,Double> entry : userClustering ) {
			String userName     = entry.getFirst();
			Integer communityId = entry.getSecond();
			Double ranking      = entry.getThird();
			
			Collection<Integer> userMemberships;
			// fill communitiy structure
			if( !communities.containsKey(communityId) ) {
				userMemberships = new TreeSet<Integer>();
				communities.put(userName, userMemberships);
			} else {
				userMemberships = communities.get(userName);
			}
			userMemberships.add(communityId);
		};
	}
	
	/**
	 * write given rankings to given output file
	 * @param rankingFile
	 * @param itemRankings
	 * @throws IOException
	 */
	private void writeRanking(String rankingFile, Map<Integer,SortedSet<Entry<String,Integer>>> itemRankings) throws IOException {
		//use buffering
		Writer rankingOutput      = new BufferedWriter(new FileWriter(rankingFile));
	    try {
	    	for( Entry<Integer,SortedSet<Entry<String,Integer>>> entry : itemRankings.entrySet() ) {
	    		Integer                      communityId = entry.getKey();
	    		SortedSet<Entry<String,Integer>> ranking = entry.getValue();
	    		
	    		for( Entry<String,Integer> item : ranking ) {
	    			rankingOutput.write(communityId + "\t" + item.getKey() + "\t" + item.getValue() + "\n");
	    		}
	    	}
	    }
	    finally {
	    	rankingOutput.close();
	    }

	}

	/**
	 * read tas line by line and adjust for each tas entry all corresponding community
	 * popularity counters
	 * 
	 * @param tasFile
	 * @param communities 
	 * @param userPopularityCounter 
	 * @param tagsPopularityCounter 
	 * @param resourcesPopularityCounter
	 * @throws IOException 
	 */
	private void processTas(String tasFile, Map<String,Collection<Integer>> communities, Map<Integer, Map<String, Integer>> userPopularityCounter, Map<Integer, Map<String, Integer>> tagsPopularityCounter, Map<Integer, Map<String, Integer>> resourcesPopularityCounter) throws IOException {
		BufferedReader input =  new BufferedReader(new FileReader(tasFile));
		
		String nextLine = null;
		while( (nextLine=input.readLine())!=null ) {
			String[] cells = nextLine.split(getDelimiter());
			String userName = cells[USR];
			String tagName  = cells[TAG];
			String resource = cells[RES];
			
			if( !communities.containsKey(userName) ) {
				log.warn("No cluster assignments for user " + userName);
				continue;
			}
			
			for( Integer communityId : communities.get(userName) ) {
				updateCounter(userPopularityCounter, userName, communityId);
				updateCounter(tagsPopularityCounter, tagName, communityId);
				updateCounter(resourcesPopularityCounter, resource, communityId);
			}
		}
	}

	private void updateCounter(Map<Integer, Map<String, Integer>> popularityCounter, String resource, Integer communityId) {
		Map<String, Integer> resourceCounts = popularityCounter.get(communityId);
		if( resourceCounts==null ) {
			resourceCounts = new HashMap<String, Integer>();
			popularityCounter.put(communityId, resourceCounts);
		}
		Integer oldCount = resourceCounts.get(resource);
		if( oldCount==null ) {
			resourceCounts.put(resource, 1);
		} else {
			resourceCounts.put(resource, oldCount+1);
		}
	}	
	

	/**
	 * sort given weighted items and limit size accordingly
	 * 
	 * @param resourcePopularityCounter
	 * @param itemRankings
	 */
	private void rankItems(Map<Integer, Map<String, Integer>> resourcePopularityCounter, Map<Integer,SortedSet<Entry<String,Integer>>> itemRankings, int sizeLimit) {
		for( Entry<Integer,Map<String,Integer>> entry : resourcePopularityCounter.entrySet() ) {
			Integer        communityId = entry.getKey();
			Map<String,Integer> counts = entry.getValue();
			
			SortedSet<Entry<String,Integer>> ranking = new TreeSet<Entry<String,Integer>>(new CountComparator());
			for( Entry<String,Integer> tagCount : counts.entrySet() ) {
				ranking.add(tagCount);
				if( ranking.size() > sizeLimit ) {
					Entry<String,Integer> test = ranking.last();
					ranking.remove(ranking.last());
				}
			}
			
			itemRankings.put(communityId, ranking);
		}
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------

}
