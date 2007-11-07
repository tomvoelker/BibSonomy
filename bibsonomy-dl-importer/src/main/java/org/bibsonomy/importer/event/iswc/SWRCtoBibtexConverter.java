package org.bibsonomy.importer.event.iswc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.bibsonomy.importer.event.iswc.exceptions.RepositoryException;
import org.bibsonomy.importer.event.iswc.model.Publication;
import org.bibsonomy.importer.event.iswc.rdf.RDFRepository;

/**
 * Main class of the SWRC to BibTeX converter. It starts the whole process.  
 * @author tst
 */
public class SWRCtoBibtexConverter {
	

	/**
	 * Main method
	 * @param args start parameters: first must be the path to the RDF file
	 */
	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.err.println("usage:");
			System.err.println("  " + SWRCtoBibtexConverter.class.getName() + " file");
			System.exit(1);
		}
		
		// init parameters
		String rdfPath = null;
		rdfPath = args[0];
		String dir = null;
		dir = args[1]; 
		

		// start convertion
		SWRCtoBibtexConverter converter = new SWRCtoBibtexConverter();
		try {
			converter.convertToBibtex(rdfPath, dir);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Convertion launcher. It starts the
	 * extraction of the {@link Publication}s out of the given RDF. Then it builds the bibtex 
	 * and print it.
	 * @param rdfPath path to a RDF file, which contains the Publications
	 * @throws RepositoryException Failure during processing the RDF data
	 * @throws IOException error during writing the bibtex files 
	 */
	public void convertToBibtex(String rdfPath, String dir) throws RepositoryException, IOException{
		
		RDFRepository repository = new RDFRepository(rdfPath);
		
		// get proceedings
		List<Publication> proceedings = repository.getProceedings();
		
		// get inproceedings
		List<Publication> inproceedings = repository.getInproceedings();
		
		// group inproceedings by proceedings
		for(Publication proceeding: proceedings){
			
			/*
			 * write proceeding with its inproceedings in a file with a modified proceeding URI
			 * (cut off http:// and replace all "/" with "_")
			 */ 
			File proceedingFile = new File(dir + "/" + proceeding.getBibtexkey().substring(7).replaceAll("/", "_") + ".bib");
			Writer writer = new OutputStreamWriter(new FileOutputStream(proceedingFile), "utf-8");
			

			// write proceedings on top of the file
			writer.write(BibtexHelper.buildBibtex(proceeding));
			for(Publication inproceeding: inproceedings){
				if(inproceeding.getCrossref().equals(proceeding.getBibtexkey()))
					writer.write(BibtexHelper.buildBibtex(inproceeding));
			}
			
			// cleanup
			writer.flush();
			writer.close();
		}

		// write into: rest_with_invalid_crossref.bib
		File inproceedingFile = new File(dir + "/rest_with_invalid_crossref.bib");
		Writer writer = new OutputStreamWriter(new FileOutputStream(inproceedingFile), "utf-8");

		// write all inproceedings without a valid proceedings in extra file
		for(Publication inproceeding: inproceedings){
			// check if inproceeding has a extracted proceeding
			boolean hasProceeding = false;
			for(Publication proceeding: proceedings)
				if(inproceeding.getCrossref().equals(proceeding.getBibtexkey()))
					hasProceeding = true;
			
			// of no proceddings is found, then write it to this file
			if(!hasProceeding)
				writer.write(BibtexHelper.buildBibtex(inproceeding));
		}

		// cleanup
		writer.flush();
		writer.close();

	}

}
