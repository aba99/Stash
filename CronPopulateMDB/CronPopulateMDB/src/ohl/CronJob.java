package ohl;

public class CronJob {

	private int JobCounter;
	private String ObjName;
	private String Script;
	private String Command;
	private String User;
	private String [] Minute;
	private boolean MinutePeriod = false;
	private String [] Hour;
	private String DayOfMonth;
	private String Month;
	private String DayOfWeek;
	private String Host;
	private String Login;
	private String OrgLine;

	public CronJob(int jc,String oname,String scri,String cmd,String usr,String[]min,boolean mperiod
			,String [] hr , String dmonth,String m,String dweek,String hst,String lgn,String oline)
	{
		JobCounter=jc;
		ObjName=oname;
		Script=scri;
		Command=cmd;
		User=usr;
		Minute=min;
		MinutePeriod = mperiod;
		Hour=hr;
		DayOfMonth=dmonth;
		Month=m;
		DayOfWeek=dweek;
		Host=hst;
		Login=lgn;
		OrgLine=oline;
		
	}
	
	public int getJobCounter()
	{
		return JobCounter;
	}
	public String getObjName()
	{
		return ObjName;
	}
	public String getScript()
	{
		return Script;
	}
	public String getCommand()
	{
		return Command;
	}
	public String getUser()
	{
		return User;
	}
	public String[] getMinute()
	{
		return Minute;
	}
	public boolean getMinutePeriod()
	{
		return MinutePeriod;
	}
	public String[] getHour()
	{
		return Hour;
	}
	public String getDayOfMonth()
	{
		return DayOfMonth;
	}
	public String getMonth()
	{
		return Month;
	}
	public String getDayOfWeek()
	{
		return DayOfWeek;
	}
	public String getHost()
	{
		return Host;
	}
	public String getLogin()
	{
		return Login;
	}
	public String getOrgLine()
	{
		return OrgLine;
	}
	
	
}



