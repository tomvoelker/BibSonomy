package org.bibsonomy.community.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.algorithm.Algorithm;
import org.bibsonomy.community.algorithm.MockAlgorithm;
import org.bibsonomy.community.importer.parser.DataInputParser;
import org.bibsonomy.community.importer.parser.DoubleDataInputParser;
import org.bibsonomy.community.importer.parser.IntegerDataInputParser;
import org.bibsonomy.community.importer.parser.StringDataInputParser;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.util.Pair;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;

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
public class LDAkMeansImporter extends CSVImporter {
	private final static Log log = LogFactory.getLog(LDAkMeansImporter.class); 
	
	private Map<Integer,String> userIdMap = new HashMap<Integer, String>();
	private Map<Integer,String> resourceHashMap = new HashMap<Integer, String>();
	private Map<Integer,Integer> resourceTypeMap = new HashMap<Integer, Integer>();
	
	private Collection<Cluster<User>> communities = new ArrayList<Cluster<User>>();;
	private Collection<Cluster<Post<? extends Resource>>> resources = new ArrayList<Cluster<Post<? extends Resource>>>();
	private Collection<Cluster<Tag>> topics = new ArrayList<Cluster<Tag>>();
	
	private Integer clusterCount = -1;
	private Integer topicCount   = -1;
	
	private Algorithm algorithm;
	
