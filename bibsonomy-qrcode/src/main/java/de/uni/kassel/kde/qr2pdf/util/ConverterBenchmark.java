package de.uni.kassel.kde.qr2pdf.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import de.uni.kassel.kde.qr2pdf.util.converter.Converter;

public class ConverterBenchmark {

	private long timeToConvert;
	private long totalTime;
	
	private Converter converter;
	private String dirName;
	private int numberOfData;
	private MyLogger logger;
	
	private int succesfullConversions;
	
	public ConverterBenchmark(Converter converter, String dirName, MyLogger logger)
	{
		this.timeToConvert = 0;
		this.totalTime = 0;
		
		this.converter = converter;
		this.dirName = dirName;
		this.numberOfData = 0;
		this.logger = logger;
		
		this.succesfullConversions = 0;
	}
	
	public void benchmark()
	{
		File file = new File(dirName);
		
		if(file.isDirectory())
		{
		
			List<File> list = Arrays.asList(file.listFiles());
			
			for(File entry : list)
			{
				if(entry.getName().endsWith(".pdf"))
				{
					try {
						timeToConvert = System.currentTimeMillis();
						converter.convertToImage(entry.getAbsolutePath());
						timeToConvert = System.currentTimeMillis() - timeToConvert;
						
						this.succesfullConversions++;
						this.numberOfData++;
						
						this.logger.getOut().println("Konvertierzeit: " + timeToConvert/1000.0 + "s");
						this.totalTime += timeToConvert;
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						this.logger.getOut().println("Konvertierzeit:");
					}
				}
			}
			
			if(this.succesfullConversions > 0)
			{
				this.logger.getOut().println();
				this.logger.getOut().println("Erfolgreich konvertiert: " + this.succesfullConversions + "/" + this.numberOfData);
				
				this.logger.getOut().println("Durchschnittliche Laufzeit: " + this.totalTime/this.succesfullConversions/1000.0 + "s");		
				this.logger.getOut().println("Gesamtlaufzeit: " + this.totalTime/1000.0 + "s");
			}
		}
	}
	
}
