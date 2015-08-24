package stateofde;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.session.Session;
import com.orsyp.api.task.Task;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.owls.impl.Connector;

public class MigrationValidator {

	//input hashmaps
	static HashMap<String,String> ESP_DollarU_JobNamingMap = new HashMap<String,String>();
	static HashMap<String,String> ESP_DollarU_FrequencyMap = new HashMap<String,String>();
	static HashMap<String,ArrayList<String>> ESPJOB_FathersMap = new HashMap<String, ArrayList<String>>();
	static HashMap<String,ArrayList<String>> ESPJOB_FrequencyMap = new HashMap<String,ArrayList<String>>();
	
	
	//placeholder hashmap
	static HashMap<String,UnitInfo> unitObjectsIn$U=new HashMap<String,UnitInfo>();
	static HashMap<String,UnitInfo> unitObjectsInTheory=new HashMap<String,UnitInfo>();

	
	static HashMap<String,Uproc> uprs = new HashMap<String,Uproc>();
	static HashMap<String,Session> sess = new HashMap<String,Session>();
	static HashMap<String,Task> tsks = new HashMap<String,Task>();
	 
	
	public static void main(String[] args) throws Exception {
		
		String connectFile = args[0];
		String file_ESP_DollarU_JobNamingMap = args[1];
		String file_ESP_DollarU_FrequencyMap = args[2];
		String file_ESPJOB_FathersMap = args[3];
		String file_ESPJOB_FrequencyMap=args[4];
		
		//input hashmaps
		readESP_DollarU_JobNamingMap(file_ESP_DollarU_JobNamingMap);//reads the mapping
		readESP_DollarU_FrequencyMap(file_ESP_DollarU_FrequencyMap);
		readESPJOB_FathersMap(file_ESPJOB_FathersMap);
		readESPJOB_FrequencyMap(file_ESPJOB_FrequencyMap);
		
	
		
		Connector myNode = new Connector(connectFile,true,"",true,"",true,"");

		uprs= myNode.getConnectionList().get(0).getUprocHashMap_from_outside();
		sess= myNode.getConnectionList().get(0).getSessionsHashMap_from_outside();
		tsks= myNode.getConnectionList().get(0).getTaskHashMap_from_outside();

	    	for(String uprocKey:uprs.keySet())
	    	{
	    		System.out.println("Reading $U info for "+uprocKey);

	    		UnitInfo uinfo = new UnitInfo();
	    		
	    		uinfo.name=uprocKey;
	    		uinfo.listOfFathers = new ArrayList<String>(myNode.getConnectionList().get(0).getListOfFathersForUproc(uprocKey));
	    		uinfo.listOfRules  = new ArrayList<String>(myNode.getConnectionList().get(0).getListOfRulesForUproc(uprocKey));
	    		uinfo.isESP = false;
	    		
	    		unitObjectsIn$U.put(uprocKey, uinfo);
	    		
	    	}
	    	
	    	for(String espJobKey:ESP_DollarU_JobNamingMap.keySet())
	    	{
	    		System.out.println("Reading ESP info for "+espJobKey);
	    		UnitInfo uinfo_theory = new UnitInfo();
	    		
	    		uinfo_theory.name=getUprocNameIn$U(espJobKey);
	    		
	    		if(ESPJOB_FathersMap.containsKey(espJobKey))
	    		{
	    			for(int f=0;f<ESPJOB_FathersMap.get(espJobKey).size();f++)
	    			{
	    				uinfo_theory.listOfFathers.add(getUprocNameIn$U(ESPJOB_FathersMap.get(espJobKey).get(f)));
	    			}
	    		}
	    		else
	    		{
	    			uinfo_theory.listOfFathers.add(espJobKey+" NOT MAPPED IN <ESPJOB><FATHER> LIST");
	    		}
	    		
	    		if(ESPJOB_FrequencyMap.containsKey(espJobKey))
	    		{
	    			for(int freq=0;freq<ESPJOB_FrequencyMap.get(espJobKey).size();freq++)
	    			{
	    				uinfo_theory.listOfRules.add(getRuleNameIn$U(ESPJOB_FrequencyMap.get(espJobKey).get(freq)));
	    			}
	    		}
	    		else
	    		{
	    			uinfo_theory.listOfRules.add(espJobKey+" NOT MAPPED IN <ESPJOB><FREQUENCY> LIST");
	    		}
	    		
	    		uinfo_theory.isESP=true;
	    		
	    		unitObjectsInTheory.put(uinfo_theory.name, uinfo_theory);
	    	}
	    	

	    	ArrayList<String>theory_uprocs=new ArrayList<String>(unitObjectsInTheory.keySet());
	    	ArrayList<String> $U_uprocs = new ArrayList<String>(unitObjectsIn$U.keySet());
	    	
	    	System.out.println("Diff Comparison on Uproc List :");
	    	System.out.println("In theory : #"+getDiff(theory_uprocs,$U_uprocs).get("a").size()+":"+getDiff(theory_uprocs,$U_uprocs).get("a"));
	    	System.out.println("In $U     : #"+getDiff(theory_uprocs,$U_uprocs).get("b").size()+":"+getDiff(theory_uprocs,$U_uprocs).get("b"));
	  
			theory_uprocs.retainAll($U_uprocs);
			ArrayList<String>commonKeys = new ArrayList<String>(theory_uprocs);
			
			
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			
			for(int i=0;i<commonKeys.size();i++)
			{
				ArrayList<String> DepsToBeAdded= new ArrayList<String>(getDiff(unitObjectsInTheory.get(commonKeys.get(i)).listOfFathers,unitObjectsIn$U.get(commonKeys.get(i)).listOfFathers).get("a"));
				ArrayList<String> RulesToBeAdded= new ArrayList<String>(getDiff(unitObjectsInTheory.get(commonKeys.get(i)).listOfRules,unitObjectsIn$U.get(commonKeys.get(i)).listOfRules).get("a"));

				if(DepsToBeAdded.size()>0){
					
					
					System.out.println("Uproc <"+commonKeys.get(i)+"> is missing "						
							+getDiff(unitObjectsInTheory.get(commonKeys.get(i)).listOfFathers,unitObjectsIn$U.get(commonKeys.get(i)).listOfFathers).get("a").size()+":"
							+getDiff(unitObjectsInTheory.get(commonKeys.get(i)).listOfFathers,unitObjectsIn$U.get(commonKeys.get(i)).listOfFathers).get("a"));
				
				
				
					myNode.getConnectionList().get(0).addDepConsToUproc(commonKeys.get(i), DepsToBeAdded);
				}

			
				
				
				
				
				String mtskName="";//main task name to be found 
				if(myNode.getConnectionList().get(0).getSessionsUprBelongsTo(commonKeys.get(i)).size()==1)
				{
					for(String tskKeys:tsks.keySet())
					{
						if(tsks.get(tskKeys).getSessionName().equalsIgnoreCase(myNode.getConnectionList().get(0).getSessionsUprBelongsTo( commonKeys.get(i)).get(0)))
						{
							mtskName=tsks.get(tskKeys).getIdentifier().getName();
							break;
						}
					
					}
				}
				String rule = "CAL";
				
			if(RulesToBeAdded.size()==1 )	
			{	
				
			
			if(!RulesToBeAdded.get(0).contains("NOT MAPPED"))
			{
				rule =RulesToBeAdded.get(0).toUpperCase();
			}
			
			myNode.getConnectionList().get(0).createMissingOptionalTask(mtskName, commonKeys.get(i), rule);
			}
			
			}
	}
	
