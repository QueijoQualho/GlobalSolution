package com.fiap.br.controller;

import com.fiap.br.models.ViaCepDTO;
import com.fiap.br.services.ViaCepService;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("viacep")
public class ViaCepController {
     private ViaCepService viaCepClient;

    public ViaCepController() {
        this.viaCepClient = new ViaCepService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUsuario(@Valid ViaCepDTO viaCepDTO) {
        Object response = viaCepClient.getEnderecoByCep(viaCepDTO.getCep());
        return Response.ok(response).build();
    }

}
