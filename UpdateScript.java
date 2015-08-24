package my.tools;

import java.io.FileNotFoundException;

import com.orsyp.api.uproc.Uproc;


public class UpdateScript {

	public static void main(String[] args) {

		String configFile = args[0];
		try {
			
			Connector myconnection = new Connector(configFile,true,"",false,"",false,"");
			
			/*for(int u=0;u<100;u++)
			{
				try {
					myconnection.getConnectionList().get(0).duplicateUproc("SAP_TEST", "SAP_TEST_"+(u+1));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
			int count=0;
			
			for(int i=0;i<myconnection.getConnectionList().size();i++)
			{
				try {
					
					for(String upr:myconnection.getConnectionList().get(i).getUprocHashMap_from_outside().keySet())
					{
						Uproc uproc = myconnection.getConnectionList().get(i).getUprocHashMap_from_outside().get(upr);
						
						if(uproc.getType().equalsIgnoreCase("CL_INT"))
						{
							String[] currentScriptLines =myconnection.getConnectionList().get(i).extractInternalScript(uproc);
							
							for(int j=0;j<currentScriptLines.length;j++)
						
							{
							
								
							if(currentScriptLines[j].contains("uxcpy SAP REFJOBNAME=${JOBNAME} REFJOBCOUNT=${JOBCOUNT} JOBNAME=${NEWJOBNAME}"))
							{
								count++;
								System.out.println(count+" - UPR ["+uproc.getName()+"]");
								break;
							}
							
							}
							
						
						}
					}
		
				} catch (Exception e) {
					
					e.printStackTrace();
				}

			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		

	}


}
