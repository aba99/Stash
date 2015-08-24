package com.orsyp.client.amex;


import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;



import com.orsyp.tools.ps.Connector;



public class FileWatcherDispatcher {


	
	public static String cabrin_template = "CABRIN_TEMPLATE";
	public static String currentMu = "AMEX-E0";
	public static String currentUser = "casm_dellc";

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception  
	{		

		String refFile = args[0];
		String configFile = args[1];
		String path = args[2];
		
		Connector conn = new Connector(configFile,true,"CABRIN_",false,"",false,"");
		@SuppressWarnings("unused")
		CabrinProcess initialSeed = new CabrinProcess(conn.getConnectionList().get(0),cabrin_template,refFile);

		System.out.println("Watching following path '"+path+"'");
		
		Path faxFolder = Paths.get(path);
		
			
		WatchService watchService = FileSystems.getDefault().newWatchService();
			
		faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		int count=0;
			
		boolean valid = true;
			
		do {
				WatchKey watchKey = watchService.take();

				for (WatchEvent event : watchKey.pollEvents()) 
				{
					@SuppressWarnings("unused")
					WatchEvent.Kind kind = event.kind();
					
					if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) 
					{
						String fileName = event.context().toString();
						System.out.println((count+1)+"- File Read:" + fileName);
						count++;

						if(CabrinProcess.hasEntry(fileName) && fileName.endsWith(".TRIGGER"))
						{	// if name.googleNumber where name is a key in the reference table
						
							String fileNameEntry = CabrinProcess.getGenericKeyFileName(fileName);//to get the key from the reference table
							
							if(fileNameEntry!=null)
							{

								ArrayList<String>listOfJobs = CabrinProcess.getReferenceTable().get(fileNameEntry);
								//this is where we fetch the list of jobs that
								//need to be triggered by this file
								
								for(int j=0;j<listOfJobs.size();j++)
								{
									String targetName = "CABRIN_"+listOfJobs.get(j).toUpperCase();
									
									CabrinProcess cabproc = new CabrinProcess(fileName,targetName,listOfJobs.get(j).toUpperCase());

									//CabrinProcess.duapi.createLaunch(cabproc.getCabrinJob(), currentUser, currentMu);
									CabrinProcess.fireAwayCabrinProcesses(cabproc, currentUser, currentMu);					
									
								}
									
									
							}
								
						}
							
							
					}
				}
				
				valid = watchKey.reset();

			} while (valid);

	}

	
}
