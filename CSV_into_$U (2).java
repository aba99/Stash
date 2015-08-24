package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;





public class CSV_into_$U {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();

	
	public static void main(String[] args) {
		
		String connectFile = args[0];
		String inputFile = args[1];
		
		connect(connectFile,true,true,true);
	   

		for(String connKey:DUAPIConnections.keySet())
		{
			DuApiConnection conn = DUAPIConnections.get(connKey);

			    try {
					conn.createTasksForSessionFromCSV(inputFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			    		
			 
			 
			 
		}	
			
	}
	
	private static void connect(String connectFile,boolean withUpr,boolean WithSes,boolean withTsk) {

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

		System.out.println("Connecting to UVMS : \""+config_params.get("HOST")+"\"");
		
		String nodes[] = config_params.get("NODE").split(",");

		for (int i = 0; i < nodes.length; i++) {

			DuApiConnection conn = new DuApiConnection(nodes[i],
					config_params.get("AREA"), config_params.get("HOST"),
					Integer.parseInt(config_params.get("PORT")),
					config_params.get("USER"), config_params.get("PASSWORD"),withUpr,WithSes,withTsk);
			if (!DUAPIConnections.containsKey(nodes[i] + "/"
					+ config_params.get("AREA"))) {
				DUAPIConnections.put(
						nodes[i] + "/" + config_params.get("AREA"), conn);

			}
			continue;
		}
	}

		
	
	
}
