package com.solutionstar.swaftee.constants;

public class FileLocationConstants
{
	public static final String FTP_BASE_LOCATION = "\\\\10.61.1.27\\SharedFolder\\Automation\\ServiceMonitoring\\";
	public static final String SERVICE_DETAILS_DATA = FTP_BASE_LOCATION + "servicelocations.xlsx";
	
	// L2 A2CEngine inbox
	public static final String A2CEngineInbox_L2 = "\\\\10.61.1.27\\SharedFolder\\cascade_apollo\\Test2\\Cascade\\Integration\\ApolloToCascade\\all\\inbox";

	// L1 SFTP A2CEngine inbox
	public static final String A2CEngineInbox_L1 = "/home/sftp-red-user03/qa/CascadeToApollo/assets/inbox/";

	// UAT SFTP A2CEngine inbox
	public static final String A2CEngineInbox_UAT = "/home/sftp-red-user03/uat/CascadeToApollo/assets/inbox/";

}