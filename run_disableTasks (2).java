package atco.tools;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;



public class run_disableTasks {

	
	public static void main(String[] args) {
		
		String connectFile=args[0];
		String seslist=args[1];
		
		ATCO_Automation_Tool Fixing = new ATCO_Automation_Tool(connectFile);
		HashMap<String,String>toDisable_sessions = new HashMap<String,String>();
		CSVReader reader;


        System.out.println();
        System.out.println("--- DISABLING TASKS ON NODES ---");
        System.out.println();
       
		
        try {
    		
			reader = new CSVReader(new FileReader(seslist),',', '\"', '\0');
			String [] line;
		

			while ((line = reader.readNext()) != null) 
			{
				
				for(int i=0;i<line.length;i++)
				{
					if(!line[i].trim().equals("") )
					{
						toDisable_sessions.put(line[i],line[i]);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		Set<String> keySet = Fixing.getDuApiConnections().keySet();
	

			for(String duapiconnectionKey:keySet)
			{
			
				try {

						
						Fixing.getDuApiConnections().get(duapiconnectionKey).cleanUp_Task(toDisable_sessions);
						
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println();
				System.out.println("Done on "+duapiconnectionKey);
				System.out.println();
			}
			
			
			System.out.println("All done !");
		

	}

}
