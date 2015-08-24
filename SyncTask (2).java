package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import com.orsyp.api.session.Session;
import com.orsyp.api.task.Task;
import com.orsyp.api.uproc.Uproc;

public class SyncTask {

	private static HashMap<String,String> config_params = new HashMap<String,String>();
	private static HashMap<String,DuApiConnection> DUAPIConnections = new HashMap<String,DuApiConnection>();
	private static HashMap<String,String> sessionFilters = new HashMap<String,String>(); 
	
	public static void main(String[] args) 
	{		
		String connectFile = args[0];
		String refnode 	   = args[1];
		String sessionFilterfile=args[2];
		String logfile     = "syncTask.log";

	    
		connect(connectFile);
	    setSessionFilters(sessionFilterfile);

	

		FileOutputStream fout=null;	
	    PrintStream prtstm=null;
	  
	    

	    boolean setRef = false;
	    
	    HashMap<String,Task> ref_tasks_map =  new HashMap<String,Task>();
	    
	    HashMap<String,Session> cur_sessions_map =  new HashMap<String,Session>();
	    HashMap<String,Uproc> cur_uprocs_map =  new HashMap<String,Uproc>();
	    HashMap<String,Task> cur_tasks_map =  new HashMap<String,Task>();
	    
	  
	    try {
	    
	    	fout = new FileOutputStream (logfile);
	    	prtstm = new PrintStream(fout);		
	    
	    	System.out.println("Build 29/09/2014");
	    	
	    	if(!DUAPIConnections.containsKey(refnode+"/"+config_params.get("AREA")))
			{
				System.out.println("Reference node ["+refnode+"] can't be used. Not in config file !");
				System.exit(-1);
			}
			
			if(DUAPIConnections.containsKey(refnode) )//&& !DUAPIConnections.get(refnode).isConsistent())
			{
				for(int i=0;i<DUAPIConnections.get(refnode).getInconsistencyList().size();i++)
				{
					System.out.println(DUAPIConnections.get(refnode).getInconsistencyList().get(i));
					prtstm.println(DUAPIConnections.get(refnode).getInconsistencyList().get(i));
				}
				
				System.exit(-1);
			}
	    	
	    	
	    	
		for(String connKey:DUAPIConnections.keySet())
		{
 
			
			 DuApiConnection conn=DUAPIConnections.get(connKey);

				 if(conn.getConnName().substring(0,conn.getConnName().indexOf("/")).toUpperCase().equalsIgnoreCase(refnode))
				 {
					 System.out.println("Reference node ["+refnode.toUpperCase()+"] has been found and set...");
					 prtstm.println("Reference node ["+refnode.toUpperCase()+"] has been found and set...");
					 setRef=true;
					
					ref_tasks_map=conn.getTaskHashMap_from_outside();
					
					HashMap<String,Task> ref_Table = new HashMap<String,Task>(ref_tasks_map);
					
					for(String tsk:ref_Table.keySet())
					{
						String session = ref_Table.get(tsk).getSessionName();
						if(!sessionFilters.containsKey(session))
						{
							ref_tasks_map.remove(tsk);
						}
					}
					break;
				 }
		}
		
		
		
		
		for(String connKey:DUAPIConnections.keySet())
		{
			 DuApiConnection conn=DUAPIConnections.get(connKey);
			 

			 if(setRef && !(conn.getConnName().substring(0,conn.getConnName().indexOf("/")).toUpperCase().equalsIgnoreCase(refnode)))
				{
				 System.out.println("Moving [TSK]  from \""+refnode+"\" to \""+conn.getConnName()+"\"...Check \""+logfile+"\"");
				 
								 
					cur_sessions_map = conn.getSessionsHashMap_from_outside();		 
					cur_uprocs_map   = conn.getUprocHashMap_from_outside();
					cur_tasks_map  = conn.getTaskHashMap_from_outside();
				     
			
				    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
					
					
				    for(String ref_tsk:ref_tasks_map.keySet())
				    {
				    	
				    	if(!cur_tasks_map.containsKey(ref_tsk))
				    	{
				    		Task temp = ref_tasks_map.get(ref_tsk);
				    		
				    		try{
				    		
				    		
				    		if(!cur_sessions_map.containsKey(temp.getSessionName()))
				    		{				    					
				    			System.out.println("[SES] "+temp.getSessionName()+" does not exist on "+conn.getConnName()+" --> Skipping "+ref_tsk);

				    			continue;
				    		}
				    		else
				    		{
				    			boolean skip=false;
				    			Session sessiontsk= cur_sessions_map.get(temp.getSessionName());
				    			
				    			for(int u=0;u<sessiontsk.getUprocs().length;u++)
				    			{
				    				if(!cur_uprocs_map.containsKey(sessiontsk.getUprocs()[u]))
				    				{
				    					System.out.println("[UPR] "+sessiontsk.getUprocs()[u]+" does not exist on "+conn.getConnName()+" --> Skipping "+ref_tsk);
				    					skip=true;
				    					break;
				    					
				    				}
				    			}
				    			
				    			if(skip)
				    			{
				    				continue;
				    			}
				    			
				    		}
				    		
				    		
				    		temp.setContext(conn.getContext());
				    		temp.setActive(false);
				    		temp.create();
			    			prtstm.println("[TSK] "+temp.getIdentifier().getName()+" created on "+conn.getConnName());
				    		}
				    		catch (Exception e) {
				 				
				 				e.printStackTrace();
				 				e.printStackTrace(prtstm);
				 			}
				    	}
				    	else
				    	{
				    		Task temp = cur_tasks_map.get(ref_tsk);
				    		try{
				    		
				    			if(!cur_sessions_map.containsKey(temp.getSessionName()))
					    		{
					    			continue;
					    		}
					    		else
					    		{
					    			boolean skip=false;
					    			Session sessiontsk= cur_sessions_map.get(temp.getSessionName());
					    			
					    			for(int u=0;u<sessiontsk.getUprocs().length;u++)
					    			{
					    				if(!cur_uprocs_map.containsKey(sessiontsk.getUprocs()[u]))
					    				{
					    					
					    					skip=true;
					    					break;
					    					
					    				}
					    			}
					    			
					    			if(skip)
					    			{
					    				continue;
					    			}
					    			
					    		}
				    			
				    		temp.delete();
				    		temp=ref_tasks_map.get(ref_tsk);
				    		temp.setContext(conn.getContext());
				    		temp.setActive(false);
				    		temp.create();
			    			prtstm.println("[TSK] "+temp.getIdentifier().getName()+" replaced on "+conn.getConnName());

				    		}
				    		catch (Exception e) {
				 				
				 				e.printStackTrace();
				 				e.printStackTrace(prtstm);
				 			}
				    	}
				    }//creating tasks on my non-ref found in ref but not on my non-ref
				    
				}
				
		}
		
	    }
	    
		 catch (Exception e) {
				
				e.printStackTrace();
				e.printStackTrace(prtstm);
			}
		
		 
		
	}		
	    
