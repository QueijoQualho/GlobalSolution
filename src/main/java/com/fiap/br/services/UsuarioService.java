package com.fiap.br.services;

import java.util.List;

import com.fiap.br.models.Usuario;
import com.fiap.br.repositories.UsuarioRepository;

public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuario findUserById(int id) {
        return usuarioRepository.findOne(Usuario.class, id);
    }

    public List<Usuario> findAllUsers() {
        return usuarioRepository.findAll(Usuario.class);
    }

    public void saveUser(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void updateUser(Usuario usuario, int id) {
        usuarioRepository.update(usuario, id);
    }

    public void deleteUser(int id) {
        usuarioRepository.delete(Usuario.class, id);
    }
}
