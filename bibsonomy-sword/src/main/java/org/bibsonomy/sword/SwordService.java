package org.bibsonomy.sword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.sword.LogicFactory;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.client.Client;
import org.purl.sword.client.PostMessage;
import org.purl.sword.client.SWORDClientException;



/**
 * Sword main
 * 
 * @author:  sven
 * @version: 
 * 
 */
public class SwordService {
	private static final Log log = LogFactory.getLog(SwordService.class);

	/*
	 * supported configuration arguments
	 */
	private static final String ARG_DATABASE = "--database";

	
	/*
	 * corresponding configuration attributes
	 */
	private boolean directDatabaseAccess = false;

	
	
	public static void main(String[] args) {
		final SwordService service = new SwordService();
		SwordPost swordPost = null;
		log.info("starting sword");
				
		SwordUser defaultuser = new SwordUser("stefani","adf57971843dac5fec32d8b2b799bd8a");
		
		
		
		SwordUser user = defaultuser;
		
		configureService(args, service);

		log.info("retrievePost");
		swordPost = service.retrieveSwordPost(user);
		
		if (swordPost.hasBibTexPost()) { 
		
		System.out.println("Title: "+swordPost.getBibTexPost().getResource().getTitle());
		
		System.out.println("Author: "+swordPost.getBibTexPost().getResource().getAuthor());

		System.out.println("Hallo");
		
		
		// get an instance of SWORD-Client
		Client swordClient = new Client();
		
		// configure sword
		String swordHttpServer = "dspace.swordapp.org";  //http://dspace.swordapp.org/sword/servicedocument
		int swordHttpPort = 80;
		String swordHttpUserAgent = "PumaDevSst";
		String swordHttpUrl = "http://dspace.swordapp.org/sword/servicedocument"; 
		String swordHttpUser = "stefani@bibliothek.uni-kassel.de";
		String swordHttpPassword = "sspuma10";
		PostMessage swordMessage = new PostMessage();
		DepositResponse depositResponse = null; 

		// create sword post message
		
		// message file
		// create directory in temp-folder
		// store post documents there
		// store meta data there in format http://purl.org/net/sword-types/METSDSpaceSIP
		// delete post document files and meta data file
		
		// add files to zip archive
		// -- send zip archive
		// -- delete zip archive
		
		
		
		// message meta
		swordMessage.setNoOp(true);
		swordMessage.setUserAgent(swordHttpUserAgent+"Post");
		
		
		
		
		swordClient.setServer(swordHttpServer, swordHttpPort);
		swordClient.setUserAgent(swordHttpUserAgent);
		swordClient.setCredentials(swordHttpUser, swordHttpPassword);
		
//		try {
//			ServiceDocument serviceDocument = swordClient.getServiceDocument(swordHttpUrl);
//			log.info("serviceDocument: " + serviceDocument.toString());

/* http://dspace.swordapp.org/sword/servicedocument
 
<?xml version="1.0" encoding="UTF-8"?>
<app:service xmlns:atom="http://www.w3.org/2005/Atom" xmlns:app="http://www.w3.org/2007/app" xmlns:sword="http://purl.org/net/sword/" xmlns:dcterms="http://purl.org/dc/terms/">
   <sword:version>1.3</sword:version>
   <sword:verbose>true</sword:verbose>
   <sword:noOp>true</sword:noOp>
   <sword:maxUploadSize>-1</sword:maxUploadSize>
   <app:workspace>
      <atom:title type="text">DSpace SWORD 1.3 Demo</atom:title>
      <app:collection href="http://dspace.swordapp.org/sword/deposit/123456789/6">
         <atom:title type="text">Data sets</atom:title>
         <app:accept>application/zip</app:accept>
         <sword:acceptPackaging q="1.0">http://purl.org/net/sword-types/METSDSpaceSIP</sword:acceptPackaging>
         <sword:collectionPolicy>NOTE: PLACE YOUR OWN LICENSE HERE This sample license is provided for informational purposes only. NON-EXCLUSIVE DISTRIBUTION LICENSE By signing and submitting this license, you (the author(s) or copyright owner) grants to DSpace University (DSU) the non-exclusive right to reproduce, translate (as defined below), and/or distribute your submission (including the abstract) worldwide in print and electronic format and in any medium, including but not limited to audio or video. You agree that DSU may, without changing the content, translate the submission to any medium or format for the purpose of preservation. You also agree that DSU may keep more than one copy of this submission for purposes of security, back-up and preservation. You represent that the submission is your original work, and that you have the right to grant the rights contained in this license. You also represent that your submission does not, to the best of your knowledge, infringe upon anyone's copyright. If the submission contains material for which you do not hold copyright, you represent that you have obtained the unrestricted permission of the copyright owner to grant DSU the rights required by this license, and that such third-party owned material is clearly identified and acknowledged within the text or content of the submission. IF THE SUBMISSION IS BASED UPON WORK THAT HAS BEEN SPONSORED OR SUPPORTED BY AN AGENCY OR ORGANIZATION OTHER THAN DSU, YOU REPRESENT THAT YOU HAVE FULFILLED ANY RIGHT OF REVIEW OR OTHER OBLIGATIONS REQUIRED BY SUCH CONTRACT OR AGREEMENT. DSU will clearly identify your name(s) as the author(s) or owner(s) of the submission, and will not make any alteration, other than as allowed by this license, to your submission. </sword:collectionPolicy>
         <dcterms:abstract>A collection for depositing data sets</dcterms:abstract>
         <sword:mediation>true</sword:mediation>
      </app:collection>
      <app:collection href="http://dspace.swordapp.org/sword/deposit/123456789/4">
         <atom:title type="text">Research materials</atom:title>
         <app:accept>application/zip</app:accept>
         <sword:acceptPackaging q="1.0">http://purl.org/net/sword-types/METSDSpaceSIP</sword:acceptPackaging>
         <sword:collectionPolicy>NOTE: PLACE YOUR OWN LICENSE HERE This sample license is provided for informational purposes only. NON-EXCLUSIVE DISTRIBUTION LICENSE By signing and submitting this license, you (the author(s) or copyright owner) grants to DSpace University (DSU) the non-exclusive right to reproduce, translate (as defined below), and/or distribute your submission (including the abstract) worldwide in print and electronic format and in any medium, including but not limited to audio or video. You agree that DSU may, without changing the content, translate the submission to any medium or format for the purpose of preservation. You also agree that DSU may keep more than one copy of this submission for purposes of security, back-up and preservation. You represent that the submission is your original work, and that you have the right to grant the rights contained in this license. You also represent that your submission does not, to the best of your knowledge, infringe upon anyone's copyright. If the submission contains material for which you do not hold copyright, you represent that you have obtained the unrestricted permission of the copyright owner to grant DSU the rights required by this license, and that such third-party owned material is clearly identified and acknowledged within the text or content of the submission. IF THE SUBMISSION IS BASED UPON WORK THAT HAS BEEN SPONSORED OR SUPPORTED BY AN AGENCY OR ORGANIZATION OTHER THAN DSU, YOU REPRESENT THAT YOU HAVE FULFILLED ANY RIGHT OF REVIEW OR OTHER OBLIGATIONS REQUIRED BY SUCH CONTRACT OR AGREEMENT. DSU will clearly identify your name(s) as the author(s) or owner(s) of the submission, and will not make any alteration, other than as allowed by this license, to your submission. </sword:collectionPolicy>
         <dcterms:abstract>A collection for depositing research materials</dcterms:abstract>
         <sword:mediation>true</sword:mediation>
      </app:collection>
      <app:collection href="http://dspace.swordapp.org/sword/deposit/123456789/5">
         <atom:title type="text">Teaching materials</atom:title>
         <app:accept>application/zip</app:accept>
         <sword:acceptPackaging q="1.0">http://purl.org/net/sword-types/METSDSpaceSIP</sword:acceptPackaging>
         <sword:collectionPolicy>NOTE: PLACE YOUR OWN LICENSE HERE This sample license is provided for informational purposes only. NON-EXCLUSIVE DISTRIBUTION LICENSE By signing and submitting this license, you (the author(s) or copyright owner) grants to DSpace University (DSU) the non-exclusive right to reproduce, translate (as defined below), and/or distribute your submission (including the abstract) worldwide in print and electronic format and in any medium, including but not limited to audio or video. You agree that DSU may, without changing the content, translate the submission to any medium or format for the purpose of preservation. You also agree that DSU may keep more than one copy of this submission for purposes of security, back-up and preservation. You represent that the submission is your original work, and that you have the right to grant the rights contained in this license. You also represent that your submission does not, to the best of your knowledge, infringe upon anyone's copyright. If the submission contains material for which you do not hold copyright, you represent that you have obtained the unrestricted permission of the copyright owner to grant DSU the rights required by this license, and that such third-party owned material is clearly identified and acknowledged within the text or content of the submission. IF THE SUBMISSION IS BASED UPON WORK THAT HAS BEEN SPONSORED OR SUPPORTED BY AN AGENCY OR ORGANIZATION OTHER THAN DSU, YOU REPRESENT THAT YOU HAVE FULFILLED ANY RIGHT OF REVIEW OR OTHER OBLIGATIONS REQUIRED BY SUCH CONTRACT OR AGREEMENT. DSU will clearly identify your name(s) as the author(s) or owner(s) of the submission, and will not make any alteration, other than as allowed by this license, to your submission. </sword:collectionPolicy>
         <dcterms:abstract>A collection for depositing teaching materials</dcterms:abstract>
         <sword:mediation>true</sword:mediation>
      </app:collection>
      <app:collection href="http://dspace.swordapp.org/sword/deposit/123456789/217">
         <atom:title type="text">Workflow</atom:title>
         <app:accept>application/zip</app:accept>
         <sword:acceptPackaging q="1.0">http://purl.org/net/sword-types/METSDSpaceSIP</sword:acceptPackaging>
         <sword:collectionPolicy>NOTE: PLACE YOUR OWN LICENSE HERE This sample license is provided for informational purposes only. NON-EXCLUSIVE DISTRIBUTION LICENSE By signing and submitting this license, you (the author(s) or copyright owner) grants to DSpace University (DSU) the non-exclusive right to reproduce, translate (as defined below), and/or distribute your submission (including the abstract) worldwide in print and electronic format and in any medium, including but not limited to audio or video. You agree that DSU may, without changing the content, translate the submission to any medium or format for the purpose of preservation. You also agree that DSU may keep more than one copy of this submission for purposes of security, back-up and preservation. You represent that the submission is your original work, and that you have the right to grant the rights contained in this license. You also represent that your submission does not, to the best of your knowledge, infringe upon anyone's copyright. If the submission contains material for which you do not hold copyright, you represent that you have obtained the unrestricted permission of the copyright owner to grant DSU the rights required by this license, and that such third-party owned material is clearly identified and acknowledged within the text or content of the submission. IF THE SUBMISSION IS BASED UPON WORK THAT HAS BEEN SPONSORED OR SUPPORTED BY AN AGENCY OR ORGANIZATION OTHER THAN DSU, YOU REPRESENT THAT YOU HAVE FULFILLED ANY RIGHT OF REVIEW OR OTHER OBLIGATIONS REQUIRED BY SUCH CONTRACT OR AGREEMENT. DSU will clearly identify your name(s) as the author(s) or owner(s) of the submission, and will not make any alteration, other than as allowed by this license, to your submission. </sword:collectionPolicy>
         <dcterms:abstract>Collection with workflow</dcterms:abstract>
         <sword:mediation>true</sword:mediation>
      </app:collection>
   </app:workspace>
</app:service>
 
 */
			
//			swordHttpUrl = "http://dspace.swordapp.org/sword/deposit/123456789/4"; // Research materials
			
			//depositResponse = swordClient.postFile(swordMessage);
			
			
//		} catch (SWORDClientException e) {
//			log.warn("SWORDClientException");
			//e.printStackTrace();
//		}
		
		} else {
			// no posts retrieved
			log.warn("got no posts for user "+user.getUsername());
		}
	}

