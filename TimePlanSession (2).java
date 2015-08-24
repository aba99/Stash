package atco.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.orsyp.UniverseException;
import com.orsyp.api.rule.Rule;
import com.orsyp.api.session.Session;
import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskImplicitData;
import com.orsyp.api.task.TaskPlanifiedData;
import com.orsyp.api.task.TaskType;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.util.DateTools;


public class TimePlanSession {

	private boolean sessionFound=false;
	private boolean mainTaskFound=false;
	private int numberOfOptionalTasks=0;
	private String sessionName;
	private Session session;
	private static DuApiConnection con;
	private  HashMap<String,String> mainTasks=new HashMap<String,String>();// key:MUName  value:TaskObject
	private  Multimap<String, String> optTasks = ArrayListMultimap.create();// key:MUName value:TaskObject 
	private  HashMap<String,String> publicMFJobID_FreqLookUp=new HashMap<String,String>();
	private  HashMap<String,TPUproc> tpUprocs=new HashMap<String,TPUproc>();
	
	public TimePlanSession(String sessName,HashMap<String,String>lookUpTableJobIDfreq,DuApiConnection duConn)
	{
		try {
				con= duConn;

				
				if(con.getSessionsHashMap_from_outside().containsKey(sessName))
				{
					sessionFound=true;
					session = con.getSessionsHashMap_from_outside().get(sessName);
					sessionName = session.getName();
					publicMFJobID_FreqLookUp=lookUpTableJobIDfreq; 
				
					for(int su=0;su<session.getUprocs().length;su++)
					{//this for loop does the following : 
						//a) it checks each and every uproc of the session for a jobID
						//b) checks if upr actually exists , if not it just skips it 
						//c) if upr is a legit upr that actually exists , then it checks for the JOBID matchup
						//c) each time it finds an equivalentID, it bundles up <jobID,MfFreq,UprocObject> in a TPUproc;
						
						String currentUpr = session.getUprocs()[su];
						
						if(con.getUprocHashMap_from_outside().containsKey(currentUpr))
						{
							for(String jid:publicMFJobID_FreqLookUp.keySet())
							{
								if(currentUpr.contains(jid))//found JobID
								{
									Uproc matchedUpr = con.getUprocHashMap_from_outside().get(currentUpr);
									TPUproc matchedTPUproc = new TPUproc(jid,publicMFJobID_FreqLookUp.get(jid),matchedUpr);
									tpUprocs.put(matchedUpr.getName(),matchedTPUproc );
								}
							}
						}
					}// tpUprocs should have all uprocs in the session with jobID that are in the initial list coming from
					// mainframe . tpUprocs will be our main source info to compare "what is the jobID's schedule in MF"
					// vs "what's the jobId's schedule in $U" 
				
					
					for(String tsk:con.getTaskHashMap_from_outside().keySet())
					{
						if(con.getTaskHashMap_from_outside().get(tsk).getSessionName().equalsIgnoreCase(sessionName))
						{
							if(con.getTaskHashMap_from_outside().get(tsk).getUprocName().equalsIgnoreCase(session.getHeader()))
							{
									mainTasks.put(con.getTaskHashMap_from_outside().get(tsk).getMuName(), con.getTaskHashMap_from_outside().get(tsk).getIdentifier().getName());
							}
							else
							{
									optTasks.put(con.getTaskHashMap_from_outside().get(tsk).getMuName(), con.getTaskHashMap_from_outside().get(tsk).getIdentifier().getName());
							}
						}
					}
				
					if(mainTasks.size()!=0)
					{
						mainTaskFound=true;
					}
					else
					{
						System.out.println("[MAIN TSK] for [SES] "+ sessName+" not found on "+con.getConnName() );

					}
					
				}
				else
				{
					System.out.println("[SES] "+ sessName+" not found on "+con.getConnName() );
				}
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private  void deleteJunkOptTsk() throws Exception
	{//deletes optTasks that are not linked to a main task , and also deletes the optTasks that have the same rule as main task;
		if(sessionFound && mainTaskFound)
		{
			Set<String> MU_mainTasks = new HashSet<String>( mainTasks.keySet());
			Set<String> MU_optTasks = new HashSet<String> (optTasks.keySet());
			
			for(String optMu:MU_optTasks)
			{
				ArrayList<String> listOfOptTaskNames = new ArrayList<String>(optTasks.get(optMu));
				
			/*	if(!MU_mainTasks.contains(optMu))
				{
					for(int opt=0;opt<listOfOptTaskNames.size();opt++)
					{
						
						if(con.getTaskHashMap_from_outside().containsKey(listOfOptTaskNames.get(opt)))
						{
							Task curOptTask = con.getTaskHashMap_from_outside().get(listOfOptTaskNames.get(opt));
							
							curOptTask.delete();
							System.out.println("Deleted [OPT TSK] "+listOfOptTaskNames.get(opt)+ ": Not linked to a [MAIN TSK] on "+con.getConnName());
						}
						else
						{
							optTasks.get(optMu).remove(listOfOptTaskNames.get(opt));
						}
					}
					optTasks.removeAll(optMu);
					
				}
				else
				{*/
				if(MU_mainTasks.contains(optMu)){
					for(int opt=0;opt<listOfOptTaskNames.size();opt++)
					{
						if(con.getTaskHashMap_from_outside().containsKey(listOfOptTaskNames.get(opt)))
						{
							Task curOptTask = con.getTaskHashMap_from_outside().get(listOfOptTaskNames.get(opt));
							Task curMainTask= con.getTaskHashMap_from_outside().get(mainTasks.get(optMu));
							
							if(getRule(curOptTask).equalsIgnoreCase(getRule(curMainTask)) || getRule(curOptTask).equalsIgnoreCase("EVERYDAY"))
							{
								curOptTask.delete();
								optTasks.get(optMu).remove(listOfOptTaskNames.get(opt));
								System.out.println("Deleted [OPT TSK] "+listOfOptTaskNames.get(opt)+ " with [RULE] "+getRule(curOptTask)+" on "+con.getConnName());
	
							}
						}
						else
						{
							optTasks.get(optMu).remove(listOfOptTaskNames.get(opt));
						}
					}
					
				}
				
			}
		}
		
	}
	
	private  void fixRuleOnRemainingOptTasks() throws UniverseException, Exception
	{
		if(sessionFound && mainTaskFound)
		{
			
			Set<String> MU_optTasks = new HashSet<String>(optTasks.keySet());
			
			for(String optMu:MU_optTasks)
			{
				ArrayList<String> listOfOptTaskNames = new ArrayList<String>(optTasks.get(optMu));
				
				for(int opt=0;opt<listOfOptTaskNames.size();opt++)
				{
					if(con.getTaskHashMap_from_outside().containsKey(listOfOptTaskNames.get(opt)))
					{
						Task curOptTask = con.getTaskHashMap_from_outside().get(listOfOptTaskNames.get(opt));						
						
						if(tpUprocs.containsKey(curOptTask.getUprocName()))
						{
							if(!tpUprocs.get(curOptTask.getUprocName()).getMFFreq().equalsIgnoreCase(getRule(curOptTask)))
							{
								updateRule(con,curOptTask,tpUprocs.get(curOptTask.getUprocName()).getMFFreq());
							}
							
						}
						
					}
					else
					{
						optTasks.get(optMu).remove(listOfOptTaskNames.get(opt));
					}

				}
			}
				
			
		}
		
	}
	
	private  void createMissingOptTasks() throws UniverseException, Exception
	{
		if(sessionFound && mainTaskFound)
		{
			Set<String> MU_mainTasks = new HashSet<String>( mainTasks.keySet());
			
			for(String optMu:MU_mainTasks)
			{
				ArrayList<String> totalListUprNeedOptTsk = new ArrayList<String>();
				String mainRule=getRule(con.getTaskHashMap_from_outside().get(mainTasks.get(optMu)));
				
				for(String tpu:tpUprocs.keySet())
				{
					if(!tpUprocs.get(tpu).getMFFreq().equalsIgnoreCase(mainRule))
					{
						totalListUprNeedOptTsk.add(tpu);
					}
				}//this list should be linked to each key:mu for optTasks;
				
				numberOfOptionalTasks=totalListUprNeedOptTsk.size();
				
				ArrayList<String> listOfOptTaskNames = new ArrayList<String>(optTasks.get(optMu));
				ArrayList<String> listUprWithMissingOptTask = totalListUprNeedOptTsk;
				
				for(int opt=0;opt<listOfOptTaskNames.size();opt++)
				{
					if(con.getTaskHashMap_from_outside().containsKey(listOfOptTaskNames.get(opt)))
					{
						Task curOptTask = con.getTaskHashMap_from_outside().get(listOfOptTaskNames.get(opt));						
						
						if(listUprWithMissingOptTask.contains(curOptTask.getUprocName()))
						{//if an opt task exists on one of those uprocs , it's safe , let it through
							listUprWithMissingOptTask.remove(curOptTask.getUprocName());
							//keep on removing  the uproc entries that already have a opt task
						}
						else
						{
							/*curOptTask.delete();
							optTasks.get(optMu).remove(listOfOptTaskNames.get(opt));
							System.out.println("Deleted [OPT TSK] "+listOfOptTaskNames.get(opt)+ ": Not needed on "+con.getConnName());
*/
							
						}
						
					}
					else
					{
						optTasks.get(optMu).remove(listOfOptTaskNames.get(opt));
					}

				}//listUprWithMissingOptTask should now contain the remainder of the uprocs that need optional tasks for this MU
				// whatever stayed in listUprWithMissingOptTask is getting created  .
				
				
				for(int mtsk=0;mtsk<listUprWithMissingOptTask.size();mtsk++)
				{
					System.out.println("[OPT TSK] on "+tpUprocs.get(listUprWithMissingOptTask.get(mtsk)).getUprocObject().getName()+" for [MU] "+optMu+" for [SES] "+sessionName+" on "+con.getConnName());
					con.createMissingOptionalTask(mainTasks.get(optMu)
							,tpUprocs.get(listUprWithMissingOptTask.get(mtsk)).getUprocObject().getName()
							,tpUprocs.get(listUprWithMissingOptTask.get(mtsk)).getMFFreq());
				}
	
			}
				
			
		}
		
	}
	
	public void fix() throws Exception
	{
		
		deleteJunkOptTsk();
		fixRuleOnRemainingOptTasks();
		createMissingOptTasks();
		
		
	}
	public  void printTpUprocs()
	{
		if(sessionFound){
		for(String tup:tpUprocs.keySet())
		{
			tpUprocs.get(tup).print(System.out);
		}
		}
		
	}
	private  void updateRule(DuApiConnection dcon,Task t,String rule) throws UniverseException
	{
		
		if(t.getTaskType().equals(TaskType.Provoked))
		{
			System.out.println("Skipping rule update on [PROVOKED TSK] "+t.getIdentifier().getName());
			return;
		}
		
		String oldRule = getRule(t);
		
		TaskPlanifiedData tpd  =(TaskPlanifiedData) t.getSpecificData();
		

	    Rule rule1;
	    
	    if(!dcon.ruleAlreadyExists(rule))
	    { 
	    	dcon.createRule(rule);
	    }
	   
	    
	    rule1=dcon.getRule(rule);

	   
	        TaskImplicitData tid1 = new TaskImplicitData (rule1.getIdentifier ());
	        
	        tid1.setFunctionalVersion(rule1.getFunctionalVersion());
	        tid1.setLabel(rule1.getLabel());
	        tid1.setMonthAuthorization(rule1.getMonthAuthorization());
	        tid1.setWeekAuthorization(rule1.getWeekAuthorization());  
	        
	        tid1.setPeriodType (rule1.getPeriodType ());
	        tid1.setPeriodNumber(rule1.getPeriodNumber());
	        tid1.setPattern (rule1.getPattern ());
	        tid1.setAuthorized (true);
	        final Date date1 = DateTools.toDate ("20140101");
	        tid1.setReferenceDate(DateTools.getYYYYMMDD(date1));
	        Calendar calendar1 = DateTools.getCalendarInstance();
	        calendar1.setTime(date1);
	        Integer weekNumber1 = calendar1.get(Calendar.WEEK_OF_YEAR);
	        tid1.setApplicationWeek(weekNumber1.toString());
	        
	        tid1.setLabel(rule1.getLabel());
	        tid1.setInternal(true);
	      
	        TaskImplicitData[] implicitDataArray = new TaskImplicitData[] {tid1};
	        tpd.setImplicitData (implicitDataArray);
	        
	        t.setSpecificData(tpd);
	        t.update();
	        
		System.out.println("Updated Rule [OPT TSK] "+t.getIdentifier().getName()+": "+oldRule+" --> "+getRule(t));

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
	
	public int getExpectedNumberOfOptTsk()
	{
		return numberOfOptionalTasks;
	}
	
}
