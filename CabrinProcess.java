package com.orsyp.client.amex;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

import com.orsyp.api.Variable;
import com.orsyp.api.execution.ExecutionStatus;
import com.orsyp.api.launch.Launch;
import com.orsyp.api.launch.LaunchId;
import com.orsyp.api.syntaxerules.OwlsSyntaxRules;
import com.orsyp.api.uproc.Uproc;
import com.orsyp.api.uproc.UprocId;
import com.orsyp.owls.impl.launch.OwlsLaunchImpl;
import com.orsyp.tools.ps.DuApiConnection;

public class CabrinProcess {
	//this class represents the process that gets triggered when a file is dropped

	private String fileName;//fullName of the actual file that came in
	private String googleNumber;//part of the fullName .G0000V00.
	private String relatedMainUproc;//the main job this cabrin process triggers
	private String relatedCabrinUproc;//the actual name of the CabrinUproc
	private Vector<Variable> variables;//uproc variables with updated fileName and googleNumber
	private static Uproc templateCabrinUproc;//static templateCabrinUproc needed for variableModifications
	
	private static boolean isTempUprocSet = false;
	public static int count = 0;//general counter
	public static HashMap<String,Queue<CabrinProcess>>publicboard=new HashMap<String,Queue<CabrinProcess>>();
	public static DuApiConnection duapi;
	private static ArrayList<ExecutionStatus> array  = new ArrayList<ExecutionStatus>();
	private static HashMap<String,ArrayList<String>> reference = new HashMap<String,ArrayList<String>>();
	
	public CabrinProcess(DuApiConnection conn,String templateUproc,String refFile) throws Exception
	{//upr is the name of the templateUproc
		array.add(ExecutionStatus.Running);
		array.add(ExecutionStatus.Aborted);
		array.add(ExecutionStatus.Pending);
		
		
		duapi=conn;
		
		
		if(duapi.doesUprocExist(templateUproc))
		{
			templateCabrinUproc=duapi.getUproc(templateUproc);
			isTempUprocSet=true;
			count++;
		}
		readReferenceFile(refFile);
	}
	public CabrinProcess(String filename,String cabrinjobname,String mainjobname) throws Exception
	{
		if(!isTempUprocSet)
		{
			System.out.println(" CabrinProcess class needs a one-time setTemplateUproc(String uprName,DuApiConnection)");
			System.exit(-1);
			
		}
	
		fileName=filename;
		relatedMainUproc=mainjobname;
		relatedCabrinUproc=cabrinjobname;
		googleNumber =getGoogleNumber(filename);
		deriveUprocVariables();
		
		if(!publicboard.containsKey(relatedCabrinUproc))
		{
			 Queue<CabrinProcess> myQ=new LinkedList<CabrinProcess>();
			 publicboard.put(relatedCabrinUproc, myQ);
			 
		}
		
		publicboard.get(relatedCabrinUproc).add(this);

		count++;

		
		
		
	}
	public Vector<Variable> getCabrinUprocVariables()
	{
		System.out.println(" Update uproc :"+getCabrinJob());
		System.out.println("Variable_1 : "+variables.get(0).getName()+"-"+variables.get(0).getValue());
		System.out.println("Variable_2 : "+variables.get(1).getName()+"-"+variables.get(1).getValue());
		System.out.println("Variable_3 : "+variables.get(2).getName()+"-"+variables.get(2).getValue());
		return variables;
	}
	public String getFullFileName()
	{
		return fileName;
	}
	public String getGNumber()
	{
		return googleNumber;
	}
	public String getMainJob()
	{
		return relatedMainUproc;
	}
	public String getCabrinJob()
	{
		return relatedCabrinUproc;
	}
	public void setFullFileName(String fname)
	{
		fileName=fname;
	}
	public void setGNumber(String gnumber)
	{
		googleNumber=gnumber;
	}
	public void setRelatedMainJob(String relatedMJ )
	{
		relatedMainUproc=relatedMJ;
	}

	public static String getGoogleNumber(String fullFilename)
	{// *.G0000V00.TRIGGER needs to be parsed
		fullFilename=fullFilename.replace(".TRIGGER","");
		int length= fullFilename.length();
		
		
		fullFilename=fullFilename.substring(length-8, length);
		return fullFilename.substring(3, 5);
		
	}
	private  void deriveUprocVariables() throws Exception
	{
		if(isTempUprocSet)
		{
		
			Vector<Variable> varia = new Vector<Variable>();
			varia.addAll(templateCabrinUproc.getVariables());

			if(varia.size()==3 && varia.get(1).getName().equals("COMMAND_PART2") 
						   		&& varia.get(2).getName().equals("MAIN_JOB_TRIGGER"))
			{	
	
			
				String command =varia.get(1).getValue();
				command=command.replace("-S00", "-S"+googleNumber);
				command=command.replace("<FILE>", fileName);
				varia.get(1).setValue(command);	
			
				
				String main_job_trigger = varia.get(2).getValue();
				main_job_trigger=main_job_trigger.replace("<JOB>", relatedMainUproc);
				varia.get(2).setValue(main_job_trigger);
				
				
		
			}
	
			variables= varia;
			
			if(!duapi.doesUprocExist(relatedCabrinUproc))
			{
				
					 UprocId newDuplicatedUprocId = new UprocId(relatedCabrinUproc, "000");
					 newDuplicatedUprocId.setId(relatedCabrinUproc);  
				        
				       
					templateCabrinUproc.duplicate(newDuplicatedUprocId, "CABRIN");
					//duapi.duplicateUproc(templateCabrinUproc.getName(), relatedCabrinUproc,"CABRIN");
					System.out.println("Created uproc <"+relatedCabrinUproc+">");						
			}
			
			
			
		}
		
	}
	

