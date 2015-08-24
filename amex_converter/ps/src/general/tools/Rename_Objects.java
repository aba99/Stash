package general.tools;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.session.Session;
import com.orsyp.api.task.Task;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.owls.impl.Connector;

public class Rename_Objects {


	static HashMap<String,Uproc> uprs = new HashMap<String,Uproc>();
	static HashMap<String,Session> sess = new HashMap<String,Session>();
	static HashMap<String,Task> tsks = new HashMap<String,Task>();
	static HashMap<String,String> oldUpr_newUpr = new HashMap<String,String>();
	
	public static void main(String[] args) throws Exception {

		String configFile = args[0];
		String file = args[1];
		
		readMapFile(file);//reads the mapping
		
		Connector myNode = new Connector(configFile,true,"",true,"",true,"");

		uprs= new HashMap<String,Uproc>(myNode.getConnectionList().get(0).getUprocHashMap_from_outside());
		sess= myNode.getConnectionList().get(0).getSessionsHashMap_from_outside();
		tsks= myNode.getConnectionList().get(0).getTaskHashMap_from_outside();
		
		
		for(String uprocKey:uprs.keySet())
		{
			myNode.getConnectionList().get(0).updateDepConsName(uprocKey, oldUpr_newUpr);
		}//update the depcons on all uprocs first
		
		for(String curUpr:oldUpr_newUpr.keySet())
		{
			if(uprs.containsKey(curUpr))
			{
				
				myNode.getConnectionList().get(0).duplicateUproc(curUpr,oldUpr_newUpr.get(curUpr));
				uprs.get(curUpr).delete();
				
			}//duplicate old upr into new upr and delete the old one.
		}
		
		for(String sesKey:sess.keySet())
		{
			myNode.getConnectionList().get(0).updateUprocNameInSession(sesKey, oldUpr_newUpr);
		}
		
		for(String tskKey : tsks.keySet())
		{
			myNode.getConnectionList().get(0).updateTaskWithNewUprocName(tskKey, oldUpr_newUpr);
		}
		
		
		
		
	}

	public static void readMapFile(String file) throws IOException
{
	CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
	
	String [] line;			

	//parse lines
	while ((line = reader.readNext()) != null) 
    {	    	
        if(line.length==2)
        {
        	if(!line[0].trim().equalsIgnoreCase(line[1].trim()) && !oldUpr_newUpr.containsKey(line[0]))
        	{
        		oldUpr_newUpr.put(line[0].trim(),line[1].trim());
        	}
        }
    }	
	
	
	reader.close();
}

}
