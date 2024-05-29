package com.fiap.br.repositories;

import com.fiap.br.models.Usuario;
import com.fiap.br.services.QueryExecutor;

public class UsuarioRepository extends Repository<Usuario> {

    public UsuarioRepository(QueryExecutor queryExecutor) {
        super(queryExecutor);
    }

}
