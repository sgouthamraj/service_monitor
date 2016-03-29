package com.solutionstar.swaftee.app;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.solutionstar.swaftee.app.authentication.AuthenticationFilter;
import com.solutionstar.swaftee.app.authentication.CookieInjection;
import com.solutionstar.swaftee.webapp.endpoint.ServiceMonitorDashboard;

public class ServiceMonitorApplication extends Application
{
	public Set<Class<?>> getClasses()
	{
		Set<Class<?>> myClassSet = new HashSet<Class<?>>();
		
		myClassSet.add(ServiceMonitorDashboard.class);
		myClassSet.add(CookieInjection.class);
		
		return myClassSet;
	}
}
