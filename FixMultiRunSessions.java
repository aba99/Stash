package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.event.JobEvent;
import com.orsyp.api.task.Task;


public class FixMultiRunSessions {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();
//this code cleans out the job event T_NC uprocs of the multiple_run sessions for ATCO .
// it also creates as many sys_batches as needed : per session per mu of size=1 
// starts the DQM and gets out . 

	
	public static void main(String[] args) {
		
		String connectFile = args[0];
		String sessionFilters = args[1];
		
		connect(connectFile);

		ArrayList<JobEvent> je = new ArrayList<JobEvent>();
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
			
			FileOutputStream fout = null;
			PrintStream prtstm = null;
			
			fout = new FileOutputStream("deleted_je.log");
			prtstm = new PrintStream(fout);

			for (String connKey : DUAPIConnections.keySet()) {
				
				DuApiConnection conn = DUAPIConnections.get(connKey);

				HashMap<String,String> session_mu_abb = new HashMap<String,String>();//contains the full sessionname and mu as key , value will be the abbreviated name to be used when assigning sysbatches to tasks
				je = conn.getJobEventsArrayList();
				String[]mus=conn.getDefaultMUs();
				HashMap<String,Task> tasks = conn.getTaskHashMap_from_outside();

				for (int j = 0; j < je.size(); j++) {
					if (sesfilters.contains(je.get(j).getSessionName())) 
					{
						if (je.get(j).getUprocName().contains("_T_NC_")) {
							System.out.println(conn.getConnName()+"- "+je.get(j).getUprocName());

							prtstm.println(conn.getConnName()+"- "+je.get(j).getUprocName());
							je.get(j).delete();
						}

					}

				}
				
				
				
				for(int s=0;s<sesfilters.size();s++)
				{
					for(int m=0;m<mus.length;m++)
					{
					
						String dqmName= session_mu_abb.size()+"_"+mus[m];
						
					
						conn.createDQM(dqmName,1);
						session_mu_abb.put(sesfilters.get(s)+"_"+mus[m],dqmName);
						
					}
				}//setting up all the dqms possiblly needed
				
				for(String tsk:tasks.keySet())
				{
					if(sesfilters.contains(tasks.get(tsk).getSessionName()))
					{
						String mu=tasks.get(tsk).getMuName();
						String session = tasks.get(tsk).getSessionName();
						if(session_mu_abb.containsKey(session+"_"+mu))
						{	
							tasks.get(tsk).setQueue(session_mu_abb.get(session+"_"+mu));
							tasks.get(tsk).update();
							System.out.println("[QUEUE] "+session_mu_abb.get(session+"_"+mu)+" set for [TSK] "+tasks.get(tsk).getIdentifier().getName());
;
						}
					}
				}
				
				
			}
			prtstm.close();
		
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
					config_params.get("USER"), config_params.get("PASSWORD"));
			if (!DUAPIConnections.containsKey(nodes[i] + "/"
					+ config_params.get("AREA"))) {
				DUAPIConnections.put(
						nodes[i] + "/" + config_params.get("AREA"), conn);

			}
			continue;
		}
	}

	
}
