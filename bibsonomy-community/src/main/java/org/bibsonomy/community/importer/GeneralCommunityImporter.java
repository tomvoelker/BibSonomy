package org.bibsonomy.community.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.algorithm.MockAlgorithm;
import org.bibsonomy.community.importer.parser.DataInputParser;
import org.bibsonomy.community.importer.parser.DoubleDataInputParser;
import org.bibsonomy.community.importer.parser.IntegerDataInputParser;
import org.bibsonomy.community.importer.parser.StringDataInputParser;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.ranking.PopularityRanking;
import org.bibsonomy.community.util.Triple;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

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
public class GeneralCommunityImporter extends CSVImporter {
	private final static Log log = LogFactory.getLog(GeneralCommunityImporter.class); 
	
	private Collection<Cluster<User>>                     communities = new ArrayList<Cluster<User>>();;
	private Collection<Cluster<Post<? extends Resource>>> resources   = new ArrayList<Cluster<Post<? extends Resource>>>();
	private Collection<Cluster<Tag>>                      topics      = new ArrayList<Cluster<Tag>>();
	
	private Integer clusterCount = -1;
	private Integer topicCount   = -1;
	
	private Algorithm algorithm;
	
	/** used for counting item occurrences per community */
	PopularityRanking itemCounter;
	
	public GeneralCommunityImporter(String algorithmName, String algorithmMeta, String clusteringFile, String tasFile, String userRankingFile, String tagRankingFile, String resourceRankingFile) throws IOException {
		// temporary storage
		Collection<Triple<String,Integer,Double>> userClustering  = new ArrayList<Triple<String,Integer,Double>>();
		Collection<Triple<Integer,String,Double>> userRanking     = new ArrayList<Triple<Integer,String,Double>>();
		Collection<Triple<Integer,String,Double>> tagRanking      = new ArrayList<Triple<Integer,String,Double>>();
		Collection<Triple<Integer,String,Double>> resourceRanking = new ArrayList<Triple<Integer,String,Double>>();
		
		//--------------------------------------------------------------------
		// read in data
		//--------------------------------------------------------------------
		DataInputParser<String> stringParser   = new StringDataInputParser();
		DataInputParser<Integer> integerParser = new IntegerDataInputParser();
		DataInputParser<Double> doubleParser   = new DoubleDataInputParser();
		
		setDelimiter("\t");
		log.info("Loading community assignments...");
		loadFile(clusteringFile, 0, 1, 2, userClustering, stringParser, integerParser, doubleParser);
		setDelimiter("\t");
		log.info("Loading community user ranking...");
		loadFile(userRankingFile, 0, 1, 2, userRanking, integerParser, stringParser, doubleParser);
		log.info("Loading community tag ranking...");
		loadFile(tagRankingFile, 0, 1, 2, tagRanking, integerParser, stringParser, doubleParser);
		log.info("Loading community resource ranking...");
		loadFile(resourceRankingFile, 0, 1, 2, resourceRanking, integerParser, stringParser, doubleParser);
		
		//--------------------------------------------------------------------
		// build user clusters
		//--------------------------------------------------------------------
		//buildUserClusters(userClustering, userRanking);
		buildUserClusters(userClustering, userRanking);

		//--------------------------------------------------------------------
		// build resource clusters
		//--------------------------------------------------------------------
		buildResourceClusters(resourceRanking);
		
		//--------------------------------------------------------------------
		// count item occurrences per community
		//--------------------------------------------------------------------
		itemCounter = new PopularityRanking(clusteringFile, tasFile);

		//--------------------------------------------------------------------
		// build tag clusters
		//--------------------------------------------------------------------
		buildTagClusters(tagRanking);

		this.algorithm = new MockAlgorithm(algorithmName, algorithmMeta);
	}


	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * build community structure from input file
	 * @param tagRanking
	 */
	private void buildTagClusters(Collection<Triple<Integer, String, Double>> tagRanking) {
		log.info("Building tag clusters...");
		// collect communities
		Map<Integer, Cluster<Tag>> topicMap = new HashMap<Integer, Cluster<Tag>>();
		for( Triple<Integer,String,Double> entry : tagRanking ) {
			Integer communityId = entry.getFirst();
			String tagName      = entry.getSecond();
			Double ranking      = entry.getThird();
			// no topics!!!
			Integer topicId   = 0;

			Tag tag = new Tag(tagName);
			tag.setTopicId(topicId);
			tag.setGlobalcount(itemCounter.getTagCount(communityId, tagName));
			tag.setWeight(ranking);
			
			// assign tag to each of user's clusters
			Cluster<Tag> topic = topicMap.get(communityId);
			if( topic == null ) {
				topic = new Cluster<Tag>();
				topic.setInstances(new ArrayList<Tag>());
				topic.setClusterID(communityId);
				topicMap.put(communityId, topic);
			};
			topic.getInstances().add(tag);
		};
		this.topicCount   = 1;
		tagRanking.clear();
		// build community structure
		for( Map.Entry<Integer, Cluster<Tag>> entry : topicMap.entrySet() ) {
			topics.add(entry.getValue());
		}
		topicMap.clear();
	}