	public static void readReferenceFile(String fileName) throws IOException
	{
		 @SuppressWarnings("resource")
		CSVReader reader = new CSVReader(new FileReader(fileName),',', '\"', '\0');
			
			String [] line;			

			//parse lines
			while ((line = reader.readNext()) != null) 
		    {	    	
				
		        if(line.length>1)
		        {
		        	String key=line[0];
		        	String value=line[1];
		        
		        	
		        	if(!reference.containsKey(key))
		        	{
		        		ArrayList<String> placer = new ArrayList<String>();
		        		placer.add(value);
		        		reference.put(key, placer);
		        	}
		        	else
		        	{
		        		if(!reference.get(key).contains(value))
		        		{
		        			reference.get(key).add(value);
		        		}
		        	}
		        		
		        }
		    }	
	}

	public static boolean hasEntry(String filename)
	{
		for(String key:reference.keySet())
		{
			if(filename.contains(key))
			{
				return true;
			}
		}
		
		return false;
	}
	public static void fireAwayCabrinProcesses(CabrinProcess cabproc,String submissionUser,String muName) throws Exception {
		
		
		if(!CabrinProcess.publicboard.containsKey(cabproc.getCabrinJob()))
			{
					Queue<CabrinProcess> myQ=new LinkedList<CabrinProcess>();
					myQ.add(cabproc);
					CabrinProcess.publicboard.put(cabproc.getCabrinJob(),myQ);
				
			}
		else if(CabrinProcess.publicboard.get(cabproc.getCabrinJob()).isEmpty())
		{
			CabrinProcess.publicboard.get(cabproc.getCabrinJob()).add(cabproc);
		}

		
		if(duapi.getExecutionList(cabproc.getCabrinJob(),array).size()==0)
		{					

			cabproc=CabrinProcess.publicboard.get(cabproc.getCabrinJob()).poll();
			
				
			Uproc cabupr= duapi.getUproc(cabproc.getCabrinJob());
			cabupr.setVariables(cabproc.getCabrinUprocVariables());
			cabupr.update();
			
			System.out.println(" Update uproc :"+cabupr.getName());
			System.out.println("Variable_1 : "+cabupr.getVariables().get(0).getName()+"-"+cabupr.getVariables().get(0).getValue());
			System.out.println("Variable_2 : "+cabupr.getVariables().get(1).getName()+"-"+cabupr.getVariables().get(1).getValue());
			System.out.println("Variable_3 : "+cabupr.getVariables().get(2).getName()+"-"+cabupr.getVariables().get(2).getValue());

			System.out.println("Updated uproc <"+cabproc.getCabrinJob()+">");						

		   
		
		    Date launchDateTime = new Date();
		    
			
			List<String> mus = duapi.getMus(); 
			List<String> users = duapi.getUsers();
			if (mus.size()==0)
				throw new Exception("No MU found");
			if (users.size()==0)
				throw new Exception("No user found");
			String aMu = mus.get(0);
			String aUser = users.get(0);
			if (users.contains(submissionUser))
				aUser = submissionUser;
			
			if(mus.contains(muName))
				aMu=muName;
			
					
			Launch l = new Launch(duapi.getContext(),LaunchId.createWithName("", "", cabproc.getCabrinJob(), "000", aMu, null));
			{
	    		l.setImpl(new OwlsLaunchImpl());
	    		l.getIdentifier().setSyntaxRules(OwlsSyntaxRules.getInstance());
			}
	        l.setBasedOnTask(false);
	        l.setBeginDate(launchDateTime);
	        Date endDate = new Date();
	        endDate.setTime(launchDateTime.getTime() + 100000000);
	        l.setEndDate(endDate);
	        l.setProcessingDate((new SimpleDateFormat("yyyyMMdd")).format(launchDateTime));
	        l.setUserName(aUser);
	        l.setQueue("SYS_BATCH");
	        l.setPriority("100");
	        l.setPrinter("IMPR");

	        l.create();	

			System.out.println("Launch created for <"+cabproc.getCabrinJob()+"> with nmLanc ="+l.getNumlanc());
		}
}
	        //return l.getIdentifier(


	public static String getGenericKeyFileName(String filename)
	{//get filename without googlenumber, that is stored as a key in the reference table
		for(String key:reference.keySet())
		{
			if(filename.contains(key))
			{
				return key;
			}
		}
		
		return null;
	}
	
	public static HashMap<String,ArrayList<String>> getReferenceTable ()
	{//get filename without googlenumber, that is stored as a key in the reference table
		return reference;
	}
	
	
	
	
	
	
	
	
	
}
