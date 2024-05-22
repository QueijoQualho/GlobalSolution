package com.fiap.br.models;

import com.fiap.br.util.annotations.CollumnName;
import com.fiap.br.util.annotations.TableName;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@TableName("teste")
public class Test {
    @CollumnName("id")
    int id;

    @CollumnName("nome")
    @NotNull(message = "O nome é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    String nome;

    @CollumnName("valor")
    @NotNull(message = "O Valor é obrigatório")
    Double valor;

    @CollumnName("descricao")
    @NotNull(message = "O descricao é obrigatório")
    @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
    String descricao;
}
