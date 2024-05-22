package com.fiap.br.repositories;

import com.fiap.br.models.Test;
import com.fiap.br.services.QueryExecutor;

public class TestRepository extends Repository<Test> {

    public TestRepository(QueryExecutor queryExecutor) {
        super(queryExecutor);
    }
    
}
