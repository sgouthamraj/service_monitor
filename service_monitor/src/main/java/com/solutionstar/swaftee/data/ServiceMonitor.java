package com.solutionstar.swaftee.data;

import java.util.HashMap;
import java.util.List;

import com.solutionstar.swaftee.entity.ProductServices;
import com.solutionstar.swaftee.entity.ServiceLog;

public class ServiceMonitor extends Data
{
	public ServiceMonitor() throws Exception
	{
		init();
	}

	public void storeServiceDetails(List<ProductServices> serviceDetails, String user) throws Exception
	{
		for (ProductServices ps : serviceDetails)
		{
			for (HashMap<String, String> map : ps.services)
			{
				dbh.executeQueryForScalar("select servicelog_entry('" + ps.product + "','" + ps.environment + "','"
						+ map.get("DisplayName") + "','" + map.get("Status") + "', '" + user + "')");
			}
		}
	}

	public ServiceLog getServiceLog(String product, String environment, String service, boolean useAdvancedLog)
			throws Exception
	{
		String sql = null;
		if (useAdvancedLog)
		{
			sql = "select * from servicelogadvanced_details('" + product + "', '" + environment + "', '" + service
					+ "')";
		}
		else
		{
			sql = "select * from servicelog_details('" + product + "', '" + environment + "', '" + service + "')";
		}
		List<HashMap<String, String>> logDetails = dbh.executeQueryForHash(sql);
		ServiceLog sl = new ServiceLog(useAdvancedLog, logDetails);
		return sl;
	}

	public boolean isUserValid(String username, String password) throws Exception
	{
		String sql = "select * from check_password('" + username + "', '" + password + "')";
		System.out.println(sql);
		String result = dbh.executeQueryForScalar(sql);
		return result.equalsIgnoreCase("t");
	}

	public void registerUser(String username, String password) throws Exception
	{
		String sql = "select * from users_entry('" + username + "', '" + password + "')";
		System.out.println(sql);
		dbh.executeQueryForScalar(sql);
	}

	public void storeAdvancedServiceDetail(String product, String environment, String service, String status,
			String user) throws Exception
	{
		dbh.executeQueryForScalar("select servicelogadvanced_entry('" + product + "','" + environment + "','" + service + "','"
				+ status + "', '" + user + "')");
	}
	
	public List<String> getPreferencesForUser(String user) throws Exception
	{
		return dbh.executeQueryResultAsList("select * from get_userpreferences('" + user +"')");
	}
	
	public void addUserPreference(String user, String service) throws Exception
	{
		dbh.executeQueryForScalar("select add_userpreference('" + user + "','" + service + "')");
	}
	
	public void deleteUserPreference(String user, String service) throws Exception
	{
		dbh.executeQueryForScalar("select delete_userpreference('" + user + "','" + service + "')");
	}

}
