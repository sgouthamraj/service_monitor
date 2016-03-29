package com.solutionstar.swaftee.entity;

import java.util.HashMap;
import java.util.List;

public class ServiceLog
{
	public static final String FILES = "No. of Files";

	public static final String RECORDS = "No. of Records";
	
	public boolean hasAdvancedLog = false;
	public List<HashMap<String, String>> logDetails = null;
	public String yAxisName = null;
	public String xAxisName = "Time";
	
	public ServiceLog(boolean hasAdvancedLog, List<HashMap<String, String>> logDetails)
	{
		this.hasAdvancedLog = hasAdvancedLog;
		this.logDetails = logDetails;
	}
	
	public void setYAxisName(String name)
	{
		this.yAxisName = name;
	}
}
