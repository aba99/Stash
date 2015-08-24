package general.tools;
//This class reads only the lines with length()=number_of_fields from a csv file and stores the info
//in memory in a hashtable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

public class InMemoryFile {
	
	private String file ;
	private  int number_of_fields;
	private HashMap <String,ArrayList<String>> hash_store = new HashMap < String,ArrayList<String>>();


   public InMemoryFile(String fileName,int fields) throws FileNotFoundException
   {
	   if(fields>1)
	   {
		   number_of_fields =fields;
	   }
	   else
	   {
		   number_of_fields=1;
	   }
	   
	   file   =fileName;

	   
   }
   public void store() throws IOException
   {
	   ArrayList<String> arrayList = new ArrayList<String>();
	   
	   CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
		
		String [] line;			

		//parse lines
		while ((line = reader.readNext()) != null) 
	    {	    	
			
	        if(line.length==number_of_fields)
	        {
	        	String key=line[0];
	        	
	        	for(int l=1;l<number_of_fields;l++)
	        	{
	        		
	        		
	        		arrayList.add(line[l].trim());
	        		
	        	}
	        	
	        	if(!hash_store.containsKey(key))
	        	{
	        		ArrayList<String> placer = new ArrayList<String>();
	        		placer.addAll(arrayList);
	        		hash_store.put(key, placer);
	        		arrayList.clear();
	        	}
	        		
	        }
	    }	
		
		
		reader.close();
	   
	   
	   
	   
   }
   
    public HashMap<String,ArrayList<String>> getHash_Store()
    {
    	return hash_store;
    }
    
    public void printOut(PrintStream prtstm)
    {
    	for(String key:hash_store.keySet())
    	{
    		prtstm.print(key+",");
    		
    		for(int f=0;f<hash_store.get(key).size();f++)
    		{
    			prtstm.print(hash_store.get(key).get(f)+",");
    		}
    		
    		prtstm.println();
    	}
    }
    		
	

}
