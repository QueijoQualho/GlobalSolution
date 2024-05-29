package com.fiap.br.repositories;

import com.fiap.br.models.Endereco;
import com.fiap.br.services.QueryExecutor;

public class EnderecoRepository extends Repository<Endereco> {

    public EnderecoRepository(QueryExecutor queryExecutor) {
        super(queryExecutor);
    }

}
