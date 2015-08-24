package atco.tools;


import java.util.Set;

public class RenameSessionAtom {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String connectFile = args[0];
		String oldupr = args[1];
		String newupr = args[2];
		ATCO_Automation_Tool Fixing = new ATCO_Automation_Tool(connectFile);

	Set<String> keySet = Fixing.getDuApiConnections().keySet();
			
				
					
					for(String duapiconnectionKey:keySet)
					{
						try {

							Fixing.getDuApiConnections().get(duapiconnectionKey).updateSessionAtom(NamingUtils.getSessionFromUpr(oldupr), oldupr, newupr);
							
						
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						System.out.println();
						System.out.println("Done on "+duapiconnectionKey);
						System.out.println();
			
			
					}
			
			
		}
	
	


}
