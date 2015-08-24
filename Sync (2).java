package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import com.orsyp.UniverseException;
import com.orsyp.api.session.Session;
import com.orsyp.api.task.Task;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.uproc.cl.InternalScript;

public class Sync {

	private static HashMap<String,String> config_params = new HashMap<String,String>();
	private static HashMap<String,DuApiConnection> DUAPIConnections = new HashMap<String,DuApiConnection>();
	private static HashMap<String,String> sessionFilters = new HashMap<String,String>(); 
	
	public static void main(String[] args) 
	{		
		String connectFile = args[0];
		String refnode 	   = args[1];
		String sessionFilterfile=args[2];
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmm");
		String formattedDate = sdf.format(date);
		
		String logfile = "sync_"+formattedDate+".log";
		//String logfile = sessionFilterfile.substring(0,sessionFilterfile.indexOf("."))+"_"+connectFile.substring(0,connectFile.indexOf(".")).replace("\\", "").replace(".","").replace(":", "")+".log";
		
	    
		
		connect(connectFile);
	    setSessionFilters(sessionFilterfile);

	
		FileOutputStream fout=null;	
	    PrintStream prtstm=null;
	  
	    

	    boolean setRef = false;
	    HashMap<String,Session> ref_sessions_map =  new HashMap<String,Session>();
	    HashMap<String,Uproc> ref_uprocs_map =  new HashMap<String,Uproc>();
	    HashMap<String,Task> ref_tasks_map =  new HashMap<String,Task>();
	    
	    HashMap<String,Session> cur_sessions_map =  new HashMap<String,Session>();
	    HashMap<String,Uproc> cur_uprocs_map =  new HashMap<String,Uproc>();
	    HashMap<String,Task> cur_tasks_map =  new HashMap<String,Task>();
	    
	  
	    try {
	    
	    	fout = new FileOutputStream (logfile);
	    	prtstm = new PrintStream(fout);		
	    
	    	
	    	
	    	if(!DUAPIConnections.containsKey(refnode+"/"+config_params.get("AREA")))
			{
				System.out.println("Reference node ["+refnode+"] can't be used. Not in config file !");
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
					ref_sessions_map=conn.getSessionsHashMap_from_outside();
					ref_uprocs_map=conn.getUprocHashMap_from_outside();
					ref_tasks_map=conn.getTaskHashMap_from_outside();
					break;
				 }
		}
		
		
		
		
		for(String connKey:DUAPIConnections.keySet())
		{
			 DuApiConnection conn=DUAPIConnections.get(connKey);
			 
			
				
			 
			 if(setRef && !(conn.getConnName().substring(0,conn.getConnName().indexOf("/")).toUpperCase().equalsIgnoreCase(refnode)))
				{
				 System.out.println("Syncing "+conn.getConnName()+"...Check \""+logfile+"\"");
				 
				 prtstm.println();
				 prtstm.println("Syncing "+conn.getConnName());
				 prtstm.println("------------------------");
				 
				 
				 	cur_sessions_map = conn.getSessionsHashMap_from_outside();		 
					cur_uprocs_map   = conn.getUprocHashMap_from_outside();
					cur_tasks_map  = conn.getTaskHashMap_from_outside();
				     
					

				    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				    
				    for(String ref_sess:ref_sessions_map.keySet())
				    {
				    	if(!sessionFilters.containsKey(ref_sess))
				    	{
				    		continue;
				    	}
				    	
				    	if(cur_sessions_map.containsKey(ref_sess))
				    	{
				    		Session temp = cur_sessions_map.get(ref_sess);
				    		try{
				    		temp.delete();
				    		
			    			prtstm.println("[SES] "+temp.getName()+" deleted on "+conn.getConnName());
				    		}
				    		catch (Exception e) {
				 				
				 				e.printStackTrace();
				 				e.printStackTrace(prtstm);
				 			}
				    		
				    		
				    	}	
				    		
				    	try{
				    		Session temp = ref_sessions_map.get(ref_sess);
				    		temp.setContext(conn.getContext());
				    		temp.create();
			    			prtstm.println("[SES] "+temp.getName()+" created on "+conn.getConnName());
			    			
			    			for(int u=0;u<ref_sessions_map.get(ref_sess).getUprocs().length;u++)
			    			{
			    				if(ref_uprocs_map.containsKey(ref_sessions_map.get(ref_sess).getUprocs()[u]))
			    				{
			    					Uproc temp_uproc = ref_uprocs_map.get(ref_sessions_map.get(ref_sess).getUprocs()[u]);
			    					
			    					if(cur_uprocs_map.containsKey(ref_sessions_map.get(ref_sess).getUprocs()[u]))
			    					{
			    						cur_uprocs_map.get(ref_sessions_map.get(ref_sess).getUprocs()[u]).delete();
						    			prtstm.println("[UPR] "+temp_uproc.getName()+" deleted on "+conn.getConnName());

			    					}
			    					
			    					try{
							    		
							    		String[]lines=extractInternalScript(temp_uproc);	

							    		temp_uproc.setContext(conn.getContext());		    		
							    		temp_uproc.create();
							    		
							    		createInternalScript(temp_uproc,lines);				    		

						    			prtstm.println("[UPR] "+temp_uproc.getName()+" created on "+conn.getConnName());

							    		}
							    		catch (Exception e) {
							 				
							 				e.printStackTrace();
							 				e.printStackTrace(prtstm);
							 			}
			    				}
			    			}
			    			
			    			
				    		}
				    		catch (Exception e) {
				 				
				 				e.printStackTrace();
				 				e.printStackTrace(prtstm);
				 			}
				    		
				    	}

	    
				    for(String ref_tsk:ref_tasks_map.keySet())
				    {
				    	if(!sessionFilters.containsKey(ref_tasks_map.get(ref_tsk).getSessionName()))
				    	{
				    		continue;
				    	}
				    	
				    	Task temp_task=ref_tasks_map.get(ref_tsk);
				    	
				    	if(cur_tasks_map.containsKey(ref_tsk))
				    	{
				    		try{
				    			cur_tasks_map.get(ref_tsk).delete();
				    		prtstm.println("[TSK] "+ref_tsk+" deleted on "+conn.getConnName());

				    		}
				    		catch (Exception e) {
				 				
				 				e.printStackTrace();
				 				e.printStackTrace(prtstm);
				 			}
				    	}
				    	
				    	temp_task=ref_tasks_map.get(ref_tsk);
			    		temp_task.setContext(conn.getContext());
			    		prtstm.println("[TSK] "+ref_tsk+" created on "+conn.getConnName());
			    		temp_task.create();

				    	
				    }//creating tasks on my non-ref found in ref but not on my non-ref
				    
				}
				
		}
		
	    }
	    
