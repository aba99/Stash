package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.orsyp.api.session.Session;

import au.com.bytecode.opencsv.CSVReader;




public class Cmu_v2 {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();
	private static HashMap<String, String> JobId_Freq_fromCSV = new HashMap<String, String>();

	private static HashMap<String,String> sessionFilters = new HashMap<String,String>(); 

	
	public static void main(String[] args) {
		
		String connectFile = args[0];
		String inputCsv = args[1];
		String sessionFilterfile=args[2];

		
		connect(connectFile,true,true,true);
		readinput(inputCsv);
	    setSessionFilters(sessionFilterfile);

		
		for(String connKey:DUAPIConnections.keySet())
		{
			DuApiConnection conn = DUAPIConnections.get(connKey);
		
			HashMap<String, Session> sesList;
			try {
				
				sesList = conn.getSessionsHashMap_from_outside();
				
				for(String sesKey:sesList.keySet())
				{
					if(sessionFilters.containsKey(sesKey)){
					TimePlanSession tpSes = new TimePlanSession(sesKey,JobId_Freq_fromCSV,conn);
					System.out.println("For "+sesKey+ ":");
					tpSes.printTpUprocs();
					tpSes.fix();
					System.out.println("Expected # of [OPT TSK] for [SES] "+sesKey+ "= "+tpSes.getExpectedNumberOfOptTsk());
					System.out.println("----------------------------------------------------------------------");
					}
				}
			
			} catch (Exception e) {
				
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

	private static void readinput(String csvFile) {
		
		String[] line;
		boolean firstLine = true;


		// parse lines
		try {
			CSVReader reader = new CSVReader(new FileReader(csvFile), ',', '\"', '\0');

				while ((line = reader.readNext()) != null) {
				// skip first line
				if (firstLine) {
					firstLine = false;
					continue;
				}

				if (line.length < 2)
					continue;

				String jobID = line[0].trim();

				if (jobID.isEmpty()) {
					continue;
				}

				String freq = line[1].trim();

				if (!JobId_Freq_fromCSV.containsKey(jobID)) {
					JobId_Freq_fromCSV.put(jobID, freq);
				}

			}
				
				reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
