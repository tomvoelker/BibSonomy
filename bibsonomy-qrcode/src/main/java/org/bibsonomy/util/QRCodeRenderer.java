package org.bibsonomy.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class QRCodeRenderer {
	
	private String projectHome = null; 
	
	public String manipulate(String filePath, String requestedUser, String intraHash) throws Exception
	{		
		final String encodee = projectHome + "bibtex/" + intraHash + "/" + requestedUser;
		
		ExecutorService pool = Executors.newFixedThreadPool(1);
		
		Future<String> embedderFuture = pool.submit(new QRCodeEmbedder(filePath, encodee));
		
		String manipulatedFilePath = embedderFuture.get(QRCodeEmbedder.WAIT_TIME, TimeUnit.MILLISECONDS);
			
		return manipulatedFilePath;
	}

	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

}
