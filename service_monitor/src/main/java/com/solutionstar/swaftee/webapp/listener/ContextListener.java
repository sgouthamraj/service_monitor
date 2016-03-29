package com.solutionstar.swaftee.webapp.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.solutionstar.swaftee.app.servicedetails.ServiceDetails;
import com.solutionstar.swaftee.helper.FileHandleHelper;

public class ContextListener implements ServletContextListener
{

	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
	}

	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		FileHandleHelper.servletContextPath = event.getServletContext().getRealPath(".");
		System.out.println(FileHandleHelper.servletContextPath);
		try
		{
			ServiceDetails sd = new ServiceDetails();
			sd.getServiceDetails("server_startup");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
