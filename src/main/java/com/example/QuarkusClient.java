package com.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/")
public interface QuarkusClient {

    @GET
    @Path("{path:.*}")
    Response fetchContent(@PathParam("path") String path);
}
