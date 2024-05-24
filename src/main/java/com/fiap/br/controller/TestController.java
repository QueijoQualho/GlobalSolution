package com.fiap.br.controller;

import java.util.List;

import com.fiap.br.models.Test;
import com.fiap.br.repositories.TestRepository;
import com.fiap.br.services.QueryExecutor;
import com.fiap.br.services.TestService;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("tests")
public class TestController {
    private TestService testeService;

    public TestController() {
        testeService = new TestService(new TestRepository(new QueryExecutor()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTests() {
        List<Test> tests = testeService.findAllTests();
        return Response.ok(tests).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTestById(@PathParam("id") int id) {
        Test test = testeService.findTestById(id);
        return Response.ok(test).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTest(@Valid Test test) {
        testeService.saveTest(test);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTest(@PathParam("id") int id, @Valid Test test) {
        testeService.updateTest(test, id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTest(@PathParam("id") int id) {
        testeService.deleteTest(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
