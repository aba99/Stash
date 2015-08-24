package stateofde;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.orsyp.owls.impl.Connector;

import stateofde.SessionModel;
import amex.tools.DuApiConnection;
import au.com.bytecode.opencsv.CSVReader;

public class CreateSession {

	static HashMap<String,String> ESP_UPR = new HashMap<String,String>();
	static HashMap<String,String> ESP_CHILDREN= new HashMap<String,String>();
	static HashMap<String,ArrayList<String>> $U_CHILDREN = new HashMap<String,ArrayList<String>>();
	
	static HashMap<String,ArrayList<String>> CHILD_PARENTS = new HashMap<String,ArrayList<String>>();
	
	public static void main(String[] args) throws Exception {

		String configFile = args[0];
		String esp_dollaru_file=args[1];
		String esp_children_file=args[2];
		
		readESP_DollarU_JobNamingMap(esp_dollaru_file);
		readESP_ChildrenMap(esp_children_file);
		
		Connector myNode = new Connector(configFile,true,"",true,"",true,"");
		
		String sessionName= "ABA";

		for(String uproc:ESP_CHILDREN.keySet())
		{
			        	
        	String[] tokens = ESP_CHILDREN.get(uproc).split("\\|");
        	ArrayList<String> childrenList_in$U= new ArrayList<String>();
        	
        	for(int t=0;t<tokens.length;t++)
        	{
        		if(!tokens[t].trim().isEmpty())
        		{
        			childrenList_in$U.add(getUprocNameIn$U(tokens[t].trim().toUpperCase()));
        			if(CHILD_PARENTS.containsKey(getUprocNameIn$U(tokens[t].trim().toUpperCase())))
        			{
        				CHILD_PARENTS.get(getUprocNameIn$U(tokens[t].trim().toUpperCase())).add(uproc);
        			}
        			else
        			{
        				ArrayList<String> listOfFathers = new ArrayList<String>();
        				listOfFathers.add(getUprocNameIn$U(uproc));
        				CHILD_PARENTS.put(getUprocNameIn$U(tokens[t].trim().toUpperCase()), listOfFathers);
        			}
        		}
        		
        	}
        	
        	$U_CHILDREN.put(getUprocNameIn$U(uproc), childrenList_in$U);
        	
		}
		
		for(String key:CHILD_PARENTS.keySet())
		{
			if(CHILD_PARENTS.get(key).size()>1)
			{
				String $U_Key = CHILD_PARENTS.get(key).get(0);
				for(String iterator:$U_CHILDREN.keySet())
				{
					if(!iterator.equalsIgnoreCase($U_Key))
					{
						if($U_CHILDREN.get(iterator).contains(key))
						{
							$U_CHILDREN.get(iterator).remove(key);
						}
					}
				}
			}
		}
		
		
		
		myNode.getConnectionList().get(0).createSession(sessionName, $U_CHILDREN);

		
		
	}

	
	public static String getUprocNameIn$U (String espjobName)
	{
		if(ESP_UPR.containsKey(espjobName))
		{
			return ESP_UPR.get(espjobName).toUpperCase();
		}
		else
		{
			return espjobName.toUpperCase();
		}
	}
	public static void readESP_DollarU_JobNamingMap(String file) throws IOException
	{
	CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
	
	String [] line;			

	//parse lines
	while ((line = reader.readNext()) != null) 
    {	    	
        if(line.length==2)
        {
        	if(!ESP_UPR.containsKey(line[0].trim().toUpperCase())&& !line[0].trim().isEmpty())
        	{
        		ESP_UPR.put(line[0].trim(),line[1].trim().toUpperCase());
        	}
        }
    }	
	
	
	reader.close();
	}
	
	public static void readESP_ChildrenMap(String file) throws IOException
	{
		CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
		
		String [] line;			

		//parse lines
		while ((line = reader.readNext()) != null) 
	    {	    	
	        if(line.length==2)
	        {
	
	        	if(!ESP_CHILDREN.containsKey(line[0]))
	        	{
	        		ESP_CHILDREN.put(line[0].trim(),line[1].trim());
	        	}
	        }
	    }	
	}

}
