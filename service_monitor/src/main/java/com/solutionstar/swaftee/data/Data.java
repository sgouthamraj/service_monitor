package com.solutionstar.swaftee.data;

import com.solutionstar.swaftee.dbconnections.ConnectDB;
import com.solutionstar.swaftee.helper.FileHandleHelper;

public class Data
{
	ConnectDB dbh = null;
	
	public ConnectDB getDBH() throws Exception
	{
		if(dbh == null)
		{
			FileHandleHelper fhd = new FileHandleHelper();
			String dbConfigFilePath = fhd.getDBConfigFilePath();
			dbh = new ConnectDB(dbConfigFilePath);
			dbh.establishConnection();
		}
		return dbh;
	}
	
	public void init() throws Exception
	{
		getDBH();
	}
}
