package org.bibsonomy.community.importer;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.importer.MockupImporter;
import org.bibsonomy.community.database.CommunityManager;
import org.bibsonomy.community.database.DBManageInterface;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.util.JNDITestDatabaseBinder;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.model.Resource;

public class ImportLDAkMeansClustering {
	private static final Log log = LogFactory.getLog(ImportLDAkMeansClustering.class);
	
	public static void main(String[] args) throws Exception {
		if( args.length <= 0 ) {
			usage();
			return;
		} 
		String url      = args[0];
		String database = args[1];
		String username = args[2];
		String password = args[3];
		JNDITestDatabaseBinder.bind("bibsonomy_community", url, database, username, password);
		//JNDITestDatabaseBinder.bind();
		DBManageInterface dbLogic = CommunityManager.getInstance();
		
		CommunityImporter importer = new LDAkMeansImporter(args[4],args[5],args[6],args[7],args[8]);
		// new MockupImporter();

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
		dbLogic.addCommunities(run_id, communities);
		log.info("Adding communities...");
		// 4. add topics
		log.info("Adding topics...");
		dbLogic.addTopics(run_id, topics);
		// 5. add posts
		log.info("Adding posts...");
		dbLogic.addResources(run_id, posts);
		
		JNDITestDatabaseBinder.unbind();
	}

	protected static void usage() {
		System.out.println("ImportLDAkMeansClustering <url> <databasse> <username> <password> <userNamesFile> <contentIDsFile> <clusteringFile> <communityResourcesFile> <communityTopicsFile>");
		
	}
}
