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

    public Usuario findUserByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> findAllUsers() {
        return usuarioRepository.findAll(Usuario.class);
    }

    public boolean saveUser(Usuario usuario) {

        Usuario user = findUserByEmail(usuario.getEmail());

        if(user != null){
            return false;
        }

        usuarioRepository.save(usuario);
        return true;
    }

    public void updateUser(Usuario usuario, int id) {
        usuarioRepository.update(usuario, id);
    }

    public void deleteUser(int id) {
        usuarioRepository.delete(Usuario.class, id);
    }
}
