package atco.tools;



public class run_JobEventsCloner {

	public static void main(String[] args) {
		

		
			 DuApiConnection conn1 = new DuApiConnection( "C2_IST_CIS", "exp","CALPMPRS02.orsypgroup.com",4184,"admin","admin");
			 DuApiConnection conn2 = new DuApiConnection( "C2_IST_MKT", "exp","CALPMPRS02.orsypgroup.com",4184,"admin","admin");
			 try {
				
				 conn2.createJobEvents_fromList(conn1.getJobEventList());
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			

	}

}
