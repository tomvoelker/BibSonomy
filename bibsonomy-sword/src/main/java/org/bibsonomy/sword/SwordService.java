package org.bibsonomy.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDException;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.client.Client;
import org.purl.sword.client.PostMessage;
import org.purl.sword.client.SWORDClientException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


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
		File swordZipFile = null;
		log.info("starting sword");
				
		//SwordUser defaultuser = new SwordUser("stefani","adf57971843dac5fec32d8b2b799bd8a"); // localhost:8080
		//SwordUser defaultuser = new SwordUser("stefani","273726d18e9f123d89fcd52debde788c"); // puma.uni-kassel.de
		SwordUser defaultuser = new SwordUser("bugsbunny","e93c5f86a07cdeebd951b8f32e9bb3f6"); // www.bibsonomy.org
		
		
		
		SwordUser user = defaultuser;
		
		configureService(args, service);

		log.info("retrievePost");
		swordZipFile = service.retrieveSwordPost(user);
		
//		if (swordPost.hasBibTexPost()) { 
		

		System.out.println("Hallo");
		
		
		// get an instance of SWORD-Client
		Client swordClient = new Client();
		
		// configure sword
		//String swordHttpServer = "dspace.swordapp.org";  //http://dspace.swordapp.org/sword/servicedocument
		String swordHttpServer = "bib-pc152.bibliothek.uni-kassel.de";  //http://dspace.swordapp.org/sword/servicedocument
		int swordHttpPort = 8080;
		String swordHttpUserAgent = "PumaDevSst";
		//String swordHttpUrl = "http://dspace.swordapp.org/sword/servicedocument"; 
		//String swordHttpUser = "stefani@bibliothek.uni-kassel.de";
		//String swordHttpPassword = "sspuma10";
		String swordHttpUrl = "/sword/servicedocument";
		//String swordHttpUser = "dspace-master@bibliothek.uni-kassel.de";
		//String swordHttpPassword = "vacuum05";

		String swordHttpUser = "stefani@bibliothek.uni-kassel.de";
		String swordHttpPassword = "123abcd";
		
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
		swordMessage.setFilepath(swordZipFile.getAbsolutePath());
		swordMessage.setFiletype("application/zip");
		swordMessage.setFormatNamespace("http://purl.org/net/sword-types/METSDSpaceSIP"); // sets packaging!
		swordMessage.setVerbose(true);

		
		try {
			// get Service Document 
			swordClient.setServer(swordHttpServer, swordHttpPort);
			log.info("serviceDocument-setUA: ");
			swordClient.setUserAgent(swordHttpUserAgent);
			log.info("serviceDocument-setCred: ");
			swordClient.setCredentials(swordHttpUser, swordHttpPassword);
			log.info("get serviceDocument: ");
			ServiceDocument serviceDocument = swordClient.getServiceDocument(swordHttpUrl);
			log.info("serviceDocument: " + serviceDocument.toString());

			
			//swordHttpUrl = "http://dspace.swordapp.org/sword/deposit/123456789/4"; // Research materials
			swordHttpUrl = "http://bib-pc152.bibliothek.uni-kassel.de:8080/sword/deposit/urn:nbn:de:hebis:34-6033";
			
			swordMessage.setDestination(swordHttpUrl);
			
			System.out.println(swordMessage.getPackaging());
			
			
			depositResponse = swordClient.postFile(swordMessage);
			try {
				log.info("depositResponse: "+ depositResponse.getErrorDocument().getErrorURI());
/*
				System.out.println(depositResponse.getErrorDocument().getErrorURI());
				System.out.println(depositResponse.getErrorDocument().getId());
				System.out.println(depositResponse.getErrorDocument().getPackaging());
				System.out.println(depositResponse.getErrorDocument().getPublished());
				System.out.println(depositResponse.getErrorDocument().getQualifiedName());
				System.out.println(depositResponse.getErrorDocument().getTreatment());
				System.out.println(depositResponse.getErrorDocument().getUserAgent());
				System.out.println(depositResponse.getErrorDocument().getTitle());
*/				
			} catch (SWORDException e) {
				e.printStackTrace();
			}
			
			
		} catch (SWORDClientException e) {
			log.warn("SWORDClientException");
			e.printStackTrace();
		}

		
