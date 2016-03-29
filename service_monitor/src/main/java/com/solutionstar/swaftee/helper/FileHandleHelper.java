package com.solutionstar.swaftee.helper;

import java.io.File;

public class FileHandleHelper
{
	
	public static String servletContextPath = "";
	
	public File getFilesUnderWebInf(String filename)
	{
		return new File(servletContextPath + "\\WEB-INF\\" + filename);
	}

	public String getDBConfigFilePath()
	{
		String filename = System.getProperty("ENVIRONMENT", "local") + ".property";
		return (servletContextPath + "\\WEB-INF\\dbconf\\" + filename);
	}
}
