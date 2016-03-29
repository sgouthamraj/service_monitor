package com.solutionstar.swaftee.app.servicedetails;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.joda.time.DateTime;

import com.solutionstar.swaftee.app.authentication.KeyStore;
import com.solutionstar.swaftee.constants.Constants;
import com.solutionstar.swaftee.constants.FileLocationConstants;
import com.solutionstar.swaftee.data.ServiceMonitor;
import com.solutionstar.swaftee.entity.ProductServices;
import com.solutionstar.swaftee.entity.ServiceLog;
import com.solutionstar.swaftee.helper.Worker;
import com.solutionstar.swaftee.utils.CommonUtils;
import com.solutionstar.swaftee.utils.dataarchive.DataArchive;
import com.solutionstar.swaftee.utils.dataarchive.XLSXDataArchive;

public class ServiceDetails
{
	ServiceMonitor sm = null;

	public ServiceDetails() throws Exception
	{
		sm = new ServiceMonitor();
	}

	public static List<HashMap<String, String>> data = null;

	public volatile static boolean semaphore = false;

	public volatile static boolean hasResults = false;

	public volatile static Date lastUpdatedTime = DateTime.now().toDate();

	public static List<String> advancedDetails = Arrays.asList(new String[] { "A2CEngine" });

	public static List<String> filesCheck = Arrays.asList(new String[] { "A2CEngine" });

	public static List<String> recordsCheck = Arrays.asList(new String[] {});

	public static List<HashMap<String, String>> getServiceLocations() throws Exception
	{
		if (data == null)
		{
			DataArchive da = new XLSXDataArchive();
			data = da.retrieveData(FileLocationConstants.SERVICE_DETAILS_DATA);
			System.out.println(data);
		}
		return data;
	}

	public List<ProductServices> getServiceDetails(String user) throws Exception
	{
		System.out.println("Has results: " + hasResults);
		System.out.println("Last modified: " + lastUpdatedTime.toString());
		if (hasResults && lastUpdatedTime.after(DateTime.now().minusSeconds(Constants.CACHE_INTERVAL).toDate()))
		{
			System.out.println("Results from cache");
			return Worker.getStoredResult();
		}
		else
		{
			Date enteredTime = DateTime.now().toDate();
			Date waitTill = DateTime.now().plusSeconds(Constants.SEMAPHORE_WAIT).toDate();
			while (semaphore && enteredTime.before(waitTill))
			{
				System.out.println("Waiting for semaphore");
				Thread.sleep(1000);
			}
			if (lastUpdatedTime.after(enteredTime))
			{
				System.out.println("Results from cache after waiting for semaphore");
				return Worker.getStoredResult();
			}
			if (enteredTime.after(waitTill))
			{
				throw new Exception("Semaphore not released in " + Constants.SEMAPHORE_WAIT + " Secs");
			}
			semaphore = true;
			List<HashMap<String, String>> serverLocations = getServiceLocations();
			Worker.clearStoredResult();
			Worker worker = new Worker(serverLocations);
			ForkJoinPool pool = new ForkJoinPool();
			pool.invoke(worker);
			hasResults = true;
			lastUpdatedTime = DateTime.now().toDate();
			semaphore = false;
			System.out.println("Results from newly captured data");
			persistCurrentDetails(user, Worker.lps);
			return Worker.lps;
		}

	}

	private void persistCurrentDetails(String user, List<ProductServices> lsp) throws Exception
	{
		sm.storeServiceDetails(lsp, user);
	}

	public ServiceLog getServiceLog(String product, String environment, String service) throws Exception
	{
		boolean useAdvancedLog = advancedDetails.contains(service);
		ServiceLog sl = sm.getServiceLog(product, environment, service, useAdvancedLog);
		if (useAdvancedLog)
		{
			if (filesCheck.contains(service))
			{
				sl.setYAxisName(ServiceLog.FILES);
			}
			if (recordsCheck.contains(service))
			{
				sl.setYAxisName(ServiceLog.RECORDS);
			}
		}
		return sl;
	}

	public HashMap<String, String> login(String username, String password) throws Exception
	{
		HashMap<String, String> result = new HashMap<String, String>();
		if (sm.isUserValid(username, password))
		{
			if (KeyStore.getInstance().getTokenForUser(username) != null)
			{
				result.put("status", "failed");
				result.put("errorMessage", "Multiple logins not allowed");
				return result;
			}
			else
			{
				result.put("status", "success");
				result.put("user", username.substring(0, username.indexOf('@')));
				result.put("token", KeyStore.getInstance().createToken(username));
			}
		}
		else
		{
			result.put("status", "failed");
			result.put("errorMessage", "Authentication failed. Invalid username and password");
		}

		return result;
	}

	public HashMap<String, String> register(String username, String password) throws Exception
	{
		HashMap<String, String> result = new HashMap<String, String>();
		sm.registerUser(username, password);
		result.put("status", "success");
		result.put("user", username.substring(0, username.indexOf('@')));
		result.put("token", KeyStore.getInstance().createToken(username));
		return result;
	}

	public String getServiceStatus(String product, String environment, String service, String user) throws Exception
	{
		if (service.equalsIgnoreCase("A2CEngine"))
		{
			return checkA2CEngineStatus(product, environment, service, user);
		}
		return "No advanced service status available";
	}

	public int checkNumberOfFilesUnderShare(String path)
	{
		return new File(path).listFiles().length;
	}
	
	public int checkNumberOfFilesUnderSftp(String hostname, int port, String username, String password, String sourceLocation)
	{
		CommonUtils utils = new CommonUtils();
		return utils.listFilesInSFTPLocation(hostname, port, username, password, sourceLocation).size();
	}

	public String checkA2CEngineStatus(String product, String environment, String service, String user) throws Exception
	{
		String status = "";
		if(environment.equalsIgnoreCase("L1"))
		{
			status = "" + checkNumberOfFilesUnderSftp(Constants.URL_SFTP, Constants.PORT_SFTP, Constants.USERNAME_SFTP, Constants.PASSWORD_SFTP, FileLocationConstants.A2CEngineInbox_L2);
		}
		else if(environment.equalsIgnoreCase("UAT"))
		{
			status = "" + checkNumberOfFilesUnderSftp(Constants.URL_SFTP, Constants.PORT_SFTP, Constants.USERNAME_SFTP, Constants.PASSWORD_SFTP, FileLocationConstants.A2CEngineInbox_UAT);
		}
		else if(environment.equalsIgnoreCase("L2"))
		{
			status = "" + checkNumberOfFilesUnderShare(FileLocationConstants.A2CEngineInbox_L2);
		}		
		sm.storeAdvancedServiceDetail(product, environment, service, status, user);
		return status;
	}

	public HashMap<String, String> logout(String token, String username) throws Exception
	{
		HashMap<String, String> result = new HashMap<String, String>();
		if (KeyStore.getInstance().isValidToken(token))
		{
			KeyStore.getInstance().destroyToken(token);
			result.put("status", "success");
		}
		else
		{
			result.put("status", "failed");
			result.put("errorMessage", "User has already logged out");
		}
		return result;
	}
	
	public void addUserPreference(String user, String service) throws Exception
	{
		sm.addUserPreference(user, service);
	}
	
	public void deleteUserPreference(String user, String service) throws Exception
	{
		sm.deleteUserPreference(user, service);
	}
	
	public List<String> getUserPreferences(String user) throws Exception
	{
		return sm.getPreferencesForUser(user);
	}

}
