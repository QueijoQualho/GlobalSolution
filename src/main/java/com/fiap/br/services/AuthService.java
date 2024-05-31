package com.fiap.br.services;

import com.fiap.br.models.AuthDTO;
import com.fiap.br.models.Usuario;
import com.fiap.br.repositories.UsuarioRepository;

public class AuthService {
    private UsuarioRepository usuarioRepository;

    public AuthService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Boolean login(AuthDTO authDto) {
        Usuario user = usuarioRepository.findByEmail(authDto.getEmail());

        if (user == null) {
            return false;
        }

        if (!user.getSenha().equals(authDto.getSenha())) {
            return false;
        }

        return true;
    }

    public void signup(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}