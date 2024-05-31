package com.fiap.br.repositories;

import java.util.List;
import java.util.Optional;

import com.fiap.br.models.Endereco;
import com.fiap.br.models.Usuario;
import com.fiap.br.models.enums.CRUDOperation;
import com.fiap.br.services.QueryExecutor;

public class UsuarioRepository extends Repository<Usuario> {

    private EnderecoRepository enderecoRepository;

    public UsuarioRepository() {
        super(new QueryExecutor());
        this.enderecoRepository = new EnderecoRepository(new QueryExecutor());
    }

    @Override
    public int save(Usuario usuario) {
        int usuarioId = super.save(usuario); 

        for (Endereco endereco : usuario.getEnderecos()) {
            endereco.setIdUsuario(usuarioId);
            enderecoRepository.save(endereco);
        }

        return 0; // null
    }

    public Usuario findByEmail(String email) {
        String sql = "SELECT u.*, e.* FROM T_GSBH_usuarios u join T_GSBH_endereco e on u.id_usuario = e.id_usuario WHERE email = ?";

        try {
            List<Usuario> result = queryExecutor.execute(Usuario.class, sql, new Object[] { email }, CRUDOperation.READ,
                    Optional.empty());
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            logError("Erro ao encontrar usuário por e-mail: " + e.getMessage());
        }
        return null;
    }
}
