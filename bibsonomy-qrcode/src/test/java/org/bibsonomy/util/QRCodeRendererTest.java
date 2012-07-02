package org.bibsonomy.util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author pbu
 * @version $Id$
 */
public class QRCodeRendererTest {

	@Test
	public void testTemplatePDF() throws IOException {
		File template = new File("src/test/resources/template.pdf");
		
		if(!template.createNewFile()) {
			
			final String encodee = "http://localhost:8080/bibtex/2dfac402f7dac97c1b303bb53764ace82/derbeukatt";

			/*
			 * create executor service
			 */
			ExecutorService pool = Executors.newFixedThreadPool(1);

			QRCodeEmbedder embedder = new QRCodeEmbedder(template.getPath(), encodee);
			Future<String> embedderFuture = pool.submit(embedder);

			/*
			 * get result within 5 seconds or throw an exception
			 */
			String manipulatedFilePath = null;
			
			try {
				manipulatedFilePath = embedderFuture.get(QRCodeEmbedder.WAIT_TIME, TimeUnit.MILLISECONDS);
			} catch (final Exception e) {
				
				/*
				 * if embedding fails, safely shutdown executor and delete output file
				 */
				pool.shutdownNow();
				new File(template.getPath().concat(".qr")).delete();
				
				e.printStackTrace();
			}

			pool.shutdownNow();
			
			new File(template.getPath().concat(".qr")).delete();
			
			Assert.assertEquals(template.getPath().concat(".qr"), manipulatedFilePath);
			Assert.assertEquals(495.0f, embedder.getX());
			Assert.assertEquals(570.0f, embedder.getY());
			Assert.assertEquals(117, embedder.getSize());
		} else {
			template.delete();
		}
	}
	
	/**
	 * This test is only for debugging purposes. Only should be invoked when one
	 * has to search for embedding failures -> no asserts
	 * @throws IOException 
	 */
	@Test
	public void errorPDF() throws IOException {
		File error = new File("src/test/resources/error.pdf");
		
		if(!error.createNewFile()) {
			
			final String encodee = "http://localhost:8080/bibtex/2dfac402f7dac97c1b303bb53764ace82/derbeukatt";

			/*
			 * create executor service
			 */
			ExecutorService pool = Executors.newFixedThreadPool(1);

			QRCodeEmbedder embedder = new QRCodeEmbedder(error.getPath(), encodee);
			Future<String> embedderFuture = pool.submit(embedder);

			/*
			 * get result within 5 seconds or throw an exception
			 */
			try {
				embedderFuture.get(QRCodeEmbedder.WAIT_TIME, TimeUnit.MILLISECONDS);
			} catch (final Exception e) {
				
				/*
				 * if embedding fails, safely shutdown executor and delete output file
				 */
				pool.shutdownNow();
				new File(error.getPath().concat(".qr")).delete();
				
				e.printStackTrace();
			}

			pool.shutdownNow();
			
			new File(error.getPath().concat(".qr")).delete();
		} else {
			error.delete();
		}
	}
	
}
