package com.solutionstar.swaftee.app.authentication;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.Cookie;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import com.solutionstar.swaftee.constants.Constants;

@Provider
@PreMatching
public class CookieInjection implements ContainerResponseFilter, ContainerRequestFilter
{

	@Context
	private HttpServletRequest request;

	@Override
	public void filter(ContainerRequestContext requestCtx, ContainerResponseContext responseCtx) throws IOException
	{
		String path = requestCtx.getUriInfo().getPath();
		System.out.println("path requested: " +path);
		if (path.startsWith("/logout"))
		{
			responseCtx.getCookies().put("apiClientId", getDefaultCookie());
		}
	}

	public NewCookie getDefaultCookie()
	{
		NewCookie cookie = new NewCookie("apiClientId", Constants.API_CLIENT_ID);
		return cookie;
	}

	@Override
	public void filter(ContainerRequestContext requestCtx) throws IOException
	{
		
		System.out.println("Inside filter");
		if (requestCtx.getRequest().getMethod().equals("OPTIONS"))
		{
			requestCtx.abortWith(Response.status(Response.Status.OK).build());
			return;
		}

		String path = requestCtx.getUriInfo().getPath();
		System.out.println("Filtering request path: " + path);

		Map<String, Cookie> cookies = requestCtx.getCookies();

		if (cookies.containsKey("apiClientId"))
		{
			Cookie apiCookie = cookies.get("apiClientId");

			if (!KeyStore.getInstance().isValidToken(apiCookie.getValue()))
			{
				requestCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
				return;
			}
		}
		else
		{
			requestCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}
	}

}
