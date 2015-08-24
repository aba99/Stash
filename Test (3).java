package general.tools;

import java.io.IOException;
import java.util.ArrayList;

import com.orsyp.UniverseException;
import com.orsyp.owls.impl.Connector;

public class Test {

	public static void main(String[] args) throws IOException, UniverseException {

		String fileName = args[0];
		Connector conn = new Connector(fileName,true,"",true,"",true,"");
	
		
		ArrayList<String> deps = new ArrayList<String>();
 		deps.add("B");
 		deps.add("B");
 		deps.add("C");
 		
		try {
			conn.getConnectionList().get(0).setDepConOnUproc("AHMAD", deps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*ArrayList<Job> list = new ArrayList<Job>(conn.getConnectionList().get(0).uprocsToJob());	
	
		for(int i=0;i<list.size();i++)
		{
			list.get(i).print(System.out);
		}
		
		ArrayList<String>rls=new ArrayList<String>();
		rls.add("DEP1");
		rls.add("DEP2");
		
		conn.getConnectionList().get(0).normalizeSchedules("AHMAD",rls );
		
		ArrayList<Job> list2 = new ArrayList<Job>(conn.getConnectionList().get(0).uprocsToJob());	

		for(int i=0;i<list2.size();i++)
		{
			list2.get(i).print(System.out);
		}*/
		
	/*		InMemoryFile myFile = new InMemoryFile(fileName,3);
		myFile.store();
		
		myFile.printOut(System.out);
		
		Job a = new Job("myJob");
		a.print(System.out);
		a.addChild("Baby");
		a.addFather("Sobhi");
		a.addRule("EveryDay");
		a.print(System.out);
	
		Job b = new Job ("myOtherJob");
		b.addChild("Baby");
		b.addFather("Sobhi");
		b.addFather("Ahmad");
		b.addRule("EveryDay");
		
		System.out.println(a.isEqual(b));
		b.print(System.out);*/
	}
	

}
