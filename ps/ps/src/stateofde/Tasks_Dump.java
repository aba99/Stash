package stateofde;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.session.Session;
import com.orsyp.api.task.LaunchHourPattern;
import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskImplicitData;
import com.orsyp.api.task.TaskPlanifiedData;
import com.orsyp.api.task.TaskType;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.owls.impl.Connector;





public class Tasks_Dump {

	static HashMap<String,String> oldUpr_newUpr = new HashMap<String,String>();

	static HashMap<String,Uproc> uprs = new HashMap<String,Uproc>();
	static HashMap<String,Session> sess = new HashMap<String,Session>();
	static HashMap<String,Task> tsks = new HashMap<String,Task>();
	 
	static Date date = new Date();
	static SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmm");
	static String formattedDate = sdf.format(date);
	
	public static void main(String[] args) throws Exception {
		
		String connectFile = args[0];
		String file = args[1];
		
		readMapFile(file);//reads the mapping
		//System.out.println(formattedDate); // 12/01/2011 4:48:16 PM

		Connector myNode = new Connector(connectFile,true,"",true,"",true,"");

		uprs= myNode.getConnectionList().get(0).getUprocHashMap_from_outside();
		sess= myNode.getConnectionList().get(0).getSessionsHashMap_from_outside();
		tsks= myNode.getConnectionList().get(0).getTaskHashMap_from_outside();

		String outputCsv = "tsk_dump_"+formattedDate+".csv";
		
		FileOutputStream fout=null;	
	    PrintStream prtstm=null;

	    try{ 
	    
	    	fout = new FileOutputStream (outputCsv);
	    	prtstm = new PrintStream(fout);		
		
	    	prtstm.println("Task,Session,Uproc,FatherUproc,DepCons,Command,MU,Rule,LW Start,Status,Template,GenEv,Node");
			
	    	
			
				HashMap<String, Task> tskList;
				
					
					tskList = myNode.getConnectionList().get(0).getTaskHashMap_from_outside();
					
					for(String tskKey:tskList.keySet())
					{
						prtstm.println(tskList.get(tskKey).getIdentifier().getName()
								+","+tskList.get(tskKey).getIdentifier().getSessionName()
								+","+tskList.get(tskKey).getIdentifier().getUprocName()
								+","+myNode.getConnectionList().get(0).getFatherUproc(
										
										tskList.get(tskKey).getIdentifier().getSessionName(),
										tskList.get(tskKey).getIdentifier().getUprocName()
										)
								
								
								+myNode.getConnectionList().get(0).getUprocDepsAndVariablesToString(
										tskList.get(tskKey).getIdentifier().getUprocName(),oldUpr_newUpr)
								
										+","+tskList.get(tskKey).getIdentifier().getMuName()
								+","+getRule(tskList.get(tskKey))
								+","+getStartLW(tskList.get(tskKey))
								+","+tskList.get(tskKey).isActive()
								+","+tskList.get(tskKey).isTemplate()
								+","+getGenEvent(tskList.get(tskKey))
								+","+myNode.getConnectionList().get(0).getConnName());
					}
				
				} catch (Exception e) {
					
					e.printStackTrace();
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
	
	
	public static void readMapFile(String file) throws IOException
{
	CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
	
	String [] line;			

	//parse lines
	while ((line = reader.readNext()) != null) 
    {	    	
        if(line.length==2)
        {
        	if(!line[0].trim().equalsIgnoreCase(line[1].trim()) && !oldUpr_newUpr.containsKey(line[0]))
        	{
        		oldUpr_newUpr.put(line[0].trim(),line[1].trim());
        	}
        }
    }	
	
	
	reader.close();
}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
