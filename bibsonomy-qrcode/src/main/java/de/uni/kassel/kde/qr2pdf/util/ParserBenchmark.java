package de.uni.kassel.kde.qr2pdf.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import de.uni.kassel.kde.qr2pdf.util.parser.Parser;

public class ParserBenchmark {

	private long timeToConvert;
	private long totalTime;
	
	private Parser parser;
	private String dirIn;
	private String dirOut;
	private int numberOfData;
	private MyLogger logger;
	
	private int succesfullParses;
	
	
	public ParserBenchmark(Parser parser, String dirIn, String dirOut, MyLogger logger)
	{
		this.timeToConvert = 0;
		this.totalTime = 0;
		
		this.parser = parser;
		this.dirIn = dirIn;
		this.dirOut = dirOut;
		this.numberOfData = 0;
		this.logger = logger;
		
		this.succesfullParses = 0;
	}
	
	public void benchmark()
	{
		File file = new File(dirIn);
		
		if(file.isDirectory())
		{
		
			List<File> list = Arrays.asList(file.listFiles());
			
			for(File entry : list)
			{
				if(entry.getName().endsWith(".pdf"))
				{
					try {
						timeToConvert = System.currentTimeMillis();
						//converter.convertToImage(dirName + String.valueOf(i) + ".pdf");
						parser.parse(entry.getAbsolutePath(), dirOut + entry.getName());
						timeToConvert = System.currentTimeMillis() - timeToConvert;
						
						this.succesfullParses++;
						this.numberOfData++;
						
						this.logger.getOut().println("Parserzeit Lauf: " + timeToConvert/1000.0 + "s");
						this.totalTime += timeToConvert;
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						this.logger.getOut().println("Parserzeit Lauf:");
					}
				}
			}
			
			this.logger.getOut().println();
			this.logger.getOut().println("Erfolgreich geparsed: " + this.succesfullParses + "/" + this.numberOfData);
			
			this.logger.getOut().println();
			this.logger.getOut().println("Durchschnittliche Laufzeit: " + this.totalTime/this.succesfullParses/1000.0 + "s");		
			this.logger.getOut().println("Gesamtlaufzeit: " + this.totalTime/1000.0 + "s");
		}
	}

}
