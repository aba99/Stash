package atco.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;


import java.util.Vector;

import com.orsyp.UniverseException;
import com.orsyp.api.Variable;
import com.orsyp.api.session.Session;
import com.orsyp.api.uproc.CompletionInstruction;
import com.orsyp.api.uproc.DependencyCondition;
import com.orsyp.api.uproc.Memorization;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.uproc.cl.InternalScript;


public class Uprocs_Dump {

	private static HashMap<String, String> config_params = new HashMap<String, String>();
	private static HashMap<String, DuApiConnection> DUAPIConnections = new HashMap<String, DuApiConnection>();


	
	public static void main(String[] args) {
		
		String connectFile = args[0];
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmm");
		String formattedDate = sdf.format(date);
		
		connect(connectFile);

		HashMap<String,Uproc> current_uprs = new HashMap<String,Uproc>();
		

		try {
			

			FileOutputStream fout = null;
			PrintStream prtstm = null;
			
			fout = new FileOutputStream("upr_dump_"+formattedDate+".csv");
			prtstm = new PrintStream(fout);

			prtstm.println("NODE,UPROC,SEVERITY,LABEL,VERSION,FORMULA,DEPCONS,CINSTR,MEMO,SCRIPT,VARIABLES,APP,DOMAIN,BELONGS TO SESSION");
			
			for (String connKey : DUAPIConnections.keySet()) {
				
				DuApiConnection conn = DUAPIConnections.get(connKey);

				current_uprs = conn.getUprocHashMap_from_outside();
				String output="";
				
				for(String uprIterator:current_uprs.keySet())
				{
					Uproc obj = current_uprs.get(uprIterator);
					
				
					String name = obj.getName();
					String severity= Integer.toString(obj.getDefaultSeverity());
					
					
					Vector<CompletionInstruction> cins=obj.getCompletionInstructions();
					String compIns = "";
					for(int c=0;c<cins.size();c++)
					{
						compIns+=cins.get(c).getUproc()+"|";
					}
					
					
					String label = obj.getLabel();
					String app  = obj.getApplication();
					String formula = "("+obj.getFormula().toString()+")";
					
					Vector<DependencyCondition> deps = new Vector<DependencyCondition>(obj.getDependencyConditions());
					
					String depcons = "";
					
					for(int i=0;i<deps.size();i++)
					{
						depcons+=deps.get(i).getNum()+":"+deps.get(i).getUproc()+"|";
					}
					
					Memorization memor =obj.getMemorization();
					String memo = memor.getType().name();
					
					
					String version = obj.getVersion();
					
					Vector<Variable> varia=obj.getVariables();
					String variables = "";
					for(int v=0;v<varia.size();v++)
					{
						variables+=varia.get(v).getName()+"="+varia.get(v).getValue()+"|";
					}
					
					
					String domain= obj.getDomain();
					String firstScriptLine="empty";
					String[] scriptLines = extractInternalScript(obj);
					 
					if (scriptLines != null) 
					 {
						 firstScriptLine=scriptLines[0];
					 }

					output = conn.getConnName()+","+name    + "," + severity  +"," + label 
							+ ","+version + "," 
						 + formula +","+depcons  +"," +compIns+","
							+ memo +","+firstScriptLine+","+variables+","+ app +"," + domain+","+getSessionsUprBelongsTo(conn.getSessionsHashMap_from_outside(), name);			
				
					prtstm.println(output);
				
				}
						
				
			}
			prtstm.close();
		
		}catch (Exception e) {
			e.printStackTrace();

		}

	}

	private static void connect(String connectFile) {

		// / Reading connectFile info
		Scanner sc = null;
		try {
			sc = new Scanner(new File(connectFile));
			while (sc.hasNext()) {
				String line = sc.nextLine().trim();
				if (line.length() > 0)
					if (!line.startsWith("#"))
						if (line.contains("="))
							config_params.put(line.split("=")[0].trim()
									.toUpperCase(),
									line.substring(line.indexOf("=") + 1)
											.trim());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

		if (!config_params.containsKey("NODE")
				|| !config_params.containsKey("PORT")
				|| !config_params.containsKey("AREA")
				|| !config_params.containsKey("HOST")
				|| !config_params.containsKey("USER")
				|| !config_params.containsKey("PASSWORD")) {
			System.out.println("Error ! Missing fields in UVMS Config   :\""
					+ connectFile + "\"");
			System.exit(-1);
		}

		System.out.println("Connecting to UVMS : \""+config_params.get("HOST")+"\"");

		String nodes[] = config_params.get("NODE").split(",");

		for (int i = 0; i < nodes.length; i++) {

			DuApiConnection conn = new DuApiConnection(nodes[i],
					config_params.get("AREA"), config_params.get("HOST"),
					Integer.parseInt(config_params.get("PORT")),
					config_params.get("USER"), config_params.get("PASSWORD"),true,true,false);
			if (!DUAPIConnections.containsKey(nodes[i] + "/"
					+ config_params.get("AREA"))) {
				DUAPIConnections.put(
						nodes[i] + "/" + config_params.get("AREA"), conn);

			}
			continue;
		}
	}
	
    protected static String[] extractInternalScript(Uproc u) throws UniverseException {
        
        InternalScript script = new InternalScript(u);
        script.extractContent();
        //printf("Internal script extraction for Uproc [%s] :\n", u.getName());

        String[] lines = script.getLines();
        return lines;
        /*if (lines != null) {
            for (String line : lines) {
                System.out.println(line);
            }
        }*/
    }
    
	private static String getSessionsUprBelongsTo(HashMap<String, Session> sessions, String upr) {
		ArrayList<String> matches = new ArrayList<String>();

		for (String s : sessions.keySet()) {
			for (int u = 0; u < sessions.get(s).getUprocs().length; u++) {
				if (sessions.get(s).getUprocs()[u].equalsIgnoreCase(upr)) {
					matches.add(s);
				}
			}
		}

		if (matches.size() == 0) {
			matches.add("NONE");
		}

		String output="";
		for(int s=0;s<matches.size();s++)
		{
				output+="|"+matches.get(s);
		}
		
		return output;
	}

	
}
