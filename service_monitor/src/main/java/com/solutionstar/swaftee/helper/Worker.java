package com.solutionstar.swaftee.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.apache.http.client.utils.CloneUtils;
import org.joda.time.DateTime;

import com.solutionstar.swaftee.entity.ProductServices;

public class Worker extends RecursiveAction
{
	public static List<ProductServices> lps = Collections.synchronizedList(new ArrayList<ProductServices>());

	List<HashMap<String, String>> inputData = null;

	public Worker(List<HashMap<String, String>> inputData)
	{
		this.inputData = inputData;
	}

	@Override
	protected void compute()
	{
		if (inputData.size() == 1)
		{
			computeDirectly(inputData.get(0));
			return;
		}
		int split = inputData.size() / 2;

		invokeAll(new Worker(inputData.subList(0, split)), new Worker(inputData.subList(split, inputData.size())));
	}

	protected void computeDirectly(HashMap<String, String> data)
	{
		StringBuilder command1 = new StringBuilder();
		StringBuilder command2 = new StringBuilder();
		command1.append("net use \\\\");
		command1.append(data.get("MachineIP"));
		command1.append(" /user:");
		command1.append(data.get("UserName"));
		command1.append(" ");
		command1.append(data.get("Password"));
		command1.append(" ; ");
		command2.append("gsv -ComputerName ");
		command2.append(data.get("MachineIP"));
		command2.append(" -name ");
		command2.append(data.get("Services"));
		command2.append(" | format-list -property Name, DisplayName, Status ;");

		// String commandFileName = System.getProperty("java.io.tmpdir") +
		// data.get("Product") + "-" + data.get("Environment") + "-Command.ps1";
		String outputFileName = System.getProperty("java.io.tmpdir") + data.get("Product") + "-"
				+ data.get("Environment") + "-Output.txt";

		// command1.append(fileName + "\"");

		List<HashMap<String, String>> services = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> tempResult = new HashMap<String, String>();

		try
		{
			// System.out.println("Start Time: " + DateTime.now().toString());
			// BufferedWriter bw = new BufferedWriter(new FileWriter(new
			// File(commandFileName)));
			// bw.write(command1.toString());
			// bw.write("\n");
			// bw.write(command2.toString());
			// bw.write("\n");
			// bw.write("exit;");
			// bw.close();
			// Runtime runtime = Runtime.getRuntime();
			// Process proc = runtime.exec("powershell \"" + commandFileName +
			// "\"");
			// proc.waitFor();
			// proc.getOutputStream().close();
			// System.out.println("Completed Time: " +
			// DateTime.now().toString());
			// InputStream is = proc.getInputStream();
			// InputStreamReader isr = new InputStreamReader(is);
			// BufferedReader reader = new BufferedReader(isr);
			// String line;
			// while ((line = reader.readLine()) != null)
			// {
			// System.out.println(line);
			// if (line.contains(":"))
			// {
			// String[] strArray = line.split(":");
			// tempResult.put(strArray[0].trim(), strArray[1].trim());
			// }
			// else
			// {
			// if (tempResult.size() > 0)
			// {
			// HashMap<String, String> resultClone = (HashMap<String, String>)
			// CloneUtils.clone(tempResult);
			// services.add(resultClone);
			// }
			// tempResult.clear();
			// }
			// }
			// reader.close();

			System.out.println("Execute command: " + command1.toString() + " Time: " + DateTime.now().toString());
			String[] commandList = { "powershell.exe", "-Command", command1.toString() + command2.toString() };
			File outputTo = new File(outputFileName);
			ProcessBuilder pb = new ProcessBuilder(commandList);
			pb.redirectOutput(outputTo);
			Process p = pb.start();
			WatchDog wd = new WatchDog(p);
			wd.start();
			wd.join(120 * 1000);
			if (wd.exit != null)
			{
				System.out.println("Executed command successfully. Time: " + DateTime.now().toString());
				BufferedReader br = new BufferedReader(new FileReader(new File(outputFileName)));
				String line = br.readLine();
				while (line != null)
				{
					System.out.println(line);
					if (line.contains(":"))
					{
						String[] strArray = line.split(":");
						tempResult.put(strArray[0].trim(), strArray[1].trim());
					}
					else
					{
						if (tempResult.size() > 0)
						{
							HashMap<String, String> resultClone = (HashMap<String, String>) CloneUtils
									.clone(tempResult);
							services.add(resultClone);
						}
						tempResult.clear();
					}
					line = br.readLine();
				}
				br.close();
				System.out.println("File read success " + outputFileName);
			}
			else
			{
				System.out.println("Timeout Exception Occured for the product: " + data.get("Product")
						+ " for the environment: " + data.get("Environment"));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		List<String> expectedServices = new ArrayList<String>();
		for(String service : data.get("Services").split(","))
		{
			expectedServices.add(service.trim());
		}
		ProductServices ps = new ProductServices(data.get("Product"), data.get("Environment"), services, expectedServices);
		findMissingServices(ps);
		lps.add(ps);
	}

	private void findMissingServices(ProductServices ps)
	{
		for(String serviceName : ps.expectedServices)
		{
			boolean flag = false;
			for(HashMap<String, String> serviceDetail : ps.services)
			{
				if(serviceDetail.get("Name").equals(serviceName))
				{
					flag = true;
					break;
				}
			}
			if(!flag)
			{
				HashMap<String, String> missingService = new HashMap<String, String>();
				missingService.put("Name", serviceName);
				missingService.put("DisplayName", serviceName);
				missingService.put("Status", "Missing");
				ps.services.add(missingService);
			}
		}
	}

	public synchronized static void clearStoredResult()
	{
		lps.clear();
	}

	public synchronized static List<ProductServices> getStoredResult()
	{
		return lps;
	}

}

class WatchDog extends Thread
{
	private final Process process;
	public Integer exit;

	WatchDog(Process process)
	{
		this.process = process;
	}

	public void run()
	{
		try
		{
			exit = process.waitFor();
		}
		catch (InterruptedException ignore)
		{
			return;
		}
	}
}
