package atco.tools;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;


public class SessionToCSV {

	
	
	
	
	public static void main(String[] args) {
		

        
		String sesList = args[0];
		
		ATCO_Automation_Tool Fixing = new ATCO_Automation_Tool();
		ArrayList<String>sessionList = new ArrayList<String>();
		
		
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(System.in));
		CSVReader reader;
		
		try {

		
			reader = new CSVReader(new FileReader(sesList),',', '\"', '\0');
		
		
			String [] line;
		

	   
			while ((line = reader.readNext()) != null) 
			{
				
				for(int i=0;i<line.length;i++)
				{
					if(!line[i].trim().equals("") )
					{
						sessionList.add(line[i]);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		String number_of_connections=null;
		
        System.out.println();
        System.out.println("--- .CSV EXTRACT ---");
        System.out.println();
        
        
        System.out.println();
        System.out.print("# of connections needed : ");
        try {
        	number_of_connections = buffreader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		
		
		
		for(int i=0;i<Integer.parseInt(number_of_connections);i++)
		{
			Fixing.addDuApiConnection();
		}
        
		System.out.println();
		System.out.println("Running extract ...might take a few minutes ...");
		System.out.println();
		
		Set<String> keySet = Fixing.getDuApiConnections().keySet();
	
		
			
			for(String duapiconnectionKey:keySet)
			{
			
				try {

					
						Fixing.getDuApiConnections().get(duapiconnectionKey).dumpNode(sessionList);

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
			
			System.out.println("All done !");
		

	
		
	}
}
