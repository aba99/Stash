package atco.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;


public class run_AAT {

	
	public static void main(String[] args) {
		
		
		String csvFile = args[0];
		String sesList = args[1];
		String connFile=args[2];
		
		ATCO_Automation_Tool Fixing = new ATCO_Automation_Tool(csvFile,sesList,connFile);
		ArrayList<String>sessionList = new ArrayList<String>();
		
		boolean buildUPRSESTSKCSV=false;
			String choice = null;
	
		CSVReader reader;
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(System.in));
       
		@SuppressWarnings("unused")
		String number_of_connections = null;
		
       
        System.out.println();
        System.out.println("[1] --> UPR/SES/TSK FROM CSV    ");
        System.out.println("[0] --> QUIT");
        
        
        while(choice==null || choice.trim().isEmpty())
        {
            System.out.print("Choice : ");
            try {
            	choice = buffreader.readLine().trim();
            	
            } catch (IOException e) {
                e.printStackTrace();
            } 
            
         }
            
            if(choice.equalsIgnoreCase("1"))
            {
            	buildUPRSESTSKCSV=true;
            }
        
            else
            {
            	System.exit(0);
            }
           

		if(buildUPRSESTSKCSV)
		{
		
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

		
        
        String withTskRepl = null;
        String withUprRepl   = null;
        String withSesRepl   = null;
         
        boolean withTsk = false;
        boolean withUpr = false;
        boolean withSes = false;
       
        
        System.out.println();
        System.out.println("--------------------------------------");
        System.out.println("--- CREATING UPR/SES/TSK  FROM CSV ---");
        System.out.println("--------------------------------------");
        System.out.println();

   
        
      
        while(withUprRepl==null || withUprRepl.trim().isEmpty()){
            System.out.print("Create/Replace UPR ? [no by default] : ");
            try {
            	withUprRepl = buffreader.readLine().trim();
            	
            } catch (IOException e) {
                e.printStackTrace();
            } 
            
            }
        
        while(withSesRepl==null || withSesRepl.trim().isEmpty()){
            System.out.print("Create/Replace SES ? [no by default] : ");
            try {
            	withSesRepl = buffreader.readLine().trim();
            	
            } catch (IOException e) {
                e.printStackTrace();
            } 
            
            }
        
        
        while(withTskRepl==null || withTskRepl.trim().isEmpty()){
        System.out.print("Create/Replace TSK ? [no by default] : ");
        try {
        	withTskRepl = buffreader.readLine().trim();
        	
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        }
        
        if(withTskRepl.equalsIgnoreCase("yes"))
        {
        	withTsk=true;
        }
        
        if(withUprRepl.equalsIgnoreCase("yes"))
        {
        	withUpr=true;
        }
        if(withSesRepl.equalsIgnoreCase("yes"))
        {
        	withSes=true;
        }
        
        
        
        if(!withTsk && !withUpr && !withSes)
        {
        	System.out.println("Done");
        	System.exit(0);
        }
        
/*        
        
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
		}*/
		
		Set<String> keySet = Fixing.getDuApiConnections().keySet();
	
		
			
			for(String duapiconnectionKey:keySet)
			{
				try {

						for(int i=0;i<sessionList.size();i++)
						{
							
							Fixing.build_UPR_SES_TSK_from_CSV(duapiconnectionKey,sessionList.get(i),withUpr,withSes,withTsk);
						}
						
						if(withTsk)
						{
							 	System.out.println();
						        System.out.println("--------------------------------------");
						        System.out.println("TSK CLEANUP ON NODE ["+duapiconnectionKey+"]");
						        System.out.println("--------------------------------------");
						        
							//Fixing.getDuApiConnections().get(duapiconnectionKey).cleanUp_Task();
						}

				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println();
				System.out.println("Done on "+duapiconnectionKey);
				System.out.println();
			
				
			
			}
			
			System.out.println("All done , check !");

	}
	
	}
}