	    private static void connect(String connectFile)
	    {
	    	
			/// Reading connectFile info
					Scanner sc = null;
					try {
						sc = new Scanner(new File(connectFile));
						while (sc.hasNext()) {
							String line=sc.nextLine().trim();
							if (line.length()>0)
								if (!line.startsWith("#")) 
									if (line.contains("="))
										config_params.put(line.split("=")[0].trim().toUpperCase(), line.substring(line.indexOf("=")+1).trim());
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						sc.close();
					}
					
					
					if(!config_params.containsKey("NODE") || !config_params.containsKey("PORT")
							||!config_params.containsKey("AREA")||!config_params.containsKey("HOST")
							|| !config_params.containsKey("USER")|| !config_params.containsKey("PASSWORD"))
					{
						System.out.println("Error ! Missing fields in UVMS Config   :\""+connectFile+"\"");
						System.exit(-1);
					}
					
					String nodes[] = config_params.get("NODE").split(",");
					
					
					for(int i=0;i<nodes.length;i++)
					{
						
						
						DuApiConnection conn= new DuApiConnection(nodes[i], config_params.get("AREA"),config_params.get("HOST"),Integer.parseInt(config_params.get("PORT")),config_params.get("USER"),config_params.get("PASSWORD"));
						 if(!DUAPIConnections.containsKey(nodes[i]+"/"+config_params.get("AREA")))
					       {
					    	   DUAPIConnections.put(nodes[i]+"/"+config_params.get("AREA"),conn);
					    	   
					    	
					       }
						 continue;
					}
	    }
	    
	    private static void setSessionFilters(String sessionFilterFile)
	    {
	    	/// Reading connectFile info
			Scanner sc = null;
			try {
				sc = new Scanner(new File(sessionFilterFile));
				while (sc.hasNext()) {
					String line=sc.nextLine().trim();
					if (line.length()>0)
						if (!line.startsWith("#")) 
							sessionFilters.put(line.trim().toUpperCase(), line.trim().toUpperCase());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				sc.close();
			}
	
	    	
	    }
	    
	    
	}
