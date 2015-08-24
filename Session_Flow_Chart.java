package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.orsyp.api.session.Session;
import com.orsyp.api.syntaxerules.OwlsSyntaxRules;
import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.uproc.UprocId;
import com.orsyp.owls.impl.uproc.OwlsUprocImpl;



public class Session_Flow_Chart {

	private static HashMap<String,String> config_params = new HashMap<String,String>();
	private static 	HashMap<String,DuApiConnection> DUAPIConnections = new HashMap<String,DuApiConnection>();
	 
	private static  String START = "B";
	private static  String END = "E";
	private static HashMap<String,String>pathStorage = new HashMap<String,String>();
	
	public static void main(String[] args) 
	{		
		String connectFile = args[0];
		String filename=args[1];
		
		FileOutputStream fout;	
	    PrintStream prtstm;
	  
	    connect(connectFile);

		
				
		for(String connKey:DUAPIConnections.keySet())
		{
				
			
		 try {
				 DuApiConnection conn=DUAPIConnections.get(connKey);
				 ArrayList<Session> listOfSessions = conn.getSessionsArrayList();
		 
				 HashMap < String ,ArrayList<String>> sessionsInbound = new HashMap<String,ArrayList<String>>();

				 Multimap<String, String> sessionsOutbound = ArrayListMultimap.create();
				
 
				 for(int i=0;i<listOfSessions.size();i++)
				 {
					 
					 if(!NamingUtils.isHeader(listOfSessions.get(i).getHeader()))
					{
						 continue;
					}
					 
					 if(!conn.uprocAlreadyExists(listOfSessions.get(i).getHeader()))
					 {
						 continue;
					 }
					 
					UprocId uprocId = new UprocId(listOfSessions.get(i).getHeader(), listOfSessions.get(i).getVersion());
			         Uproc obj = new Uproc(conn.getContext(), uprocId);
			         obj.setImpl(new OwlsUprocImpl());
			         obj.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			         obj.extract();
					 
			         ArrayList<String> headerDeps = new ArrayList<String>();
			         Vector<DependencyCondition> currentDeps = obj.getDependencyConditions();
					 
			         for(int j=0;j<currentDeps.size();j++)
					 {
						if(NamingUtils.isTrailer(currentDeps.get(j).getUproc()) || NamingUtils.isExitPoint(currentDeps.get(j).getUproc()))
						{
							if(NamingUtils.getSessionFromUpr(currentDeps.get(j).getUproc())==null)
							{
								continue;
							}
							
							String session_fromHeaderDep =NamingUtils.getSessionFromUpr(currentDeps.get(j).getUproc());
							
							if (!headerDeps.contains(session_fromHeaderDep) && !session_fromHeaderDep.equalsIgnoreCase(listOfSessions.get(i).getName()))
							{
								 headerDeps.add(session_fromHeaderDep);	 
								 sessionsOutbound.put(currentDeps.get(j).getUproc(), listOfSessions.get(i).getName());

							}

						}
					 }
					 
					if(!sessionsInbound.containsKey(listOfSessions.get(i).getHeader()))
					{
						 sessionsInbound.put(listOfSessions.get(i).getHeader(),headerDeps );
					}
				 }
		 
				 
				 HashMap<String,FlowBox> flowBoxes = new HashMap<String,FlowBox>();
				 
				 for(String sessionHeader:sessionsInbound.keySet())
				 {
					 FlowBox fb = new FlowBox();
					 fb.setInbound(sessionsInbound.get(sessionHeader));
					 
					 if(NamingUtils.getSessionFromUpr(sessionHeader)==null)
					 {
						 continue;
					 }
						 
					 
					 fb.setName(NamingUtils.getSessionFromUpr(sessionHeader));
 
					 					 
					 for(String sessionTrailer:sessionsOutbound.keySet())
					 {
						 if(sessionTrailer.contains(NamingUtils.getSessionFromUpr(sessionHeader)))
						 {
							 ArrayList<String>arrayListToAppend = new ArrayList<String>(new HashSet<String>(sessionsOutbound.get(sessionTrailer)));
							 fb.appendOutbound(arrayListToAppend);
						 }
					 }
					 
					 flowBoxes.put(fb.getName(), fb);
				 }
		 
				 
				 Graph_enh g=new Graph_enh();
				
				
			for(String flowbox:flowBoxes.keySet())
			{
				FlowBox current = new FlowBox(flowBoxes.get(flowbox));
				
				for(int i=0;i<current.getOutbound().size();i++)
				{
					g.addEdge(current.getName(),current.getOutbound().get(i));

				}
				
			}//the graph has been constructed 
		 
		
			for(String flowbox1:flowBoxes.keySet())
			{
				if(!flowBoxes.get(flowbox1).isStart())// if the start point is not really a start point skip
				{
					continue;
				}
				START=flowbox1;
				LinkedList<String> visited = new LinkedList<String>();
		     
				for(String flowbox2:flowBoxes.keySet())
				{
					if(!flowbox1.equals(flowbox2))
					{
						if(!flowBoxes.get(flowbox2).isDeadEnd())//if the end point is not really an end point skip
						{
							continue;
						}
						END=flowbox2;
						visited.add(START);
						new Session_Flow_Chart().breadthFirst(g, visited);
					}
				}
		     
		     
			}
			
			try {
				
				fout = new FileOutputStream (filename);
			    prtstm = new PrintStream(fout);		
		
			prtstm.println("NODE,START,END,");
			
			for(String path:pathStorage.keySet())
			{
				prtstm.println(connKey+","+path+pathStorage.get(path));
			}
			
			System.out.println("File \""+filename+"\" created !");
			} catch(IOException e)
			{
				            
				System.out.println("Can't find file");
						
			}	
			
		 } catch (Exception e) {
				
				e.printStackTrace();
			}
		 
			pathStorage.clear();
			
	 
		}
	}		

	 private void breadthFirst(Graph_enh graph, LinkedList<String> visited) {
	        LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
	        // examine adjacent nodes
	        for (String node : nodes) {
	            if (visited.contains(node)) {
	                continue;
	            }
	            if (node.equals(END)) {
	                visited.add(node);
	                printAndStorePath(visited);
	                visited.removeLast();
	                break;
	            }
	        }
	        // in breadth-first, recursion needs to come after visiting adjacent nodes
	        for (String node : nodes) {
	            if (visited.contains(node) || node.equals(END)) {
	                continue;
	            }
	            visited.addLast(node);
	            breadthFirst(graph, visited);
	            visited.removeLast();
	        }
	    }

	    private void printAndStorePath(LinkedList<String> visited) {
	        String path="";
	    	for (String node : visited) {
	            if(!path.contains(node))
	    		{
	            	path+=node+",";
	    		}
	           
	        }
	        
	    	if(!pathStorage.containsKey(START+","+END+",") && !path.equals(""))
	    	{
	        	pathStorage.put(START+","+END+",", path) ;
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
	}
