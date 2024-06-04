package com.fiap.br.controller;

import com.fiap.br.models.AuthDTO;
import com.fiap.br.models.Usuario;
import com.fiap.br.services.AuthService;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthController {
    private AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid AuthDTO authDTO) {
        if (!authService.verifyRecaptcha(authDTO.getRecaptchaToken())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("reCAPTCHA verification failed").build();
        }

        Boolean logado = authService.login(authDTO);

        if (!logado) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Usuário ou senha incorretos").build();
        }

        return Response.ok().entity("Login realizado com sucesso").build();
    }

    @POST
    @Path("signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signup(@Valid Usuario usuario) {
        Boolean emailExist = authService.signup(usuario);

        if (!emailExist) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Já existe um usuario com esse email").build();
        }
        
        return Response.status(Response.Status.CREATED).build();
    }
}