	public LDAkMeansImporter(String userMapFile, String contentIDsFile, String clusteringFile, String resouceFile, String topicFile) throws IOException {
		Collection<Pair<Integer, Integer>> userClustering         = new ArrayList<Pair<Integer,Integer>>();
		
		Map<Integer, Cluster<User>> userMap = new HashMap<Integer, Cluster<User>>();
		Map<Integer, Collection<Integer>> user2Clusters = new HashMap<Integer, Collection<Integer>>();
		Map<Integer, Cluster<Post<? extends Resource>>> resourceMap = new HashMap<Integer, Cluster<Post<? extends Resource>>>();
		Map<Integer, Cluster<Tag>> topicMap = new HashMap<Integer, Cluster<Tag>>();

		Set<Integer> topicsCache = new HashSet<Integer>();
		Set<Integer> communCache = new HashSet<Integer>();

		// count number of occurrences for each resource
		Map<String,Integer> resourceCount  = new HashMap<String, Integer>();
		// calculate number of clusters containing given resource
		Map<String,Map<Integer,Integer>> clusterResourceCount  = new HashMap<String, Map<Integer,Integer>>();
		// calculate frequency of given resource in given cluster
		Map<Integer,Map<String,Integer>> clusterResourceFrequency = new HashMap<Integer, Map<String,Integer>>();
		
		// count number of occurrences for each resource
		Map<String,Integer> tagCount  = new HashMap<String, Integer>();
		// calculate number of clusters containing given resource
		Map<String,Map<Integer,Integer>> clusterTagCount  = new HashMap<String, Map<Integer,Integer>>();
		// calculate frequency of given resource in given cluster
		Map<Integer,Map<String,Integer>> clusterTagFrequency = new HashMap<Integer, Map<String,Integer>>();
		
		//--------------------------------------------------------------------
		// read in data
		//--------------------------------------------------------------------
		DataInputParser<String> stringParser   = new StringDataInputParser();
		DataInputParser<Integer> integerParser = new IntegerDataInputParser();
		DataInputParser<Double> doubleParser   = new DoubleDataInputParser();
		
		Collection<Pair<Integer, String>>  userList     = new ArrayList<Pair<Integer,String>>();
		
		log.info("Reading usernames...");
		loadFile(userMapFile, userList, integerParser, stringParser);
		setDelimiter(" ");
		log.info("Loading community assignments...");
		loadFile(clusteringFile, userClustering, integerParser, integerParser);
		setDelimiter("\t");
		log.info("Loading community resource assignments...");
		Collection<String[]> communityResourcesInput = loadFile(resouceFile);
		log.info("Loading resource types...");
		Collection<String[]> resourceList = loadFile(contentIDsFile);
		log.info("Loading community topics...");
		Collection<String[]> communityTopicsInput = loadFile(topicFile);
		
		//--------------------------------------------------------------------
		// build data structures
		//--------------------------------------------------------------------
		// build user id mapping
		for( Pair<Integer,String> entry : userList ) {
			userIdMap.put(entry.getFirst(), entry.getSecond());
		}	
		
		//--------------------------------------------------------------------
		// build content type mapping
		//--------------------------------------------------------------------
		for( String[] row : resourceList ) {
			Integer contentId;
			Integer contentType;
			String hash;
			
			try {
				contentId   = integerParser.parseString(row[3]);
				contentType = integerParser.parseString(row[4]);
				hash        = row[0];
			} catch( Exception e ) {
				continue;
			}

			// count current resource's number of occurrences
			resourceTypeMap.put(contentId, contentType);
			resourceHashMap.put(contentId, hash);
			Integer oldCount = 0;
			if( resourceCount.containsKey(hash) ) {
				oldCount = resourceCount.get(hash); 
			}
			resourceCount.put(hash, ++oldCount);
			
		}
		
		
		log.info("Building user clusters...");
		//--------------------------------------------------------------------
		// build user clusters
		//--------------------------------------------------------------------
		for( Pair<Integer,Integer> entry : userClustering ) {
			Integer userId    = entry.getFirst();
			Integer clusterId = entry.getSecond();
			
			communCache.add(clusterId);
			
			String userName = userIdMap.get(userId);
			if( !ValidationUtils.present(userName) ) {
				log.error("No user name given for user id " + userId);
				userName = "<undef>";
				continue;
			}
			User user = new User(userName);
			user.setWeight(1.0);
			
			// user cluster lookup
			Collection<Integer> clusterList = user2Clusters.get(userId);
			if( clusterList == null ) {
				clusterList = new ArrayList<Integer>();
				user2Clusters.put(userId, clusterList);
			}
			clusterList.add(clusterId);
			
			Cluster<User> community;
			// fill communitiy structure
			if( !userMap.containsKey(clusterId) ) {
				community = new Cluster<User>();
				community.setInstances(new ArrayList<User>());
				community.setClusterID(clusterId);
				userMap.put(clusterId, community);
			} else {
				community = userMap.get(clusterId);
			}
			
			community.getInstances().add(user);
		};
		for( Map.Entry<Integer, Cluster<User>> entry : userMap.entrySet() ) {
			communities.add(entry.getValue());
		}

		//--------------------------------------------------------------------
		// build resource clusters
		//--------------------------------------------------------------------
		log.info("Building resource clusters...");
		for( String[] row : communityResourcesInput ) {
																																																																										Integer contentId;
			Integer clusterId;
			Double  weight;
			try {
				contentId = integerParser.parseString(row[0]);
				clusterId = integerParser.parseString(row[1]);
				weight    = doubleParser.parseString(row[2]);
			} catch( Exception e ) {
				continue;
			}
			
			Integer contentType = resourceTypeMap.get(contentId);
			if( contentType == null ) {
				log.error("No content type for content id "+contentId);
				continue;
			}
			
			String hash = resourceHashMap.get(contentId);
			if( hash == null ) {
				log.error("No hash for content id " + contentId);
				continue;
			}
			
			Post<? extends Resource> post = new Post<Resource>();
			post.setContentType(contentType);
			post.setContentId(contentId);
			post.setWeight(weight);
			
			Cluster<Post<? extends Resource>> resources = resourceMap.get(clusterId);
			if( resources == null ) {
				resources = new Cluster<Post<? extends Resource>>();
				resources.setInstances(new ArrayList<Post<? extends Resource>>());
				resources.setClusterID(clusterId);
				resourceMap.put(clusterId, resources);
			};
			resources.getInstances().add(post);
			
			//--------------------------------------------------------------------
			// calculate resource frequency for given cluster
			//--------------------------------------------------------------------
			Map<String,Integer> clusterFrequencies = clusterResourceFrequency.get(clusterId);
			if( clusterFrequencies==null ) {
				clusterFrequencies = new HashMap<String, Integer>();
				clusterResourceFrequency.put(clusterId, clusterFrequencies);
			};
			Integer oldCount = 0;
			if( clusterFrequencies.containsKey(hash) ) {
				oldCount = clusterFrequencies.get(hash);
			};
			clusterFrequencies.put(hash, ++oldCount);
			
			// count number of clusters containing current resource
			Map<Integer,Integer> resourceClusters = clusterResourceCount.get(hash);
			if( resourceClusters==null ) {
				resourceClusters = new HashMap<Integer, Integer>();
				clusterResourceCount.put(hash, resourceClusters);
			};
			oldCount = 0;
			if( !resourceClusters.containsKey(clusterId) ) {
				resourceClusters.put(clusterId, 1);
			}
		};
		log.info("Got "+resourceMap.size()+" communites for "+communityResourcesInput.size()+" resource assignments");
		for( Map.Entry<Integer, Cluster<Post<? extends Resource>>> entry : resourceMap.entrySet() ) {
			resources.add(entry.getValue());
		}
		
		//--------------------------------------------------------------------
		// build tag clusters
		//--------------------------------------------------------------------
		log.info("Building tag clusters");
		Set<String> topicTagCache = new HashSet<String>();
		for( String[] row : communityTopicsInput ) {
			Integer clusterId = integerParser.parseString(row[0]); 
			Integer topicId   = integerParser.parseString(row[1]);
			String  tagName   = stringParser.parseString(row[2]);
			Double  weight    = doubleParser.parseString(row[3]);
			if( weight < 0.5 ) {
				continue;
			}
			
			topicsCache.add(topicId);
			
			// avoid duplicates
			if( topicTagCache.contains(clusterId+"-"+topicId+"-"+tagName) ) {
				continue;
			}

			Tag tag = new Tag(tagName);
			tag.setTopicId(topicId);
			tag.setWeight(weight);
			
			// assign tag to each of user's clusters
			Cluster<Tag> topic = topicMap.get(clusterId);
			if( topic == null ) {
				topic = new Cluster<Tag>();
				topic.setInstances(new ArrayList<Tag>());
				topic.setClusterID(clusterId);
				topicMap.put(clusterId, topic);
			};
			topic.getInstances().add(tag);
			
			//----------------------------------------------------------------
			// update tag frequencies
			//----------------------------------------------------------------
			// count current resource's number of occurrences
			Integer oldCount = 0;
			if( tagCount.containsKey(tagName) ) {
				oldCount = tagCount.get(tagName); 
			}
			tagCount.put(tagName, ++oldCount);

			// calculate tag frequencies
			Map<String,Integer> clusterFrequencies = clusterTagFrequency.get(clusterId);
			if( clusterFrequencies==null ) {
				clusterFrequencies = new HashMap<String, Integer>();
				clusterTagFrequency.put(clusterId, clusterFrequencies);
			};
			oldCount = 0;
			if( clusterFrequencies.containsKey(tagName) ) {
				oldCount = clusterFrequencies.get(tagName);
			};
			clusterFrequencies.put(tagName, ++oldCount);
			
			// count number of clusters containing current resource
			Map<Integer,Integer> tagClusters = clusterTagCount.get(tagName);
			if( tagClusters==null ) {
				tagClusters = new HashMap<Integer, Integer>();
				clusterTagCount.put(tagName, tagClusters);
			};
			oldCount = 0;
			if( !tagClusters.containsKey(clusterId) ) {
				tagClusters.put(clusterId, 1);
			}
			
		};
		for( Map.Entry<Integer, Cluster<Tag>> entry : topicMap.entrySet() ) {
			topics.add(entry.getValue());
		}
		
		this.clusterCount = communCache.size();
		this.topicCount   = topicsCache.size();
		
		this.algorithm = new MockAlgorithm("LDAkMeans", "LDA="+topicCount+",kMeans="+clusterCount);

		//--------------------------------------------------------------------
		// calculate tf-idf for resources
		//--------------------------------------------------------------------
		Integer nResources = resourceCount.keySet().size();
		for( Cluster<Post<? extends Resource>> cluster : resources ) {
			Integer community = cluster.getClusterID();
			// get maximal resource frequency for given cluster
			double maxFreq = 0;
			for( Map.Entry<String,Integer> entry : clusterResourceFrequency.get(community).entrySet() ) {
				if( entry.getValue() > maxFreq ) {
					maxFreq = entry.getValue();
				}
			}
			// calculate ranking value for each post
			for( Post<? extends Resource> post : cluster.getInstances() ) {
				String hash = resourceHashMap.get(post.getContentId());
				Integer resourceClusters = clusterResourceCount.get(hash).keySet().size();
				double tf = 
					clusterResourceFrequency.get(community).get(hash)/maxFreq;
				double idf = 
					Math.log(1.0*nResources / resourceClusters);
				
				post.setWeight(tf*idf);
			}
		}
		
		//--------------------------------------------------------------------
		// calculate tf-idf for tags
		//--------------------------------------------------------------------
		Integer nTags = tagCount.keySet().size();
		for( Cluster<Tag> cluster : topics ) {
			Integer community = cluster.getClusterID();
			// get maximal resource frequency for given cluster
			double maxFreq = 0;
			for( Map.Entry<String,Integer> entry : clusterTagFrequency.get(community).entrySet() ) {
				if( entry.getValue() > maxFreq ) {
					maxFreq = entry.getValue();
				}
			}
			// calculate ranking value for each post
			for( Tag tag : cluster.getInstances() ) {
				Integer tagClusters = clusterTagCount.get(tag.getName()).keySet().size();
				double tf = 
					clusterTagFrequency.get(community).get(tag.getName())/maxFreq;
				double idf = 
					Math.log(1.0*nTags/ tagClusters);
				
				tag.setWeight(tf*idf);
				tag.setGlobalcount(clusterTagFrequency.get(community).get(tag.getName()));
			}
		}
	}


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
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------


}
