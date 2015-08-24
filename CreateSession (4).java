package atco.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

import com.orsyp.api.task.Task;

import au.com.bytecode.opencsv.CSVReader;



public class CreateSession {

	/**
	 * @param args
	 */
	final static String HEADER_FLAG_IN_UPRNAME="_H_";
	final static String TASKNAMESPLITTER = "\\-";
	final static String MUNAMESPLITTER ="\\_";
	final static String UPR_NAME_SEPERATOR="-";
	final static String X_AREA = "EXP";
	final static String provoked_flag="PROVOKED";
	
	static String fileName = new SimpleDateFormat("mmhhddMMyyyy").format(new Date());
	//fileName="AAT_"+fileName.substring(0,3)+".log";
	
	public  String lastnode ="";
	public  String lastarea ="";
	public  String lasthost ="";
	public  String lastport ="";
	public  String lastadmin ="";
	public  String lastpass="";

	private String[] temp_H_T_TNC = new String[] {"C2_TEMPLATE_HEADER","C2_TEMPLATE_TRAILER","C2_TEMPLATE_TRAILER"};
	final static int UPRLBL_LIMIT = 63;
	final static int UPRNAME_LIMIT=52;
	
	HashMap<String,DuApiConnection> DUAPIConnections = new HashMap<String,DuApiConnection>();
	HashMap<String,String>config_params = new HashMap<String,String>();
	HashMap<String,String[]> CSV_lines = new HashMap<String,String[]>();
	
	@SuppressWarnings("unused")
	private CustomProperties connProps;//if you want to use external file for properties as in connection issues etc..
	

	public CreateSession()
	{
	
	
	
	}

	public CreateSession(CustomProperties connProps) {
		this.connProps = connProps;
	}
	public CreateSession(String connectFile)
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
	
	public CreateSession(String csvFile,String sessionListfile,String connectFile)
	{
		try {				
			
			 	System.out.println("---------------------------------------------");
		        System.out.println("-----  ATCO AUTOMATION TOOL (18092014)  -----");
		        System.out.println("---------------------------------------------");
		        System.out.println();
		        System.out.println("AUTHOR : AAB/ORSYP for $U6.2.21");
		        System.out.println();
		        System.out.println();
		        System.out.println("Loading input files in AAT ...");
		        System.out.println(" - CSV           : \""+csvFile+"\"");
		        System.out.println(" - List of items : \""+sessionListfile+"\"");
		        System.out.println(" - UVMS Config   : \""+connectFile+"\"");
		        System.out.println();
			

			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new FileReader(csvFile),',', '\"', '\0');
			
			String [] line;
			boolean firstLine = true;
			
			//parse lines
		    while ((line = reader.readNext()) != null) 
		    {
		    	//skip first line
		    	if (firstLine) {
		    		firstLine = false;
		    		continue;
		    	}
		    	
		    	if (line.length<9)
		    		continue;
		    	
		    	String uproc = line[1].trim();
		    	String newSession = line[4].trim();


		    	if(NamingUtils.isHeader(uproc))
		    	{
		    		String renamedHeader = NamingUtils.getTruncatedUprName((NamingUtils.getSA_XX(uproc)+"H_"+newSession).toUpperCase());
		    		if(!CSV_lines.containsKey(renamedHeader))
		    		{
		    			CSV_lines.put(renamedHeader, line);
		    		}
		    	}else if (NamingUtils.isTrailer(uproc) && !NamingUtils.isNCTrailer(uproc))
		    	{
		    		String renamedT = NamingUtils.getTruncatedUprName((NamingUtils.getSA_XX(uproc)+"T_"+newSession).toUpperCase());
		    		if(!CSV_lines.containsKey(renamedT))
		    		{
		    			CSV_lines.put(renamedT, line);
		    			
		    		}
		    		
		    	}
		    	else if(NamingUtils.isTrailer(uproc) && NamingUtils.isNCTrailer(uproc))
		    	{
		    		String renamedT_NC = NamingUtils.getTruncatedUprName((NamingUtils.getSA_XX(uproc)+"T_NC_"+newSession).toUpperCase());
		    		if(!CSV_lines.containsKey(renamedT_NC))
		    		{
		    			CSV_lines.put(renamedT_NC, line);
		    			
		    		}
		    	}
		    	else
		    	{
		    		 String renamedUpr = NamingUtils.getTruncatedUprName((NamingUtils.getSA_XX(uproc) + NamingUtils.getJOB(uproc)+ "-" + newSession).toUpperCase());
		    		if(!CSV_lines.containsKey(renamedUpr))
		    		{
		    			CSV_lines.put(renamedUpr, line);
		    		}
		    	}

			} 
		    
		}
		catch (Exception e)
		{
			System.out.println("Reading CSV file error !");
			System.exit(1);
		}
		
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
	