	public static String getUprocNameIn$U (String espjobName)
	{
		if(ESP_DollarU_JobNamingMap.containsKey(espjobName))
		{
			return ESP_DollarU_JobNamingMap.get(espjobName);
		}
		else
		{
			return espjobName+" NOT MAPPED IN <ESPJOB><UPROCNAME> LIST";
		}
	}
	public static String getRuleNameIn$U (String Freq)
	{
		if(ESP_DollarU_FrequencyMap.containsKey(Freq))
		{
			return ESP_DollarU_FrequencyMap.get(Freq);
		}
		else
		{
			return Freq+" NOT MAPPED IN <ESPFREQ><RULE> LIST";
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
        	if(!ESP_DollarU_JobNamingMap.containsKey(line[0].trim()) && !line[0].trim().isEmpty())
        	{
        		ESP_DollarU_JobNamingMap.put(line[0].trim().toUpperCase(),line[1].trim().toUpperCase());
        	}
        }
    }	
	
	
	reader.close();
}
	public static void readESPJOB_FathersMap(String file) throws IOException
{
	CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
	
	String [] line;			

	//parse lines
	while ((line = reader.readNext()) != null) 
    {	    	
        if(line.length==2)
        {
        	ArrayList<String> holder = new ArrayList<String>();
        	
        	String[] tokens = line[1].split("\\|");
        	
        	
        	for(int t=0;t<tokens.length;t++)
        	{
        		if(!tokens[t].trim().isEmpty())
        		{
        			holder.add(tokens[t].trim().toUpperCase());
        		}
        	}
        	
        	if(!ESPJOB_FathersMap.containsKey(line[0].trim())&& !line[0].trim().isEmpty())
        	{
        		//System.out.print("ESP+FATHER "+line[0]+" has been instered with "+holder);
        		ESPJOB_FathersMap.put(line[0].trim().toUpperCase(),holder);
        	}
        }
    }	
	
	
	reader.close();
}

	public static void readESP_DollarU_FrequencyMap(String file) throws IOException
{//reads csv file that contains <name of esp frequency>,<dollaru_rul>
	CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
	
	String [] line;			

	//parse lines
	while ((line = reader.readNext()) != null) 
    {	    	
        if(line.length==2)
        {
        	
   
        	
        	if(!ESP_DollarU_FrequencyMap.containsKey(line[0].trim())&& !line[0].trim().isEmpty()
        			&& !line[1].trim().isEmpty())
        	{
        		ESP_DollarU_FrequencyMap.put(line[0].trim().toUpperCase(),line[1].trim().toUpperCase());
        	}
        }
    }	
	
	
	reader.close();
}
		
	public static void readESPJOB_FrequencyMap(String file) throws IOException
	{//reads <ESPJOB><frequency>
		CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
		
		String [] line;			

		//parse lines
		while ((line = reader.readNext()) != null) 
	    {	    	
	        if(line.length==2)
	        {
	        	ArrayList<String> holder = new ArrayList<String>();
	        	
	        	String[] tokens = line[1].split("\\|");
	        	
	        	
	        	for(int t=0;t<tokens.length;t++)
	        	{
	        		if(!tokens[t].trim().isEmpty())
	        		{
	        			holder.add(tokens[t]);
	        		}
	        	}
	        	
	        	if(!ESPJOB_FrequencyMap.containsKey(line[0].trim().toUpperCase())&& !line[0].trim().isEmpty())
	        	{
	        		ESPJOB_FrequencyMap.put(line[0].trim().toUpperCase(),holder);
	        	}
	        }
	    }	
		
		
		reader.close();
	}
	

	public static HashMap<String,List<String>> getDiff(ArrayList<String> a,ArrayList<String>b)
	{
		HashMap<String,List<String>> result = new HashMap<String,List<String>>();
		
	    List<String> sourceList = new ArrayList<String>(a);
	    List<String> destinationList = new ArrayList<String>(b);


	    sourceList.removeAll( b );
	    destinationList.removeAll( a );
	    
	    result.put("a",sourceList );
	    result.put("b",destinationList);
	    
	    return result;
	}
	
	public static void displayHashMap(HashMap<String,UnitInfo> hm)
	{
		for(String key:hm.keySet())
		{
			System.out.println("UPROC -"+hm.get(key).name);
			System.out.println("------------------------------------");
			System.out.println("Father List:");
			for(int h=0;h<hm.get(key).listOfFathers.size();h++)
			{
				System.out.println(hm.get(key).listOfFathers.get(h));
			}
			System.out.println("Rules List :");
			for(int r=0;r<hm.get(key).listOfRules.size();r++)
			{
				System.out.println(hm.get(key).listOfRules.get(r));
			}
			System.out.println();
		}
	}
	
	}
