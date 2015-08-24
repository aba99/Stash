package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.task.LaunchHourPattern;
import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskPlanifiedData;


public class FixLWofMultiRunSessions {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();

//this code changes the LW to be one minute less than the frequency time on multiple run session
	
	public static void main(String[] args) {
		
		String connectFile = args[0];
		String sessionFilters = args[1];
		
		connect(connectFile);

		ArrayList<String> sesfilters=new ArrayList<String>();

		try {
			
			CSVReader reader = new CSVReader(new FileReader(sessionFilters),',', '\"', '\0');
			
			String [] line;			
		
			//parse lines
			while ((line = reader.readNext()) != null) 
		    {	    	
		        for(int i=0;i<line.length;i++)
		        {
		        	sesfilters.add(line[i].trim());
		        }
		    }	
			
			reader.close();
			

			for (String connKey : DUAPIConnections.keySet()) {
				
				DuApiConnection conn = DUAPIConnections.get(connKey);

				HashMap<String,Task> tasks = conn.getTaskHashMap_from_outside();
	
				for(String tsk:tasks.keySet())
				{
						if(sesfilters.contains(tasks.get(tsk).getSessionName()))
						{
					       TaskPlanifiedData tpd = new TaskPlanifiedData ();
				            
					       tpd=(TaskPlanifiedData)tasks.get(tsk).getSpecificData();
					       
				            LaunchHourPattern[] launchHourPatterns =tpd.getLaunchHourPatterns() ;         

						       if(launchHourPatterns.length>0)
						       {
						    	   int currentFreqInMin=launchHourPatterns[0].getFrequency();
						    	   
						    	   String initialLW=launchHourPatterns[0].getDurationHour()+"h "+launchHourPatterns[0].getDurationMinute()+"min";
						    	   
						    	   
						    	   if((currentFreqInMin<=60) && (currentFreqInMin>=1))
							    	   {
						    		   		if(currentFreqInMin==1)
						    		   		{	
						    		   			launchHourPatterns[0].setDurationMinute(currentFreqInMin);
						    		   		}
						    		   		else
						    		   		{
						    		   			launchHourPatterns[0].setDurationMinute((currentFreqInMin-1));
						    		   		}
							    	   
						    		   		launchHourPatterns[0].setDurationHour(0);
						    	   }
						    	   else
						    	   {
						    		   int LWhours=(currentFreqInMin/60);
						    		   launchHourPatterns[0].setDurationHour(LWhours);
						    		   launchHourPatterns[0].setDurationMinute((currentFreqInMin%60)-1);
						    	   }

						    	   tpd.setLaunchHourPatterns (launchHourPatterns);

						    	   tasks.get(tsk).setSpecificData(tpd);
						    	   tasks.get(tsk).update();
						    	   System.out.println("[TSK] "+tasks.get(tsk).getIdentifier().getName()+" : LW updated from "+initialLW+" -> "+tpd.getLaunchHourPattern(0).getDurationHour()+"h "+tpd.getLaunchHourPattern(0).getDurationMinute()+"min");
						       }
						       else
						       {
						    	   continue;
						       }
							
						}
					}
				}
				
			
			
			
		
		}catch (Exception e) {
			e.printStackTrace();

		}

	}

	private static void connect(String connectFile) {

		// / Reading connectFile info
		Scanner sc = null;
		try {
			sc = new Scanner(new File(connectFile));
			while (sc.hasNext()) {
				String line = sc.nextLine().trim();
				if (line.length() > 0)
					if (!line.startsWith("#"))
						if (line.contains("="))
							config_params.put(line.split("=")[0].trim()
									.toUpperCase(),
									line.substring(line.indexOf("=") + 1)
											.trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

		if (!config_params.containsKey("NODE")
				|| !config_params.containsKey("PORT")
				|| !config_params.containsKey("AREA")
				|| !config_params.containsKey("HOST")
				|| !config_params.containsKey("USER")
				|| !config_params.containsKey("PASSWORD")) {
			System.out.println("Error ! Missing fields in UVMS Config   :\""
					+ connectFile + "\"");
			System.exit(-1);
		}

		String nodes[] = config_params.get("NODE").split(",");

		for (int i = 0; i < nodes.length; i++) {

			DuApiConnection conn = new DuApiConnection(nodes[i],
					config_params.get("AREA"), config_params.get("HOST"),
					Integer.parseInt(config_params.get("PORT")),
					config_params.get("USER"), config_params.get("PASSWORD"));
			if (!DUAPIConnections.containsKey(nodes[i] + "/"
					+ config_params.get("AREA"))) {
				DUAPIConnections.put(
						nodes[i] + "/" + config_params.get("AREA"), conn);

			}
			continue;
		}
	}

	
}