	public HashMap<String,DuApiConnection> getDuApiConnections()
	{
		return this.DUAPIConnections;
	}
	public void addDuApiConnection()
	{
		this.connectKeyBoard();
	}
	
	

	
	protected DuApiConnection connectKeyBoard ()
	{
		System.out.println("Connecting to UVMS ...");
		System.out.println();
		
		DuApiConnection conn;
		
		 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        
	        String nodename = null;
	        String area = null;
	        String host = null;
	        String port = null;
	        String user = null;
	        String password = null;
	        
	        
	        
	        System.out.print("NODE NAME ["+lastnode+"]: ");
	        try {
	            nodename = reader.readLine().trim();
	            
	            if(nodename.trim().isEmpty() ||nodename.trim().equals("") )
	            {
	            	nodename=lastnode;
	            }
	            else
	            {
	            	this.lastnode=nodename;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } 
	        System.out.println("--> Confirming node  : " + nodename);
	        
	       	        
	        
	        System.out.print("AREA      ["+lastarea+"]: ");
	        try {
	            area = reader.readLine().trim();
	            if(area.trim().isEmpty() || area.trim().equals("") )
	            {
	            	area=lastarea;
	            }
	            else
	            {
	            	this.lastarea=area;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println("--> Confirming area  : " + area);

	        if(DUAPIConnections.containsKey(nodename+"/"+area))
		       {
	        	
	        	System.out.println("	    ---> ALREADY CONNECTED TO "+nodename+"/"+area+"! .... ");
	        	System.out.println();
		    	return DUAPIConnections.get(nodename+"/"+area);
		       }
	        
	        System.out.print("HOST    ["+lasthost+"]: ");
	        try {
	            host = reader.readLine().trim();
	            if(host.trim().isEmpty() || host.trim().equals("") )
	            {
	            	host=lasthost;
	            }
	            else
	            {
	            	this.lasthost=host;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println("--> Confirming host  : " + host);
	        
	        
	        System.out.print("PORT     ["+lastport+"] : ");
	        try {
	            port = reader.readLine().trim();
	            if(port.trim().isEmpty() || port.trim().equals("") )
	            {
	            	port=lastport;
	            }
	            else
	            {
	            	this.lastport=port;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println("--> Confirming port  : " + port);
	        
	        
	        System.out.print("USER     ["+lastadmin+ "]: ");
	        try {
	            user = reader.readLine().trim();
	            if(user.trim().isEmpty() || user.trim().equals("") )
	            {
	            	user=lastadmin;
	            }
	            else
	            {
	            	this.lastadmin=user;
	            }
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println("--> Confirming user  : " + user);
	        

	        System.out.print("PASS    ["+lastpass  +"]: ");
	        try {
	            password = reader.readLine().trim();
	            if(password.trim().isEmpty() || password.trim().equals("") )
	            {
	            	password=lastpass;
	            }
	            else
	            {
	            	this.lastpass=password;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	       
	        
	        conn= new DuApiConnection(nodename, area,host,Integer.parseInt(port),user,password);
	       
	       
	       
	       System.out.println();
	       System.out.println();
	       System.out.println("	    ---> CONNECTED TO "+nodename+"/"+area+" now ! .... ");
	       System.out.println();
	       
	       if(!DUAPIConnections.containsKey(nodename+"/"+area))
	       {
	    	   DUAPIConnections.put(nodename+"/"+area,conn);
	       }	    	
	       
	       System.out.println();
	       
	       return 	DUAPIConnections.get(nodename+"/"+area);
	        
	}

	


	 public void build_UPR_SES_TSK_from_CSV (String connName,String sessionNameFilter,boolean withUprRepl,boolean withSesRepl,boolean withTskRepl) {
		
		
		try {				
			
			System.out.println();
			System.out.println("Building UPR/SES/TSK from CSV for session ["+ sessionNameFilter+"] on node ["+connName+"]");
			System.out.println("--------------------------------------------------------------------------------------------------");
			System.out.println();
			
			
			
			TreeMap<String,SessionModel> oldSessions = new TreeMap<String,SessionModel>(); 
			TreeMap<String,SessionModel> newSessions = new TreeMap<String,SessionModel>();
			
			HashMap <String,String > header_mainrule = new HashMap<String,String>();//where I store the session name and the rule of its header

			HashMap <String,String > uproc_rule = new HashMap<String,String>();//where I store the session name and the rule of its header
			HashMap <String,String > uproc_lw = new HashMap<String,String>();//the launch windows
			HashMap <String,String > uproc_Mus = new HashMap<String,String>();//the mus
		
			DuApiConnection conn;
			
			if(this.getDuApiConnections().containsKey(connName))
			{
				conn=this.getDuApiConnections().get(connName);
			}
			else
			{
				conn = this.connectKeyBoard();	
			}
			
			
			
			//parse lines
			for(String uproc_renamed:CSV_lines.keySet())
			{

				String uproc = uproc_renamed;
		    	String oldSession = CSV_lines.get(uproc)[0].trim();
		    	String uprTemplate= CSV_lines.get(uproc)[2].trim();
		    	//uprTemplate=NamingUtils.getTruncatedUprName(uprTemplate);

		    	String oldChildren = CSV_lines.get(uproc)[3].replaceAll("\\((.*?)\\)", "").trim();
		    	String newSession = CSV_lines.get(uproc)[4].trim();
		    	
		    	if(newSession.equalsIgnoreCase(sessionNameFilter))
		    	{
		    	
		    	String newSessionLabel = CSV_lines.get(uproc)[5].trim();
		    	String uprLabel = CSV_lines.get(uproc)[6].trim();
		    	String uprseverity = CSV_lines.get(uproc)[7].trim();
		    	
		    	String newChildren = CSV_lines.get(uproc)[8].replaceAll("\\((.*?)\\)", "").trim();
		    	newChildren=newChildren.replace(oldSession, newSession);
		    	
		    	String newDependencies = CSV_lines.get(uproc)[9].trim();
		    	newDependencies = newDependencies.replace(oldSession, newSession);
		    	
		    	String rule = CSV_lines.get(uproc)[10].trim();
		    	String lw = CSV_lines.get(uproc)[11].trim();
		    	String mus= CSV_lines.get(uproc)[12].trim();
		    	

		    	if(withUprRepl)
				{
		    		if(!NamingUtils.isTrailer(uproc))
		    		{ 
			    		if (uprLabel.length()> UPRLBL_LIMIT)
				    	{
				    		uprLabel=uprLabel.substring(0,UPRLBL_LIMIT).trim();
				    	}//limit the label length to 64 characters

			    		if(conn.uprocAlreadyExists(uproc))
						{
			    			
			    			if(!conn.uprocAlreadyExists(uprTemplate))
						    {//uprTemplate does not exist , create it
				    			
			    				System.out.println("Template UPR ["+uprTemplate+"] does not exist on "+conn.getConnName()+". Trying with \"C2_TEMPLATE_MKT_ONE_OF\"..." );

			    				uprTemplate="C2_TEMPLATE_MKT_ONE_OF";
				    			
				    			if(!conn.uprocAlreadyExists(uprTemplate))
							    {
					    			System.out.println("[C2_TEMPLATE_MKT_ONE_OF] also not found , UPROC ["+uproc+"] not created ! Skipping ...Objects for \""+sessionNameFilter+"\" will not be created !"); 

					    			return;
							    }
						    }	
			    			
			    			
			    			
							if(!uprTemplate.equalsIgnoreCase(uproc))
							{
								conn.deleteUproc(uproc);
								conn.duplicateUproc(uprTemplate,uproc,uprLabel,Integer.parseInt(uprseverity));
				    			System.out.println("UPROC : "+uproc+" created");
				    			
							
							}
						
						}
			    		
			    		else
			    		{
			    			if(!conn.uprocAlreadyExists(uprTemplate))
						    {//uprTemplate does not exist , create it
			    				System.out.println("Template UPR ["+uprTemplate+"] does not exist on "+conn.getConnName()+". Trying with \"C2_TEMPLATE_MKT_ONE_OF\"..." );

			    				uprTemplate="C2_TEMPLATE_MKT_ONE_OF";
				    			
				    			if(!conn.uprocAlreadyExists(uprTemplate))
							    {
					    			System.out.println("[C2_TEMPLATE_MKT_ONE_OF] also not found , UPROC ["+uproc+"] not created ! Skipping ...Objects for \""+sessionNameFilter+"\" will not be created !"); 
					    			return;
							    }
						    }	
				    			
			    				conn.duplicateUproc(uprTemplate,uproc,uprLabel,Integer.parseInt(uprseverity));
					    		System.out.println("UPROC : "+uproc+" created");
					    		
			    		}

			    		conn.setDependenciesOnUproc(uproc,newDependencies);

		    		}
				}
		    	
		       	if(withTskRepl)
		    	{
		    		if(uproc.contains("_MKT_") && mus.contains("_CIS"))
			    	{
			    		mus=mus.replace("CIS", "MKT");
			    	}
		    		
		    		if(NamingUtils.isHeader(uproc))
		    		{
		    			header_mainrule.put(uproc, rule);
		    		}
		    		
	    		  	uproc_rule.put(uproc, rule);
	    		  	uproc_lw.put(uproc, lw);
					uproc_Mus.put(uproc, mus);
		    		
		    	}
		    	
		    	
		    	if(withSesRepl)
		    	{
		    		if(NamingUtils.isTrailer(uproc))
		    		{
		    			continue;
		    		}
		    		//create session object if not already existing
			    	if (!oldSessions.containsKey(oldSession))
			    	{
			    		oldSessions.put(oldSession, new SessionModel(oldSession));
			    		SessionModel sm = oldSessions.get(oldSession);
			    		sm.addUproc(uproc, oldChildren);
			    	
			    	}
				    	
			    	//create session object if not already existing
				    if (!newSessions.containsKey(newSession)) 
				    {
				    		SessionModel smNew = new SessionModel(newSession);
				    		smNew.label = newSessionLabel;
				    		newSessions.put(newSession, smNew);
				    }
				    		
			    	
				    newSessions.get(newSession).addUproc(uproc, newChildren);
			    	if(NamingUtils.isHeader(uproc))
			    	{
			    		newSessions.get(newSession).setHeader(uproc);
			    	}
			    	
		    	}
		    	
		 
		    	
		    	
		    	}
			}
		    	
			System.out.println();
			

		    	if(withSesRepl)
		    	{
		    		for (String sess: newSessions.keySet()) 
				    {
				    	

				    	SessionModel sm = newSessions.get(sess);
				    	
				    	//sm.addHeader();
				    	if(sm.getHeader()==null || sm.getHeader().trim().length()==0)
				    	{
				    		sm.setfirstUprocName(sm.getFirstAddedUprocName());
				    		sm.addHeader();
				    	}
						sm.addTrailer(true);
						sm.addTrailer(false);
						//sm.renameUprocs();
						
						if(withTskRepl)
						{
							if(!header_mainrule.containsKey(sm.getHeader()))
							{
								header_mainrule.put(sm.getHeader(),"WORKDAY");
							}
							if(!uproc_rule.containsKey(sm.getHeader()))
							{
								uproc_rule.put(sm.getHeader(), "WORKDAY");//store header uprocname and associated rule;
							}
							if(!uproc_lw.containsKey(sm.getHeader()))
							{
								uproc_lw.put(sm.getHeader(), "1700;0000;000;018;00");
							}
							if(!uproc_Mus.containsKey(sm.getHeader()))
							{
								uproc_Mus.put(sm.getHeader(), "C_C2_01_CIS|");
							}

						}// add default info for headers if not defined in .csv file 
						
				    
				    	try 
				    	{
					    		
				    			if(conn.sesAlreadyExists(sess))
					    		{
				    				conn.deleteSession(sess);
					    		}
					    		
					    			
					    		conn.createSession(sess, sm, temp_H_T_TNC);
					    		conn.setSessionHDP(CSV_lines, sess);
					    		conn.fixTrailerDependencies(sess);
				    			System.out.println();
				    			
				    		
				    	} catch (Exception e) {
				    		System.out.println("ERROR creating session " +sess);
				    		e.printStackTrace();
				    	}
		    	
		    	
				    }
		    	}

				System.out.println();
				
				if(withTskRepl)
				{
					System.out.println("Building Main Tasks on [NODE] "+ conn.getConnName());
					System.out.println("-----------------------------------------------");
				
	
					for(String headerUprocs:header_mainrule.keySet())
					{
						if(header_mainrule.get(headerUprocs).equalsIgnoreCase(provoked_flag))
						{
							continue;
						}
						if(uproc_lw.containsKey(headerUprocs) && uproc_Mus.containsKey(headerUprocs))
						{
							ArrayList<String> taskNames = new ArrayList<String>();
					    	String currentTask;
							String[] mu_tks = uproc_Mus.get(headerUprocs).split("\\|");
							
							for (String tk: mu_tks) 
							{
								if(!tk.trim().isEmpty() && !tk.trim().equals(""))
								{
									
									currentTask=headerUprocs+"-"+tk.trim();
									taskNames.add(currentTask);
								}//building the task names from the piped list of MUs and uproc name
							}
							
							for(int m=0;m<taskNames.size();m++)
							{
								if(conn.tskAlreadyExists(taskNames.get(m)))
								{
									Task tskToDel = conn.getTaskByName(taskNames.get(m), false);
									Task tskToDel_template = conn.getTaskByName(taskNames.get(m), true);
									
									if(tskToDel!=null)
									{
										conn.deleteTask(tskToDel.getIdentifier().getName(), tskToDel.getIdentifier().getVersion(), tskToDel.getMuName(), false);
									}
									
									if (tskToDel_template!=null)
									{
										conn.deleteTask(tskToDel_template.getIdentifier().getName(), tskToDel_template.getIdentifier().getVersion(), tskToDel_template.getMuName(), true);
	
									}
									
								}
								conn.createMainTask(taskNames.get(m), header_mainrule.get(headerUprocs),uproc_lw.get(headerUprocs));
							}
						}
						else
						{
							
							System.out.println("MAIN TASK ON HEADER ["+headerUprocs+"] NOT CREATED : Missing info ....skipping");
						}
					}
					
					System.out.println("Building Optional Tasks on [NODE] "+ conn.getConnName());
					System.out.println("-----------------------------------------------");
	
					
	
					for(String strUpr:uproc_rule.keySet())
					{
						if(NamingUtils.isHeaderOrTrailer(strUpr))
						{
							continue;
						}
						String headerDerived=conn.getHeaderNameFromUprName(strUpr);
						if(headerDerived==null)
						{
							System.out.println();
					
							System.out.println("OPT TASK NOT CREATED FOR ["+strUpr+"] : WRONG NAME FORMAT ! Skipping ...");

							continue;
						}
						
						if(!header_mainrule.containsKey(headerDerived))
						{
							System.out.println();
							
							System.out.println("OPT TASK  NOT CREATED FOR ["+strUpr+"] : Derived Header Name ["+headerDerived+"] not found in HEADER-RULE Hashmap");

							continue;
						}
						
						String headerRule = header_mainrule.get(headerDerived);
						
						if(!NamingUtils.isHeaderOrTrailer(strUpr)&& !uproc_rule.get(strUpr).equals(headerRule)){
							
						if(uproc_lw.containsKey(strUpr) && uproc_Mus.containsKey(strUpr))
						{
						
							ArrayList<String> taskNames = new ArrayList<String>();
					    	String currentTask;
					    	
							String[] mu_tks = uproc_Mus.get(strUpr).split("\\|");
							
							for (String tk: mu_tks) 
							{
								if(!tk.trim().isEmpty() && !tk.trim().equals(""))
								{
									
									currentTask=strUpr+"-"+tk.trim();
									taskNames.add(currentTask);
								}//building the task names from the piped list of MUs and uproc name
							}
							
							for(int o=0;o<taskNames.size();o++)
							{
								if(conn.tskAlreadyExists(taskNames.get(o)))
								{
									Task tskToDel = conn.getTaskByName(taskNames.get(o), false);
									Task tskToDel_template = conn.getTaskByName(taskNames.get(o), true);
									
									if(tskToDel!=null)
									{
										conn.deleteTask(tskToDel.getIdentifier().getName(), tskToDel.getIdentifier().getVersion(), tskToDel.getMuName(), false);
									
									}
									
									if (tskToDel_template!=null)
									{
										conn.deleteTask(tskToDel_template.getIdentifier().getName(), tskToDel_template.getIdentifier().getVersion(), tskToDel_template.getMuName(), true);
	
									}
									
								}
								conn.createOptionalTask(taskNames.get(o), uproc_rule.get(strUpr),uproc_lw.get(strUpr));
							}
						}
						else
						{
							System.out.println("OPT TASK FOR ["+strUpr+"] NOT CREATED : Missing info ..check .csv file ....skipping");
						}

					}
				}
				
					System.out.println("Building Provoked Tasks on [NODE] "+ conn.getConnName());
					System.out.println("-----------------------------------------------");
				

					for(String headerUprocs:header_mainrule.keySet())
					{
						if(header_mainrule.get(headerUprocs).equalsIgnoreCase("PROVOKED") && conn.uprocAlreadyExists(headerUprocs)) 
						{
							ArrayList<String> taskNames = new ArrayList<String>();
					    	String currentTask;
							String[] mu_tks = uproc_Mus.get(headerUprocs).split("\\|");
							
							for (String tk: mu_tks) 
							{
								if(!tk.trim().isEmpty() && !tk.trim().equals(""))
								{
									
									currentTask=headerUprocs+"-"+tk.trim();
									taskNames.add(currentTask);
								}//building the task names from the piped list of MUs and uproc name
							}
							
							for(int m=0;m<taskNames.size();m++)
							{
								if(conn.tskAlreadyExists(taskNames.get(m)))
								{
									Task tskToDel = conn.getTaskByName(taskNames.get(m), false);
									Task tskToDel_template = conn.getTaskByName(taskNames.get(m), true);
									
									if(tskToDel!=null)
									{
										conn.deleteTask(tskToDel.getIdentifier().getName(), tskToDel.getIdentifier().getVersion(), tskToDel.getMuName(), false);
									}
									
									if (tskToDel_template!=null)
									{
										conn.deleteTask(tskToDel_template.getIdentifier().getName(), tskToDel_template.getIdentifier().getVersion(), tskToDel_template.getMuName(), true);
	
									}
									
								}
								conn.createProvokedTask(taskNames.get(m));
							}
						}
						
					}
					
					
					
					
				}
			
	 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	 
	 
	 
	 }
	


}