	/**
	 * build community structure from input file
	 * @param resourceRanking
	 */
	private void buildResourceClusters(Collection<Triple<Integer, String, Double>> resourceRanking) {
		// collect communities
		Map<Integer, Cluster<Post<? extends Resource>>> resourceMap = new HashMap<Integer, Cluster<Post<? extends Resource>>>();
		log.info("Building resource clusters...");
		for( Triple<Integer,String,Double> entry : resourceRanking ) {
			Integer communityId = entry.getFirst();
			String hash         = entry.getSecond();
			Double ranking      = entry.getThird();
			
			Integer contentType;
			try {
				contentType = Integer.parseInt(hash.substring(0, 1));
				hash = hash.substring(1);
			} catch( NumberFormatException e ) {
				log.error("Error determing content type for resource " + hash);
				continue;
			}
			
			// create resource object
			Resource resource;
			switch (contentType) {
			case 1:
				resource = new Bookmark();
				resource.setIntraHash(hash);
				break;
			case 2:
				resource = new BibTex();
				resource.setInterHash(hash);
				break;
			default:
				continue;
			}
			
			// create post object
			Post<Resource> post = new Post<Resource>();
			post.setContentType(contentType);
			post.setResource(resource);
			post.setWeight(ranking);
			
			Cluster<Post<? extends Resource>> resources = resourceMap.get(communityId);
			if( resources == null ) {
				resources = new Cluster<Post<? extends Resource>>();
				resources.setInstances(new ArrayList<Post<? extends Resource>>());
				resources.setClusterID(communityId);
				resourceMap.put(communityId, resources);
			};
			resources.getInstances().add(post);
		};
		resourceRanking.clear();
		// build community structure
		for( Map.Entry<Integer, Cluster<Post<? extends Resource>>> entry : resourceMap.entrySet() ) {
			resources.add(entry.getValue());
		}
		resourceMap.clear();
	}

	/**
	 * build community structure from input file
	 * @param userClustering2 
	 * @param userClustering
	 */
	private void buildUserClusters(Collection<Triple<String, Integer, Double>> userClustering, Collection<Triple<Integer, String, Double>> userRanking) {
		log.info("Parsing user ranking...");
		Map<Integer, Map<String,Double>> rankingMap = new HashMap<Integer, Map<String,Double>>();
		for( Triple<Integer,String,Double> entry : userRanking ) {
			Integer communityId = entry.getFirst();
			String     userName = entry.getSecond();
			Double      ranking = entry.getThird();
			
			Map<String,Double> communityRank = rankingMap.get(communityId);
			if( communityRank==null ) {
				communityRank = new HashMap<String, Double>();
				rankingMap.put(communityId, communityRank);
			}
			communityRank.put(userName, ranking);
		}
		
		log.info("Building user clusters...");
		// collect communities
		Map<Integer, Cluster<User>> communityMap = new HashMap<Integer, Cluster<User>>();
		for( Triple<String, Integer, Double> entry : userClustering ) {
			String userName     = entry.getFirst();
			Integer communityId = entry.getSecond();
			Double relevance = entry.getThird();
			
			// get ranking
			Double ranking = 0.0; // relevance;
			if( rankingMap.containsKey(communityId) && rankingMap.get(communityId).containsKey(userName) ) {
				ranking = rankingMap.get(communityId).get(userName);
			};

			// create user
			User user = new User(userName);
			user.setWeight(ranking);
					
			Cluster<User> community;
			// fill communitiy structure
			if( !communityMap.containsKey(communityId) ) {
				community = new Cluster<User>();
				community.setInstances(new ArrayList<User>());
				community.setClusterID(communityId);
				communityMap.put(communityId, community);
			} else {
				community = communityMap.get(communityId);
			}
			community.getInstances().add(user);
		};
		this.clusterCount = communityMap.keySet().size(); 
		userClustering.clear();
		// build community structure
		for( Map.Entry<Integer, Cluster<User>> entry : communityMap.entrySet() ) {
			communities.add(entry.getValue());
		}
		communityMap.clear();
	}

	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public int getClusterCount() {
		return this.clusterCount;
	}

	public int getTopicCount() {
		return this.topicCount;
	}

	public Collection<Cluster<User>> getCommunities() {
		return this.communities;
	}

	public Collection<Cluster<Post<? extends Resource>>> getResources() {
		return this.resources;
	}

	public Collection<Cluster<Tag>> getTopics() {
		return this.topics;
	}

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}
}
