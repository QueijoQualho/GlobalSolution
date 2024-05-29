package com.fiap.br.controller;

import java.util.List;

import com.fiap.br.models.Usuario;
import com.fiap.br.repositories.UsuarioRepository;
import com.fiap.br.services.QueryExecutor;
import com.fiap.br.services.UsuarioService;

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

@Path("usuarios")
public class UsuarioController {
    private UsuarioService usuarioService;

    public UsuarioController() {
        usuarioService = new UsuarioService(new UsuarioRepository(new QueryExecutor()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarios() {
        List<Usuario> usuarios = usuarioService.findAllUsers();
        return Response.ok(usuarios).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarioById(@PathParam("id") int id) {
        Usuario usuario = usuarioService.findUserById(id);
        return Response.ok(usuario).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUsuario(@Valid Usuario usuario) {
        usuarioService.saveUser(usuario);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUsuario(@PathParam("id") int id, @Valid Usuario usuario) {
        usuarioService.updateUser(usuario, id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUsuario(@PathParam("id") int id) {
        usuarioService.deleteUser(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
