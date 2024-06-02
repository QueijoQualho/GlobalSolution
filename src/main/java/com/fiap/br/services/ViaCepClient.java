package com.fiap.br.services;

import org.json.JSONObject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class ViaCepClient {
    private static final String BASE_URL = "https://viacep.com.br/ws/";

    public String getEnderecoByCep(String cep) {
        cep = cep.replace("-", "");
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(BASE_URL).path(cep).path("/json/");
        Response response = target.request(MediaType.APPLICATION_JSON).get();

        String jsonResponse;
        if (response.getStatus() == 200) {
            jsonResponse = response.readEntity(String.class);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject resultJson = new JSONObject();

            resultJson.put("rua", jsonObject.optString("logradouro", "N/A"));
            resultJson.put("cidade", jsonObject.optString("localidade", "N/A"));
            resultJson.put("bairro", jsonObject.optString("bairro", "N/A"));
            resultJson.put("estado", jsonObject.optString("uf", "N/A"));

            jsonResponse = resultJson.toString();
        } else {
            jsonResponse = new JSONObject()
                    .put("erro", "Erro ao fazer a requisição")
                    .put("status", response.getStatus())
                    .toString();
        }

        client.close();
        return jsonResponse;
    }
}
