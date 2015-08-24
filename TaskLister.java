package my.tools;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.orsyp.api.task.LaunchHourPattern;
import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskImplicitData;
import com.orsyp.api.task.TaskPlanifiedData;
import com.orsyp.api.task.TaskType;


public class TaskLister {

	public static void main(String[] args) {

		String configFile = args[0];
		try {
			
			Connector myconnection = new Connector(configFile,false,"",false,"",true,"");
			
			int count=0;
			
			for(int i=0;i<myconnection.getConnectionList().size();i++)
			{
				try {
					
					for(String tsk:myconnection.getConnectionList().get(i).getTaskMultiMap_from_outside().keySet())
					{
						ArrayList<Task> curList = new ArrayList<Task>(myconnection.getConnectionList().get(i).getTaskMultiMap_from_outside().get(tsk));
					
						for(int t=0;t<curList.size();t++)
						{
							Task tk = curList.get(t);
							
							
							String startTime = getStartLW(tk);
							
							if(!startTime.equalsIgnoreCase("Provoked") && !startTime.equalsIgnoreCase("N/A") && (Integer.parseInt(startTime.substring(0,2))>=8) 
									&& (Integer.parseInt(startTime.substring(0,2))<=10))
							{
								count++;
							System.out.println(count+" - "+tk.getIdentifier().getName()+" - "+startTime+" - "+getRule(tk));
							
							}
							
						}
					}
		
				} catch (Exception e) {
					
					e.printStackTrace();
				}

			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
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

	
}