	private static void configureService(final String[] args, final SwordService service) {
		log.info("parsing command line arguments " + Arrays.toString(args));
		for (final String arg: args) {
			if (ARG_DATABASE.equals(arg)) {
				service.setDirectDatabaseAccess(true);
			} 
		}
		log.info("using direct database access: " + service.isDirectDatabaseAccess());
	}

	
	@SuppressWarnings("unchecked")
	private SwordPost retrieveSwordPost(SwordUser swordUser) {
		SwordPost swordPost = new SwordPost();
		Post<BibTex> bibTexPost = null;
		
		String resourceHash = "545d2292ba08ff5ec34e9ae8e9c5b314"; 
		String apiUrl = "http://localhost:8080/api/";
		
		
		/*
		 * getting DB access
		 */
		log.info("getting database access for user " + swordUser.getUsername());
		final LogicInterface logic;
		final LogicFactory logicFactory = new LogicFactory(apiUrl, swordUser);
		if (directDatabaseAccess) {
			logic = logicFactory.getDBLogic();
		} else {
			logic = logicFactory.getRestLogic();
		}
		final String username = logicFactory.getLoginUserName();
		
		log.info("got username from logicFactory: " + username);
		
		// get meta data for post 
		try {
			final Post<? extends Resource> post = logic.getPostDetails(resourceHash, username); 

			final Resource resource = post.getResource();
			if (post.getResource() instanceof BibTex) {
				
				// fileprefix
				String pathToWorkDir = "/tmp/"; 
				
				// Destination directory 
				File destinationDirectory = new File(pathToWorkDir+username+"_"+post.getResource().getIntraHash());

				// zip-filename
				File zipFilename = new File(destinationDirectory.getAbsoluteFile()+"/"+"pumapublication_"+username+"_"+post.getResource().getIntraHash()+".zip");

				byte[] buffer = new byte[18024];
				
				bibTexPost = (Post<BibTex>) post;
				swordPost.setBibTexPost(bibTexPost);

				log.info("getIntraHash = " + bibTexPost.getResource().getIntraHash());

				
				/*
				 * get documents
				 */
				// get documents for post
				final List<Document> documents = ((BibTex) resource).getDocuments();
				System.out.println(documents);
				for (final Document document : documents) {
					// get file and store it in hard coded folder "/tmp/"
					final Document document2 = logic.getDocument(username, bibTexPost.getResource().getIntraHash(), document.getFileName());
					//System.out.println(document2.getFile());
					/*
					 * print file type
					 */
/*
					Process result;
					try {
						result = Runtime.getRuntime().exec("file " + document2.getFile().getAbsolutePath());
						final BufferedReader reader = new BufferedReader(new InputStreamReader(result.getInputStream(), "UTF-8"));
						String line;
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				*/

					// move file to user folder with username_resource-hash as folder name
						
					// File (or directory) to be moved 
					File fileToMove = new File(document2.getFile().getAbsolutePath()); 
					// create directory
				    boolean mkdir_success = (new File(destinationDirectory.getAbsolutePath())).mkdir();
				    if (mkdir_success) {
				      System.out.println("Directory: " + destinationDirectory.getAbsolutePath() + " created");
				    }    
	
					
					// Move file to new directory 
					boolean rename_success = fileToMove.renameTo(new File(destinationDirectory, fileToMove.getName())); 
					if (!rename_success) { 
						// File was not successfully moved } 
						log.info("File was not successfully moved: "+fileToMove.getName());
					}
					
					// add file to zip archive  
					try {
						ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilename));
						ZipEntry zipEntry = new ZipEntry(fileToMove.getName());

						
						// Set the compression ratio
						zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);

						FileInputStream in = new FileInputStream(destinationDirectory.getAbsoluteFile()+"/"+fileToMove.getName());

						// Add ZIP entry to output stream.
						zipOutputStream.putNextEntry(zipEntry);

						// Transfer bytes from the current file to the ZIP file
						//out.write(buffer, 0, in.read(buffer));

						int len;
						while ((len = in.read(buffer)) > 0)
					    {
							zipOutputStream.write(buffer, 0, len);
					    }

						zipOutputStream.closeEntry();

						// Close the current file input stream
						in.close();			
						
						zipOutputStream.close();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						log.info("FileNotFoundException! Could not create zip-archive: "+zipFilename.getAbsolutePath());
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.info("IOException! Could not modify zip-archive: "+zipFilename.getAbsolutePath());
					}
				
				}
				
				
				// create meta data structure
				
				
				// write meta data to file
				
				
				// add metadata files to archive  
				
				
				// remove dir

				
			}			
			
		
		
		} catch (ResourceNotFoundException e) {
			// e.printStackTrace();
			log.info("ResourceNotFoundException! SwordService-retrievePost");
		} catch (ResourceMovedException e) {
			//e.printStackTrace();
			log.info("ResourceMovedException! SwordService-retrievePost");
		}
		
		
		
		
		return swordPost;
	}

	public boolean isDirectDatabaseAccess() {
		return directDatabaseAccess;
	}
	public void setDirectDatabaseAccess(boolean directDatabaseAccess) {
		this.directDatabaseAccess = directDatabaseAccess;
	}
	
	
}



