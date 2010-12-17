package de.unikassel.puma.openaccess.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.database.DBLogicApiInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.HashUtils;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDException;
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
	
	private String dirTemp;
	private String httpServer;
	private int httpPort;
	private String httpUserAgent;
	private String authUsername;
	private String authPassword;
	private String httpServicedocumentUrl;
	private String httpDepositUrl;

	public SwordService() {
		init();
	}
	
	private void init() {
		retrieveSwordConfiguration();
		

	}
	
	private void retrieveSwordConfiguration() {
		
		dirTemp = "/tmp/";
		httpServer = "localhost";
		httpPort = 80;
		httpUserAgent = "puma";
		authUsername = "username";
		authPassword = "password";
		httpServicedocumentUrl = "/sword/servicedocument" ;
		httpDepositUrl = "/no/deposit/url/";
		
		/**
		 * retrieve configuration from environment variables in context.xml if set
		 */
		
		Context initContext = null;
		Context envContext = null;
		
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
		} catch (NamingException ex) {
			log.error("Error when trying create initContext lookup for java:/comp/env via JNDI.", ex);
		}

		try {
			dirTemp = (String) envContext.lookup("sword/dirTemp");
		} catch (NamingException ex) {
			log.warn("Error when trying to read environment variable 'sword/dirTemp' via JNDI. Using defautl value "+dirTemp, ex);
		}			

		try {
			httpPort = (Integer) envContext.lookup("sword/httpPort");
		} catch (NamingException ex) {
			log.warn("Error when trying to read environment variable 'sword/httpPort' via JNDI. Using defautl value "+httpPort, ex);
		}			
		
		try {
			httpServer = (String) envContext.lookup("sword/httpServer");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'sword/dirTemp' via JNDI.", ex);
		}			

		try {
			httpUserAgent = (String) envContext.lookup("sword/httpUserAgent");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'sword/dirTemp' via JNDI.", ex);
		}			

		try {
			authUsername = (String) envContext.lookup("sword/authUsername");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'sword/authUsername' via JNDI.", ex);
		}			

		try {
			authPassword = (String) envContext.lookup("sword/authPassword");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'sword/authPassword' via JNDI.", ex);
		}			

		try {
			httpServicedocumentUrl = (String) envContext.lookup("sword/httpServicedocumentUrl");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'sword/httpServicedocumentUrl' via JNDI.", ex);
		}			

		try {
			httpDepositUrl = (String) envContext.lookup("sword/httpDepositUrl");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'sword/httpDepositUrl' via JNDI.", ex);
		}
		
		
	}
	
	private String retrieveServicedocument(){
		return null;
	}
	
	private boolean checkServicedokument(String doc, String url) {
		return checkServicedokument(doc, url, null);
	}
	
	private boolean checkServicedokument(String doc, String url, String accept) {
		return false;
	}
	
	

	/*
	 * 
<?xml version="1.0" encoding="UTF-8"?>
<app:service xmlns:atom="http://www.w3.org/2005/Atom" xmlns:app="http://www.w3.org/2007/app" xmlns:sword="http://purl.org/net/sword/" xmlns:dcterms="http://purl.org/dc/terms/">
   <sword:version>1.3</sword:version>
   <sword:verbose>true</sword:verbose>
   <sword:noOp>true</sword:noOp>
   <sword:maxUploadSize>-1</sword:maxUploadSize>
   <atom:generator uri="http://www.dspace.org/ns/sword/1.3.1" version="1.3"/>
   <app:workspace>
      <atom:title type="text">DSpace 1.6.1-Test</atom:title>
      <app:collection href="http://bib-pc152.bibliothek.uni-kassel.de:8080/sword/deposit/urn:nbn:de:hebis:34-2006051211778">
         <atom:title type="text">Arbeitspapiere</atom:title>
         <app:accept>application/zip</app:accept>
         <sword:acceptPackaging q="1.0">http://purl.org/net/sword-types/METSDSpaceSIP</sword:acceptPackaging>
         <sword:collectionPolicy>NOTE: PLACE YOUR OWN LICENSE HERE This sample license is provided for informational purposes only. NON-EXCLUSIVE DISTRIBUTION LICENSE By signing and submitting this license, you (the author(s) or copyright owner) grants to DSpace University (DSU) the non-exclusive right to reproduce, translate (as defined below), and/or distribute your submission (including the abstract) worldwide in print and electronic format and in any medium, including but not limited to audio or video. You agree that DSU may, without changing the content, translate the submission to any medium or format for the purpose of preservation. You also agree that DSU may keep more than one copy of this submission for purposes of security, back-up and preservation. You represent that the submission is your original work, and that you have the right to grant the rights contained in this license. You also represent that your submission does not, to the best of your knowledge, infringe upon anyone's copyright. If the submission contains material for which you do not hold copyright, you represent that you have obtained the unrestricted permission of the copyright owner to grant DSU the rights required by this license, and that such third-party owned material is clearly identified and acknowledged within the text or content of the submission. IF THE SUBMISSION IS BASED UPON WORK THAT HAS BEEN SPONSORED OR SUPPORTED BY AN AGENCY OR ORGANIZATION OTHER THAN DSU, YOU REPRESENT THAT YOU HAVE FULFILLED ANY RIGHT OF REVIEW OR OTHER OBLIGATIONS REQUIRED BY SUCH CONTRACT OR AGREEMENT. DSU will clearly identify your name(s) as the author(s) or owner(s) of the submission, and will not make any alteration, other than as allowed by this license, to your submission. </sword:collectionPolicy>
         <dcterms:abstract>working papers</dcterms:abstract>
         <sword:mediation>true</sword:mediation>
      </app:collection>	 
      
     *
	 */

	
	/**
	 * collects all informations to send Documents with metadata to repository 
	 */
	public boolean submitDocument(Post<?> post, User user, String projectDocumentPath) {
		log.info("starting sword");
		File swordZipFile = null;

		//swordZipFile = service.retrieveSwordPost(user);

		// -------------------------------------------------------------------------------
		/*
		 * retrieve ZIP-FILE
		 */
			
		if (post.getResource() instanceof BibTex) {
					
			// fileprefix
			String fileID = HashUtils.getMD5Hash(user.getName().getBytes()) + "_"+post.getResource().getIntraHash();
					
			// Destination directory 
			File destinationDirectory = new File(dirTemp+"/"+fileID);

			// zip-filename
			swordZipFile = new File(destinationDirectory.getAbsoluteFile()+"/"+fileID+".zip");

			byte[] buffer = new byte[18024];
					
			log.info("getIntraHash = " + post.getResource().getIntraHash());

			/*
			 * get documents
			 */
			
			// At the moment, there are no Documents delivered by method parameter post.
			// retrieve list of documents from database - workaround
			
			// get documents for post and insert documents into post 
			((BibTex) post.getResource()).setDocuments(retrieveDocumentsFromDatabase(user, post.getResource().getIntraHash()));
			
					
			try {
				// create directory
				boolean mkdir_success = (new File(destinationDirectory.getAbsolutePath())).mkdir();
				if (mkdir_success) {
					System.out.println("Directory: " + destinationDirectory.getAbsolutePath() + " created");
				}    
				
						
				// open zip archive to add files to  
				System.out.println("zipFilename: "+swordZipFile);
				ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(swordZipFile));
				
				ArrayList<String> fileList = new ArrayList<String>();
				
				for (final Document document : ((BibTex) post.getResource()).getDocuments()) {

					//getpostdetails
					// get file and store it in hard coded folder "/tmp/"
					//final Document document2 = logic.getDocument(user.getName(), post.getResource().getIntraHash(), document.getFileName());
					
					// move file to user folder with username_resource-hash as folder name
					
					// File (or directory) to be copied 
					File fileToZip = new File(document.getFileHash());
					
					fileList.add(document.getFileName());
							
			
					
					// Move file to new directory 
					//boolean rename_success = fileToCopy.renameTo(new File(destinationDirectory, fileToMove.getName()));
					/*
					if (!rename_success) { 
						// File was not successfully moved } 
						log.info("File was not successfully moved: "+fileToMove.getName());
					}
					*/
					ZipEntry zipEntry = new ZipEntry(document.getFileName());
		
								
					// Set the compression ratio
					zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
		
					
					String inputFilePath = projectDocumentPath+document.getFileHash().substring(0, 2)+"/"+document.getFileHash();
					FileInputStream in = new FileInputStream(inputFilePath);
		
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
				}

				
				// create meta data structure
				BibTex bibTexPostResource = (BibTex) post.getResource();
				
				// write meta data into zip archive
				ZipEntry zipEntry = new ZipEntry("mets.xml");
				zipOutputStream.putNextEntry(zipEntry);				

				// create XML-Document
				// PrintWriter from a Servlet
						
				MetsBibTexMLGenerator metsBibTexMLGenerator = new MetsBibTexMLGenerator();
				metsBibTexMLGenerator.setFilenameList(fileList);
				//metsGenerator.setMetadata(metadataMap);
				metsBibTexMLGenerator.setMetadata((Post<BibTex>) post);
				StreamResult streamResult = new StreamResult(zipOutputStream);
						
				zipOutputStream.write(metsBibTexMLGenerator.generateMets().getBytes());
						
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				String currentTime = df.format(cal.getTime());

				zipOutputStream.closeEntry();
							
				// close zip archive  
				zipOutputStream.close();
										
				System.out.println("saved to "+swordZipFile.getPath());
						
			} catch (MalformedURLException e) {
				// e.printStackTrace();
				log.info("MalformedURLException! " + e.getMessage());
			} catch (IOException e) {
				//e.printStackTrace();
				log.info("IOException! " + e.getMessage());
				
			} catch (ResourceNotFoundException e) {
				// e.printStackTrace();
				log.warn("ResourceNotFoundException! SwordService-retrievePost");
			}
		}
		/*
		 * end of retrieve ZIP-FILE
		 */
		//---------------------------------------------------
		
		/*
		 * do the SWORD stuff
		 */

		if (null != swordZipFile) {

			// get an instance of SWORD-Client
			Client swordClient = new Client();

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
			swordMessage.setNoOp(false);
			swordMessage.setUserAgent(httpUserAgent);
			swordMessage.setFilepath(swordZipFile.getAbsolutePath());
			swordMessage.setFiletype("application/zip");
			swordMessage.setFormatNamespace("http://purl.org/net/sword-types/METSDSpaceSIP"); // sets packaging!
			swordMessage.setVerbose(true);


			try {
				// get Service Document 
				swordClient.setServer(httpServer, httpPort);
				swordClient.setUserAgent(httpUserAgent);
				swordClient.setCredentials(authUsername, authPassword);
				ServiceDocument serviceDocument = swordClient.getServiceDocument(httpServicedocumentUrl);

				// transmit sword message (zip file with document metadata and document files
				
				swordMessage.setDestination(httpDepositUrl);

				depositResponse = swordClient.postFile(swordMessage);
				if (depositResponse.getHttpResponse()>=300) {
					try {
						log.info("depositResponse: "+ depositResponse.getErrorDocument().getErrorURI());
					} catch (SWORDException e) {
						e.printStackTrace();
					}
				} else {
					log.info("depositResponse: OK!");
				}


			} catch (SWORDClientException e) {
				log.warn("SWORDClientException");
			e.printStackTrace();
			}

		}

		return true;
	}

	/*
	 * Workaround method to retrieve 
	 */
	private List<Document> retrieveDocumentsFromDatabase(User user, String resourceHash) {

		/*
		 * getting DB access
		 */
		final String username = user.getName();

		log.info("getting database access for user " + username);
		final LogicInterface logic;

		final DBLogicApiInterfaceFactory factory = new DBLogicApiInterfaceFactory();
		factory.setDbSessionFactory(new IbatisDBSessionFactory());

		logic = factory.getLogicAccess(username, user.getApiKey());
		
		// get meta data for post
		try {
			final Post<? extends Resource> post = logic.getPostDetails(resourceHash, username); 
			if (post.getResource() instanceof BibTex) {
				
				// get documents for post
				return ((BibTex) post.getResource()).getDocuments();
			}			
		} catch (ResourceNotFoundException e) {
			// e.printStackTrace();
			log.warn("ResourceNotFoundException! SwordService-retrieveDocumentsFromDatabase");
		} catch (ResourceMovedException e) {
			log.warn("ResourceMovedException! SwordService-retrieveDocumentsFromDatabase");
		}
		return null;
	}

	/**
	 * @return the dirTemp
	 */
	public String getDirTemp() {
		return dirTemp;
	}

	/**
	 * @param dirTemp the dirTemp to set
	 */
	public void setDirTemp(String dirTemp) {
		this.dirTemp = dirTemp;
	}

	/**
	 * @return the httpServer
	 */
	public String getHttpServer() {
		return httpServer;
	}

	/**
	 * @param httpServer the httpServer to set
	 */
	public void setHttpServer(String httpServer) {
		this.httpServer = httpServer;
	}

	/**
	 * @return the httpPort
	 */
	public int getHttpPort() {
		return httpPort;
	}

	/**
	 * @param httpPort the httpPort to set
	 */
	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	/**
	 * @return the httpUserAgent
	 */
	public String getHttpUserAgent() {
		return httpUserAgent;
	}

	/**
	 * @param httpUserAgent the httpUserAgent to set
	 */
	public void setHttpUserAgent(String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}

	/**
	 * @return the authUsername
	 */
	public String getAuthUsername() {
		return authUsername;
	}

	/**
	 * @param authUsername the authUsername to set
	 */
	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}

	/**
	 * @return the authPassword
	 */
	public String getAuthPassword() {
		return authPassword;
	}

	/**
	 * @param authPassword the authPassword to set
	 */
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	/**
	 * @return the httpServicedocumentUrl
	 */
	public String getHttpServicedocumentUrl() {
		return httpServicedocumentUrl;
	}

	/**
	 * @param httpServicedocumentUrl the httpServicedocumentUrl to set
	 */
	public void setHttpServicedocumentUrl(String httpServicedocumentUrl) {
		this.httpServicedocumentUrl = httpServicedocumentUrl;
	}

	/**
	 * @return the httpDepositUrl
	 */
	public String getHttpDepositUrl() {
		return httpDepositUrl;
	}

	/**
	 * @param httpDepositUrl the httpDepositUrl to set
	 */
	public void setHttpDepositUrl(String httpDepositUrl) {
		this.httpDepositUrl = httpDepositUrl;
	}

	



	
}



