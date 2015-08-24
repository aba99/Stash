package amex.tools;
//identify uprocs with dep conditions that are run through other sessions and who have a different rule
	import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.session.Session;
import com.orsyp.api.task.Task;
import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.SessionControl;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.owls.impl.Connector;

public class Depistage_v2 {

	



		static HashMap<String,Task> tsks = new HashMap<String,Task>();
		static ArrayList<String> sesfilters=new ArrayList<String>();


		
		public static void main(String[] argv) {
			
			String configFile = argv[0];
			String sessionFilters = argv[1];
			try {
				
			Connector myNode = new Connector(configFile,false,"",true,"",true,"");
			tsks=myNode.getConnectionList().get(0).getTaskHashMap_from_outside();
			
			System.out.println("Let's do this !");
			System.out.println();
			
			readSessionFilters(sessionFilters);
			
			
					
			int count=0;
			
		     
			for(int q=0;q<sesfilters.size();q++)
			{
				
						String sessionKey=sesfilters.get(q);
						System.out.println("In SESSION \""+sessionKey+"\" :");

						
						Session currentSession = myNode.getConnectionList().get(0).getSession(sessionKey);
						
						for(int us=0;us<currentSession.getUprocs().length;us++)
						{
								String uprIterator=currentSession.getUprocs()[us];
								

								
									Uproc obj = myNode.getConnectionList().get(0).getUproc(uprIterator);
									Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
									ArrayList<Task > matched_tasks_curUpr= myNode.getConnectionList().get(0).getTasksUprBelongsTo(tsks, obj.getName());

			
									for(int i=0;i<deps.size();i++)
									{
										if(deps.get(i).getSessionControl().getType().equals(SessionControl.Type.ANY_SESSION))
										{

											System.out.println("-UPR \""+uprIterator+" has DEPCON \""+deps.get(i).getUproc()+"\" in ANY_SESSION ");
											
											ArrayList<Task > matched_tasks_depUpr= myNode.getConnectionList().get(0).getTasksUprBelongsTo(tsks,deps.get(i).getUproc());
										
											System.out.println("-- "+matched_tasks_curUpr.size()+" task(s) on UPR "+obj.getName()+" from SESSION "+sessionKey+" listed below:");
											for(int tu=0;tu<matched_tasks_curUpr.size();tu++)
											{
												System.out.println("-- "+matched_tasks_curUpr.get(tu).getIdentifier().getName());
											}
											
											
											System.out.println("-- "+matched_tasks_depUpr.size()+" task(s) on DEPCON "+deps.get(i).getUproc()+" listed below:");											
											for(int rr=0;rr<matched_tasks_depUpr.size();rr++)
											{
												System.out.println("-- "+matched_tasks_depUpr.get(rr).getIdentifier().getName());
												
											}

											for(int k=0;k<matched_tasks_curUpr.size();k++)
											{
												for(int l=0;l<matched_tasks_depUpr.size();l++)
												{
													String rule_curUpr=myNode.getConnectionList().get(0).getRuleFromTask(matched_tasks_curUpr.get(k));
													String rule_depUpr=myNode.getConnectionList().get(0).getRuleFromTask(matched_tasks_depUpr.get(l));
													
													if(!rule_curUpr.equals(rule_depUpr)&& !rule_curUpr.equalsIgnoreCase("Provoked") && !rule_depUpr.equalsIgnoreCase("Provoked"))
													{
														count++;
														
														
														System.out.println("---> Action #"+count+" : In \""+sessionKey+"\" UPR \""+obj.getName()+"\" runs on RULE \""+rule_curUpr+"\" and has DEP COND \""
																
														+deps.get(i).getUproc()+"\" that runs on RULE \""+rule_depUpr+"\"");
														System.out.println("-------------------------------------------");
													}
												}
											}
										}
									}
							}
						
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();

		}
	}
							
							
		private static void readSessionFilters(String file) throws IOException
							{
								
								CSVReader reader = new CSVReader(new FileReader(file),',', '\"', '\0');
								
								String [] line;			
							
								//parse lines
								while ((line = reader.readNext()) != null) 
							    {	    	
							        for(int i=0;i<line.length;i++)
							        {
							        	sesfilters.add(line[i].trim());
							        }
							    }	
								
								
								reader.close();
							}
}
