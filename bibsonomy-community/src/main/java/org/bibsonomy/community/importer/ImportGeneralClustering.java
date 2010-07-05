package org.bibsonomy.community.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.importer.MockupImporter;
import org.bibsonomy.community.database.CommunityManager;
import org.bibsonomy.community.database.DBManageInterface;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.util.CommunityConfig;
import org.bibsonomy.community.util.JNDITestDatabaseBinder;
import org.bibsonomy.community.util.PropertyLoader;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.model.Resource;
import org.bibsonomy.util.ValidationUtils;

public class ImportGeneralClustering {
	private static final Log log = LogFactory.getLog(ImportGeneralClustering.class);
	
	/**
	 * args:
	 * 
	 * 0) url 
	 * 1) post database
	 * 2) community database
	 * 3) username
	 * 4) password
	 * 
	 * 5) clusteringFile
	 * 6) resourceRankingFile
	 * 7) tagRankingFile
	 * 
	 * 8) algorithmName
	 * 9) algorithmMeta
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if( args.length <= 0 ) {
			usage();
			return;
		} 
		Properties config = PropertyLoader.openPropertyFile("community.properties");

		String url               = config.getProperty("url");
		String postDatabase      = config.getProperty("postDatabase");
		String communityDatabase = config.getProperty("communityDatabase");
		String username          = config.getProperty("username");
		String password          = config.getProperty("password");
		
		String clusteringFile      = config.getProperty("clusteringFile");
		String tasFile             = config.getProperty("tasFile");
		String userRankingFile     = config.getProperty("userRankingFile");
		String tagRankingFile      = config.getProperty("tagRankingFile");
		String resourceRankingFile = config.getProperty("resourceRankingFile");
		
		String algorithmName = config.getProperty("algorithmName");
		String algorithmMeta = config.getProperty("algorithmMeta"); 

		// initialize database manager
		JNDITestDatabaseBinder.bind("bibsonomy_community", url, communityDatabase, username, password);
		JNDITestDatabaseBinder.bind("bibsonomy_community_posts", url, postDatabase, username, password);
		DBManageInterface dbLogic = CommunityManager.getInstance();

		// initialize importer
		CommunityImporter importer = new GeneralCommunityImporter(algorithmName, algorithmMeta, clusteringFile, tasFile, userRankingFile, tagRankingFile, resourceRankingFile);

		int nClusters = importer.getClusterCount();
		int nTopics   = importer.getTopicCount();

		Collection<Cluster<User>> communities = importer.getCommunities();
		Collection<Cluster<Tag>> topics       = importer.getTopics();
		Collection<Cluster<Post<? extends Resource>>> posts = importer.getResources();

		// store result in data base
		// 0. add algorithm
		log.info("Adding algorithm "+importer.getAlgorithm().getName()+"...");
		dbLogic.addAlgorithm(importer.getAlgorithm());
		// 1. start run set
		Integer block_id = dbLogic.addRunSet();
		log.info("Started run set nr. "+block_id);
		// 2. add algorithm
		Integer run_id   = dbLogic.addAlgorithmToRunSet(importer.getAlgorithm(), block_id, nClusters, nTopics);
		log.info("Algorithm got run id " + run_id);
		// 3. add communities
		log.info("Adding communities...");
		dbLogic.addCommunities(run_id, communities);
		// 4. add topics
		log.info("Adding topics...");
		dbLogic.addTopics(run_id, topics);
		// 5. add posts
		log.info("Adding posts...");
		dbLogic.addResources(run_id, posts);
		
		JNDITestDatabaseBinder.unbind();
	}
	
	protected static void usage() {
		System.out.println("ImportLDAkMeansClustering <url> <post databasse> <community database> <username> <password> <clusteringFile> <tasFile> <resourceRankingFile> <tagRankingFile> <algorithmName> <algorithmMeta>");
		
	}
}