//		swordZipFile		
		
		
/*		} else {
			// no posts retrieved
			log.warn("got no posts for user "+user.getUsername());
		}
*/
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
	private File retrieveSwordPost(SwordUser swordUser) {
		SwordPost swordPost = new SwordPost();
		Post<BibTex> bibTexPost = null;
		File zipFilename = null; 
		
//		String resourceHash = "545d2292ba08ff5ec34e9ae8e9c5b314"; // localhost 
		String resourceHash = "f6228cdd07e87a6e903bb21eb4e86a59"; // bibsonomy
//		String resourceHash = "1b6dcdc28691f73b443371af0feeca9e"; // puma

//		String apiUrl = "http://localhost:8080/api/";
		String apiUrl = "http://www.bibsonomy.org/api/";
//		String apiUrl = "http://puma.uni-kassel.de/api/";
		
		
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

			if (post.getResource() instanceof BibTex) {
				
				// fileprefix
				String pathToWorkDir = "/tmp/"; 
				
				// Destination directory 
				File destinationDirectory = new File(pathToWorkDir+username+"_"+post.getResource().getIntraHash());

				// zip-filename
				zipFilename = new File(destinationDirectory.getAbsoluteFile()+"/"+"pumapublication_"+username+"_"+post.getResource().getIntraHash()+".zip");

				byte[] buffer = new byte[18024];
				
				bibTexPost = (Post<BibTex>) post;
				swordPost.setBibTexPost(bibTexPost);

				log.info("getIntraHash = " + bibTexPost.getResource().getIntraHash());

				
				/*
				 * get documents
				 */
				// get documents for post
				final List<Document> documents = ((BibTex) post.getResource()).getDocuments();
				System.out.println(documents);
				
				try {
					// create directory
				    boolean mkdir_success = (new File(destinationDirectory.getAbsolutePath())).mkdir();
				    if (mkdir_success) {
				      System.out.println("Directory: " + destinationDirectory.getAbsolutePath() + " created");
				    }    
					
					
					// open zip archive to add files to  
					System.out.println("zipFilename: "+zipFilename);
					ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilename));
	
					ArrayList fileList = new ArrayList();
					
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
							e.printStackTrace();
						}
					*/
	
						// move file to user folder with username_resource-hash as folder name
							
						// File (or directory) to be moved 
						File fileToMove = new File(document2.getFile().getAbsolutePath()); 
		
						fileList.add(fileToMove.getName());
						
						// Move file to new directory 
						boolean rename_success = fileToMove.renameTo(new File(destinationDirectory, fileToMove.getName())); 
						if (!rename_success) { 
							// File was not successfully moved } 
							log.info("File was not successfully moved: "+fileToMove.getName());
						}
						
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
							
							
					}

				
					// create meta data structure
					BibTex bibTexPostResource = (BibTex) post.getResource();
					System.out.println(bibTexPostResource.getAuthor());
					System.out.println(bibTexPostResource.getYear());
					System.out.println(bibTexPostResource.getBibtexKey());
					System.out.println(bibTexPostResource.getEntrytype());

					// write meta data into zip archive
					ZipEntry zipEntry = new ZipEntry("mets.xml");
					zipOutputStream.putNextEntry(zipEntry);				

					
					// create XML-Document
					// PrintWriter from a Servlet
					StreamResult streamResult = new StreamResult(zipOutputStream);

					Map<String, String> metadataMap = new HashMap<String, String>();
					metadataMap.put("Vorname", "Hans");
					metadataMap.put("Name", "Mustermann");
					metadataMap.put("Geburtstag", "01.01.01");
					metadataMap.put("Wohnort", "Musterstadt");

					
					MetsGenerator metsGenerator = new MetsGenerator();
					metsGenerator.setFilenameList(fileList);
					//metsGenerator.setMetadata(metadataMap);
					metsGenerator.setMetadata(bibTexPost);
					metsGenerator.setResult(streamResult);

			    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			    	String currentTime = df.format(cal.getTime());

					zipOutputStream.closeEntry();
						
					// close zip archive  
					zipOutputStream.close();
									
					System.out.println("saved to "+zipFilename.getPath());
					
					} catch (MalformedURLException e) {
						// e.printStackTrace();
						log.info("MalformedURLException! " + e.getMessage());
					} catch (SAXException e) {
						// thrown by metsWrapper.validate();
						log.info("SAXException! " + e.getMessage());
					} catch (IOException e) {
						//e.printStackTrace();
						log.info("IOException! " + e.getMessage());
					} catch (TransformerConfigurationException e) {
						//e.printStackTrace();
						log.info("TransformerConfigurationException! " + e.getMessage());
					}
					
					
				
			}			
			
		
		
		} catch (ResourceNotFoundException e) {
			// e.printStackTrace();
			log.warn("ResourceNotFoundException! SwordService-retrievePost");
		} catch (ResourceMovedException e) {
			//e.printStackTrace();
			log.warn("ResourceMovedException! SwordService-retrievePost");
		}
		
		
		
		
		return zipFilename;
	}

    static private org.w3c.dom.Document createMODS(String title, String genre) throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        Element root = doc.createElementNS("http://www.loc.gov/mods/v3", "mods");
        doc.appendChild(root);
        
        Element ti = doc.createElement("titleInfo");
        Element t = doc.createElement("title");
        t.setTextContent(title);
        ti.appendChild(t);
        root.appendChild(ti);
        
        Element g = doc.createElement("genre");
        g.setTextContent(genre);
        root.appendChild(g);
        
        return doc;
    }	
	
	public boolean isDirectDatabaseAccess() {
		return directDatabaseAccess;
	}
	public void setDirectDatabaseAccess(boolean directDatabaseAccess) {
		this.directDatabaseAccess = directDatabaseAccess;
	}
	

	
}



