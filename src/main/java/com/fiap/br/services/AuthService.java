package com.fiap.br.services;

import com.fiap.br.models.AuthDTO;
import com.fiap.br.models.Usuario;

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
}