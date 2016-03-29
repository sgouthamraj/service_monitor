package com.solutionstar.swaftee.app.authentication;

import java.util.HashMap;
import java.util.UUID;

import org.jboss.resteasy.spi.UnauthorizedException;

import com.solutionstar.swaftee.constants.Constants;

public class KeyStore
{
	public HashMap<String, String> validatedUsers = new HashMap<String, String>();
	
	private static KeyStore ks = null; 
	
	private KeyStore()
	{
		validatedUsers.put(Constants.API_CLIENT_ID, "local");
	}
	
	public String getUser(String token) throws UnauthorizedException
	{
		if(validatedUsers.containsKey(token))
		{
			return validatedUsers.get(token);
		}
		throw new UnauthorizedException();
	}
	
	public static KeyStore getInstance()
	{
		if(ks == null)
		{
			ks = new KeyStore();
		}
		return ks;
	}
	
	public String createToken(String user)
	{
		String token = UUID.randomUUID().toString();
		validatedUsers.put(token, user);
		return token;
	}
	
	public void destroyToken(String token)
	{
		validatedUsers.remove(token);
	}
	
	public boolean isValidToken(String token)
	{
		System.out.println(token);
		System.out.println(validatedUsers);
		if(validatedUsers.containsKey(token))
		{
			return true;
		}
		return false;
	}
	
	public String getTokenForUser(String user)
	{
		for(String token : validatedUsers.keySet())
		{
			if(validatedUsers.get(token).equals(user))
			{
				return token;
			}
		}
		
		return null;
	}
}
