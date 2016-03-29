package com.solutionstar.swaftee.webapp.endpoint;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.solutionstar.swaftee.app.authentication.KeyStore;
import com.solutionstar.swaftee.app.servicedetails.ServiceDetails;
import com.solutionstar.swaftee.entity.ProductServices;
import com.solutionstar.swaftee.entity.ServiceLog;

@Path("/")
public class ServiceMonitorDashboard
{	
	@CookieParam("apiClientId")
	public String token;
	
	@GET
	@Path("/getServiceDetails")
	@Produces("application/json")
	public List<ProductServices> getServiceDetails() throws Exception
	{
		String user = getUser();
		ServiceDetails sd = new ServiceDetails();
		return sd.getServiceDetails(user);
	}

	private String getUser()
	{
		return KeyStore.getInstance().getUser(token);
	}

	@GET
	@Path("/getServiceDetail/{product}/{environment}/{service}")
	@Produces("application/json")
	public ServiceLog getServiceLog(@PathParam("product") String product, @PathParam("environment") String environment,
			@PathParam("service") String service) throws Exception
	{
		System.out.println("Inside get service log");
		System.out.println("Product: " + product);
		System.out.println("Environment: " + environment);
		System.out.println("Service: " + service);
		ServiceDetails sd = new ServiceDetails();
		return sd.getServiceLog(product, environment, service);
	}

	@POST
	@Path("/register")
	@Produces("application/json")
	public HashMap<String, String> register(@FormParam("username") String username,
			@FormParam("password") String password) throws Exception
	{
		ServiceDetails sd = new ServiceDetails();
		return sd.register(username, password);
	}

	@POST
	@Path("/login")
	@Produces("application/json")
	public HashMap<String, String> login(@FormParam("username") String username, @FormParam("password") String password)
			throws Exception
	{
		ServiceDetails sd = new ServiceDetails();
		return sd.login(username, password);
	}

	@GET
	@Path("/getServiceStatus/{product}/{environment}/{service}")
	@Produces("application/json")
	public String getServiceStatus(@PathParam("product") String product, @PathParam("environment") String environment,
			@PathParam("service") String service) throws Exception
	{
		System.out.println("Inside get service log");
		System.out.println("Product: " + product);
		System.out.println("Environment: " + environment);
		System.out.println("Service: " + service);
		String user = getUser();
		ServiceDetails sd = new ServiceDetails();
		return sd.getServiceStatus(product, environment, service, user);
	}
	
	@POST
	@Path("/logout")
	@Produces("application/json")
	public HashMap<String, String> logout() throws Exception
	{
		String username = getUser();
		ServiceDetails sd = new ServiceDetails();
		return sd.logout(token, username);
	}
	
	@GET
	@Path("/userpreferences")
	@Produces("application/json")
	public List<String> getUserPreferences() throws Exception
	{
		String username = getUser();
		ServiceDetails sd = new ServiceDetails();
		return sd.getUserPreferences(username);
	}
	
	@PUT
	@Path("/userpreferences/{service}")
	@Produces("application/json")
	public void putUserPreferences(@PathParam("service") String service) throws Exception
	{
		String username = getUser();
		ServiceDetails sd = new ServiceDetails();
		sd.addUserPreference(username, service);
	}
	
	@DELETE
	@Path("/userpreferences/{service}")
	@Produces("application/json")
	public void deleteUserPreferences(@PathParam("service") String service) throws Exception
	{
		String username = getUser();
		ServiceDetails sd = new ServiceDetails();
		sd.deleteUserPreference(username, service);
	}
	
}
