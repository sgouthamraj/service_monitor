package com.solutionstar.swaftee.entity;

import java.util.HashMap;
import java.util.List;

public class ProductServices
{
	public String product = null;
	public String environment = null;
	public List<HashMap<String, String>> services = null;
	public List<String> expectedServices = null;
	
	public ProductServices(String product, String environment, List<HashMap<String, String>> services, List<String> expectedServices)
	{
		this.product = product;
		this.environment = environment;
		this.services = services;
		this.expectedServices = expectedServices;
	}
	
	public String toString()
	{
		return "Environment: " + environment + "\nProduct: " + product + "\nServices: " + services.toString() + "\nExpected Services: " + expectedServices.toString(); 
	}
}