		 catch (Exception e) {
				
			 	System.out.println("ERROR : check .log ! It will tell you the last task reached before error");
				e.printStackTrace();
				e.printStackTrace(prtstm);
			}
		
		 return ;
		
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
					
					System.out.println("Connecting to UVMS : \""+config_params.get("HOST")+"\"");

					
					for(int i=0;i<nodes.length;i++)
					{
						
						
						DuApiConnection conn= new DuApiConnection(nodes[i], config_params.get("AREA"),config_params.get("HOST"),Integer.parseInt(config_params.get("PORT")),config_params.get("USER"),config_params.get("PASSWORD"),true,true,true);
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
	    
	
	    
	    private static void createInternalScript(Uproc obj,String[]lines) throws UniverseException {
	        InternalScript data = new InternalScript(obj);
	        
	        data.setLines(lines);// put your script here

	        obj.setInternalScript(data);
	        obj.setSpecificData(data);
	        data.save();
	        //printf("Uproc [%s] => specific data created.\n", obj.getIdentifier()
	         //       .getName());
	    }
	    public static  String[] extractInternalScript(Uproc u) throws UniverseException {
	        
	        InternalScript script = new InternalScript(u);
	        script.extractContent();
	        //printf("Internal script extraction for Uproc [%s] :\n", u.getName());

	        String[] lines = script.getLines();
	        if (lines != null) {
	            return lines;
	            }
	        else
	        {
	        	return new String[]{"Error copying script"};
	        }
	    }
	

	
	}
