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
import com.orsyp.api.task.TaskType;


public class LW_Changer {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();

//this code changes the LW to be one minute less than the frequency time on multiple run session
	
	public static void main(String[] args) {
		
		String connectFile = args[0];
		String sessionFilters = args[1];
		String oldLWStart = "170000";
		String newLWStart= "191500";
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
							if(!tasks.get(tsk).getTaskType().equals(TaskType.Scheduled) && !tasks.get(tsk).getTaskType().equals(TaskType.Optional))
							{
								System.out.println("Skipping LW update on TSK "+tasks.get(tsk).getIdentifier().getName()+" on "+conn.getConnName());
								continue;
							}
							
					       TaskPlanifiedData tpd = new TaskPlanifiedData ();
				           
					       
					       tpd=(TaskPlanifiedData)tasks.get(tsk).getSpecificData();
					       
				            LaunchHourPattern[] launchHourPatterns =tpd.getLaunchHourPatterns() ;         

						       if(launchHourPatterns.length>0)
						       {
						    	   if(launchHourPatterns[0].getStartTime().equalsIgnoreCase(oldLWStart))
						    	   {
						    		   launchHourPatterns[0].setStartTime(newLWStart);
						    		   tpd.setLaunchHourPatterns (launchHourPatterns);
						    		   tasks.get(tsk).setSpecificData(tpd);
						    		   tasks.get(tsk).update();
						    		   System.out.println("[TSK] "+tasks.get(tsk).getIdentifier().getName()+" : LW Start updated from "+oldLWStart+" -> "+tpd.getLaunchHourPattern(0).getStartTime());
						    	   }
						       }
						       else
						       {
									System.out.println("Skipping LW update on TSK "+tasks.get(tsk).getIdentifier().getName()+" on "+conn.getConnName());
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
					config_params.get("USER"), config_params.get("PASSWORD"),false,false,true);
			if (!DUAPIConnections.containsKey(nodes[i] + "/"
					+ config_params.get("AREA"))) {
				DUAPIConnections.put(
						nodes[i] + "/" + config_params.get("AREA"), conn);

			}
			continue;
		}
	}

	
}
