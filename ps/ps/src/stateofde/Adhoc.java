package stateofde;

import java.util.ArrayList;

import com.orsyp.owls.impl.Connector;

public class Adhoc {

	public static void main(String[] args) throws Exception {
	
		String configFile = args[0];
		Connector myNode = new Connector(configFile,true,"",false,"",false,"");

		myNode.getConnectionList().get(0).updateOPT_TaskNameAdhoc();
 		int count =0;
		for(String curUpr:myNode.getConnectionList().get(0).getUprocHashMap_from_outside().keySet())
		{
			count++;
			System.out.print(count+" -");
			myNode.getConnectionList().get(0).removeDepConsFromUproc(curUpr);
		}
		//ArrayList<String> deps = new ArrayList<String>();
 		//deps.add("CLEAN FILE");
 		///deps.add("CONTINUE");
 		
		//myNode.getConnectionList().get(0).addDepConsToUproc("AAAB", deps);
	}

}
