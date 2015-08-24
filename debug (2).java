package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;


public class debug {

	private static HashMap<String,String> config_params = new HashMap<String,String>();
	private static HashMap<String,DuApiConnection> DUAPIConnections = new HashMap<String,DuApiConnection>();
	private static HashMap<String,String> sessionFilters = new HashMap<String,String>(); 
	
	public static void main(String[] args) 
	{		
		String connectFile = args[0];
		String sessionFilterfile=args[1];
		
		
		Date now = new Date();
		String timestamp = new SimpleDateFormat("MMddyy_hhmm").format(now);
		String logfile = "optrule_"+timestamp+".log";

		
		connect(connectFile);
	    setSessionFilters(sessionFilterfile);

	
		FileOutputStream fout=null;	
	    PrintStream prtstm=null;
	  

	    try {
	    
	    	fout = new FileOutputStream (logfile);
	    	prtstm = new PrintStream(fout);		

		for(String connKey:DUAPIConnections.keySet())
		{

			 DuApiConnection conn=DUAPIConnections.get(connKey);
			conn.refreshRuleOnOptTsk(sessionFilters);

		}

	    }
	    catch(Exception e)
	    {
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
					
					System.out.println("Connecting to UVMS : \""+config_params.get("HOST")+"\"...");

					String nodes[] = config_params.get("NODE").split(",");
					
					
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
	    

	
	}
