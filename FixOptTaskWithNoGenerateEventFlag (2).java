package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


import java.io.PrintStream;

import java.util.HashMap;
import java.util.Scanner;


import com.orsyp.api.task.Task;
import com.orsyp.api.task.TaskPlanifiedData;
import com.orsyp.api.task.TaskType;

public class FixOptTaskWithNoGenerateEventFlag {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();

	public static void main(String[] args) {
		
		String connectFile = args[0];
		String outputFile = "opt_tsk_no_gen.txt";

		connect(connectFile);

		FileOutputStream fout = null;
		PrintStream prtstm = null;
		

		HashMap<String, Task> tsks = new HashMap<String, Task>();


		try {
			

			fout = new FileOutputStream(outputFile);
			prtstm = new PrintStream(fout);

			for (String connKey : DUAPIConnections.keySet()) {
				
				DuApiConnection conn = DUAPIConnections.get(connKey);
				//conn.deleteAllTemplateTasks();

				tsks = conn.getTaskHashMap_from_outside();
				
				int counter=0;
				
				for (String tsk : tsks.keySet()) {
				
					Task temp =tsks.get(tsk);
					if(temp.getTaskType().equals(TaskType.Optional))
					{
						TaskPlanifiedData tpd  =(TaskPlanifiedData) temp.getSpecificData();
						
						if(tpd!=null)
						{
							if(!tpd.isGenerateEvent())
							{
								tpd.setGenerateEvent(true);
								temp.setSpecificData(tpd);
								temp.update();
								counter++;
								//System.out.println(conn.getConnName()+","+temp.getIdentifier().getName());
								prtstm.println(conn.getConnName()+","+temp.getIdentifier().getName());
							}
							

						}
						
					}

				

				}

										
					System.out.println(conn.getConnName()+" : "+counter+" done");
				
			}
			
		
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
