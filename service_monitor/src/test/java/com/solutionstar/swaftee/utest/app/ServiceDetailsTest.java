package com.solutionstar.swaftee.utest.app;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.solutionstar.swaftee.app.servicedetails.ServiceDetails;
import com.solutionstar.swaftee.entity.ProductServices;
import com.solutionstar.swaftee.helper.FileHandleHelper;
import com.solutionstar.swaftee.utils.CommonUtils;

public class ServiceDetailsTest
{
	//@Test
	public void getServiceLocationsTest()
	{
		try
		{
			List<HashMap<String, String>> data = null;
			data = ServiceDetails.getServiceLocations();
			for(HashMap<String, String> map : data)
			{
				System.out.println(map.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//@Test
	public void getServiceDetails()
	{
		try
		{
			CommonUtils utils = new CommonUtils();
			FileHandleHelper.servletContextPath = utils.getCurrentWorkingDirectory() + "\\src\\main\\webapp";
			ServiceDetails sd = new ServiceDetails();
			List<ProductServices> data;
			data = sd.getServiceDetails("utest");
			for(ProductServices ps : data)
			{
				System.out.println(ps.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	@Test
	public void getA2CEngineAdvancedStatus()
	{
		try
		{
			CommonUtils utils = new CommonUtils();
			FileHandleHelper.servletContextPath = utils.getCurrentWorkingDirectory() + "\\src\\main\\webapp";
			ServiceDetails sd = new ServiceDetails();
			String status = sd.getServiceStatus("Cascade", "L2", "A2CEngine", "utest");
			System.out.println(status);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
