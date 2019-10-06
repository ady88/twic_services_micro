package com.adrian.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.adrian.domain.ResultStatus;

@RequestScoped
@Path("/twic")
public class TwicTestService {

	@Inject
	@ConfigProperty(name = "application.username", defaultValue = "Adrian")
	private String name;

	@Inject
	private TwicRetrievalService twicService;

	/**
	 * Download the twic archive file with the given number and store this file in
	 * google cloud storage.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path(value = "/downloadAndStore")
	public Response downloadAndStore(@QueryParam(value = "twic_number") final int pgnNumber)
			throws FileNotFoundException, IOException {
		final ResultStatus status = twicService.retrieveAndStore(pgnNumber);
		return Response.ok(status.getMessage(), MediaType.TEXT_PLAIN).build();
	}
}