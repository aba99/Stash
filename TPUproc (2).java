package atco.tools;

import java.io.PrintStream;

import com.orsyp.api.uproc.Uproc;

public class TPUproc {
	
	
	private String jobId;
	private String MFfreq;
	private Uproc uproc ;
	
	
	public TPUproc(String ji,String mf,Uproc u)
	{
		MFfreq = mf;
		jobId=ji;
		uproc=u;
	}
	public String getJobId()
	{
		return jobId;
	}
	public Uproc getUprocObject()
	{
		return uproc;
	}
	public String getMFFreq()
	{
		return MFfreq;
	}
	public void print(PrintStream ps)
	{
		ps.println(jobId+" - "+uproc.getName()+" - "+MFfreq);
	}
	
}
