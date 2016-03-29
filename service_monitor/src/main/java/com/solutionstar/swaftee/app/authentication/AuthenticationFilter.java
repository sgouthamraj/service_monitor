package com.solutionstar.swaftee.app.authentication;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter
{

	@Override
	public void filter(ContainerRequestContext requestCtx) throws IOException
	{
		

	}

}
