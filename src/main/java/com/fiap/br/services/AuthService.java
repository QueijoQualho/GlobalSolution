package com.fiap.br.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

import com.fiap.br.models.AuthDTO;
import com.fiap.br.models.Usuario;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class AuthService {
    private UsuarioService usuarioService;

    public AuthService() {
        this.usuarioService = new UsuarioService();
    }

    public Boolean login(AuthDTO authDto) {
        Usuario user = usuarioService.findUserByEmail(authDto.getEmail());

        if (user == null) {
            return false;
        }

        if (!user.getSenha().equals(authDto.getSenha())) {
            return false;
        }

        return true;
    }

    public boolean signup(Usuario usuario) {
        return usuarioService.saveUser(usuario);
    }

    public boolean verifyRecaptcha(String recaptchaToken) {
        try {
            URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String postParams = "secret=" + getSecretKey() + "&response=" + recaptchaToken;
            OutputStream outStream = conn.getOutputStream();
            outStream.write(postParams.getBytes());

            Scanner inStream = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (inStream.hasNext()) {
                response.append(inStream.nextLine());
            }
            inStream.close();

            JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();

            return jsonObject.getBoolean("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getSecretKey(){
        String secretKey;
        try (InputStream inputStream = AuthService.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            secretKey = properties.getProperty("secret_key_recaptcha");
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar as propriedades do banco de dados", e);
        }
        return secretKey;
    }
    
}