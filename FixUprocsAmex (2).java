package amex.tools;
//identify uprocs with dep conditions that are run through other sessions and who have a different rule
import general.tools.DuApiConnection;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.session.Session;
import com.orsyp.api.task.Task;
import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.LaunchFormula;
import com.orsyp.api.uproc.SessionControl;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.owls.impl.Connector;


public class FixUprocsAmex {

	static HashMap<String,Uproc> uprs = new HashMap<String,Uproc>();
	static HashMap<String,Session> sess = new HashMap<String,Session>();
	static HashMap<String,Task> tsks = new HashMap<String,Task>();
	
	static String tech_uproc_template = "TECH_TEMPLATE";

	static ArrayList<String> sesfilters=new ArrayList<String>();
	
	public static void main(String[] argv) {
		
		String configFile = argv[0];
		String sessionFilters = argv[1];
		try {
			
		Connector myNode = new Connector(configFile,true,"",true,"",true,"");
		System.out.println("Let's do this !");
		System.out.println();
		
		readSessionFilters(sessionFilters);
		
		uprs= new HashMap<String,Uproc>(myNode.getConnectionList().get(0).getUprocHashMap_from_outside());
		sess= myNode.getConnectionList().get(0).getSessionsHashMap_from_outside();
		tsks= myNode.getConnectionList().get(0).getTaskHashMap_from_outside();
		
		ArrayList<String>techuprocs=new ArrayList<String>();
				
		int count=0;
		
	     
		for(int q=0;q<sesfilters.size();q++)
		{
			
				if(sess.containsKey(sesfilters.get(q)))
				{
					String sessionKey=sesfilters.get(q);
				
				for(int us=0;us<sess.get(sessionKey).getUprocs().length;us++)
				{
					if(uprs.containsKey(sess.get(sessionKey).getUprocs()[us]))
					{
						String uprIterator=sess.get(sessionKey).getUprocs()[us];
						
						if(!techuprocs.contains(uprIterator))
						{
							
								Uproc obj = uprs.get(uprIterator);
								Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
								Vector<DependencyCondition> depCons = obj.getDependencyConditions();
		
		
								for(int i=0;i<deps.size();i++)
								{
									if(deps.get(i).getSessionControl().getType().equals(SessionControl.Type.ANY_SESSION))
									{
										ArrayList<Task > matched_tasks_curUpr= myNode.getConnectionList().get(0).getTasksUprBelongsTo(tsks, obj.getName());
										ArrayList<Task > matched_tasks_depUpr= myNode.getConnectionList().get(0).getTasksUprBelongsTo(tsks,deps.get(i).getUproc());
									
			
										
										for(int k=0;k<matched_tasks_curUpr.size();k++)
										{
											for(int l=0;l<matched_tasks_depUpr.size();l++)
											{
												String rule_curUpr=myNode.getConnectionList().get(0).getAllRulesFromTask(matched_tasks_curUpr.get(k)).get(0);
												String rule_depUpr=myNode.getConnectionList().get(0).getAllRulesFromTask(matched_tasks_depUpr.get(l)).get(0);
												
												if(!rule_curUpr.equals(rule_depUpr)&& !rule_curUpr.equalsIgnoreCase("Provoked") && !rule_depUpr.equalsIgnoreCase("Provoked"))
												{
													count++;
													
													
													System.out.println(count+" : In SESSION \""+sessionKey+"\" UPROC \""+obj.getName()+"\" runs on RULE \""+rule_curUpr+"\" and has DEP COND \""
															
													+deps.get(i).getUproc()+"\" that runs on RULE \""+rule_depUpr+"\"");
													
													ArrayList<Session> atSession = DuApiConnection.getSessionsUprBelongsTo(sess,obj.getName());
													
													if(atSession.size()==1)
														{// find the session this uproc belongs to
														    String techUprName = obj.getName()+"_"+deps.get(i).getUproc()+"_TECH";
															myNode.getConnectionList().get(0).insertSessionAtom(atSession.get(0), obj.getName(),techUprName);
															myNode.getConnectionList().get(0).duplicateUproc(tech_uproc_template, techUprName);
															techuprocs.add(techUprName);
															
															Uproc techUproc = myNode.getConnectionList().get(0).getUprocHashMap_from_outside().get(techUprName);
															
															
															Vector<DependencyCondition> new_depCons_forTechUproc = new Vector<DependencyCondition>();
															LaunchFormula lf = new LaunchFormula();
																		
			
															for(int d=0;d<depCons.size();d++)
															{
																if(depCons.get(d).getUproc().equals(deps.get(i).getUproc()))
																{
																	
																	new_depCons_forTechUproc.add(depCons.get(d));
																	depCons.remove(d);
																	break;
																}
															}
															
															String text = "=C01";
															
															
															int num =new_depCons_forTechUproc.get(0).getNum();
															if(num<10)
															{
																text = "=C0"+num;
															}
															else 
															{
																text = "=C"+num;
															}
																									
															
															
															
															lf.appendText(text);
															techUproc.setFormula(lf);
															techUproc.setDependencyConditions(new_depCons_forTechUproc);
															techUproc.update(); //prepped the newly added techuproc : dependency fixed
															
															
			
															String mtskName="";//main task name to be found 
															
															if(myNode.getConnectionList().get(0).getSessionsUprBelongsTo(obj.getName()).size()==1)
															{
																for(String tskKeys:tsks.keySet())
																{
																	if(tsks.get(tskKeys).getSessionName().equalsIgnoreCase(myNode.getConnectionList().get(0).getSessionsUprBelongsTo(obj.getName()).get(0)))
																	{
																		mtskName=tsks.get(tskKeys).getIdentifier().getName();
																		break;
																	}
																
																}
															}
															myNode.getConnectionList().get(0).createMissingOptionalTask(mtskName, techUprName, rule_depUpr);
															//created the opttask on the newly added techuproc
															System.out.println("--------------------------");
															System.out.println("");
			
														}
												
												}
											}
										}
									}
										
								}
						
							
						//Stiching up the dependecies on the uproc at hand 
						LaunchFormula lformula = new LaunchFormula();
						String lf_string = "";
												
											
												
												for(int ncd=0;ncd<depCons.size();ncd++)
												{
													int num =depCons.get(ncd).getNum();

													
														if(ncd != (depCons.size()-1))
															{
																if(num<10){
																	lf_string = " =C0"+num+" AND";// OK
																}
																else 
																{
																	lf_string = " =C"+num+" AND";
																}
															}
															else
															{
																if(num<10){
																	lf_string = " =C0"+num;// OK
																	}
																	else 
																	{
																		lf_string = " =C"+(num);
																	}
															}

													
												
												}
												
												
												lformula.appendText(lf_string);
												obj.setFormula(lformula);
												obj.setDependencyConditions(depCons);
												obj.update();//removed the dependency because it's been added to the techuproc
												

									
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
