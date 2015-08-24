package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import com.orsyp.api.task.LaunchHourPattern;
import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskImplicitData;
import com.orsyp.api.task.TaskPlanifiedData;
import com.orsyp.api.task.TaskType;





public class Tasks_Dump {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();

	
	public static void main(String[] args) {
		
		String connectFile = args[0];
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmm");
		String formattedDate = sdf.format(date);
		
		//System.out.println(formattedDate); // 12/01/2011 4:48:16 PM

		connect(connectFile,false,false,true);

		String outputCsv = "tsk_dump_"+formattedDate+".csv";
		
		FileOutputStream fout=null;	
	    PrintStream prtstm=null;

	    try{ 
	    
	    	fout = new FileOutputStream (outputCsv);
	    	prtstm = new PrintStream(fout);		
		
	    	prtstm.println("Task,Session,Uproc,MU,Rule,LW Start,Status,Template,GenEv,Node");
			
	    	for(String connKey:DUAPIConnections.keySet())
			{
				DuApiConnection conn = DUAPIConnections.get(connKey);
			
				HashMap<String, Task> tskList;
				try {
					
					tskList = conn.getTaskHashMap_from_outside();
					
					for(String tskKey:tskList.keySet())
					{
						prtstm.println(tskList.get(tskKey).getIdentifier().getName()
								+","+tskList.get(tskKey).getIdentifier().getSessionName()
								+","+tskList.get(tskKey).getIdentifier().getUprocName()
								+","+tskList.get(tskKey).getIdentifier().getMuName()
								+","+getRule(tskList.get(tskKey))
								+","+getStartLW(tskList.get(tskKey))
								+","+tskList.get(tskKey).isActive()
								+","+tskList.get(tskKey).isTemplate()
								+","+getGenEvent(tskList.get(tskKey))
								+","+conn.getConnName());
					}
				
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			
			}	
	    }
	    catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private static void connect(String connectFile,boolean withUpr,boolean WithSes,boolean withTsk) {

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

		System.out.println("Connecting to UVMS : \""+config_params.get("HOST")+"\"");
		
		String nodes[] = config_params.get("NODE").split(",");

		for (int i = 0; i < nodes.length; i++) {

			DuApiConnection conn = new DuApiConnection(nodes[i],
					config_params.get("AREA"), config_params.get("HOST"),
					Integer.parseInt(config_params.get("PORT")),
					config_params.get("USER"), config_params.get("PASSWORD"),withUpr,WithSes,withTsk);
			if (!DUAPIConnections.containsKey(nodes[i] + "/"
					+ config_params.get("AREA"))) {
				DUAPIConnections.put(
						nodes[i] + "/" + config_params.get("AREA"), conn);

			}
			continue;
		}
	}
	
	private static String getRule(Task tsk) {
		if (tsk.getTaskType().equals(TaskType.Provoked)) {
			return "Provoked";
		} else {
			TaskPlanifiedData taskPlanifiedData = (TaskPlanifiedData) tsk
					.getSpecificData();

			if (taskPlanifiedData.getImplicitData() != null) {
				if (taskPlanifiedData.getImplicitData().length > 0) {
					TaskImplicitData taskImplicitData = taskPlanifiedData
							.getImplicitData()[0];
					return taskImplicitData.getName();
				} else {
					return "RULE_READ_ERROR";

				}

			} else {

				return "RULE_READ_ERROR";

			}
		}
	}
	
	
	
	
	
	private static String getStartLW(Task tsk){
		
		if(!tsk.getTaskType().equals(TaskType.Provoked))
		{
			TaskPlanifiedData tpd = new TaskPlanifiedData ();
    
			tpd=(TaskPlanifiedData)tsk.getSpecificData();
    
			LaunchHourPattern[] launchHourPatterns =tpd.getLaunchHourPatterns() ;         

		       if(launchHourPatterns.length>0)
		       {
		    	   return launchHourPatterns[0].getStartTime();
		       }
		       else
		       {
		    		 return "N/A";
		       }
		}
		else
		{
			return "Provoked";
		}
}
	
	
	private static boolean getGenEvent(Task tsk){
		
		if(!tsk.getTaskType().equals(TaskType.Optional))
		{
			return false;
		}
		else
		{
			TaskPlanifiedData tpd = new TaskPlanifiedData ();
		    
			tpd=(TaskPlanifiedData)tsk.getSpecificData();
			if(tpd!=null){
			return tpd.isGenerateEvent();
			}
			else
			{
				return false;
			}
		}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
