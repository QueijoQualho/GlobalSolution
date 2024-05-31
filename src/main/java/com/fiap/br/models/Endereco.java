package com.fiap.br.models;

import com.fiap.br.util.annotations.CollumnName;
import com.fiap.br.util.annotations.JoinedTableFk;
import com.fiap.br.util.annotations.TableName;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@TableName("T_GSBH_endereco")
public class Endereco {
    @CollumnName("id_endereco")
    int id;

    @CollumnName("cep")
    @NotNull(message = "O cep é obrigatório")
    String cep;

    @CollumnName("logradouro")
    @NotNull(message = "O logradouro é obrigatório")
    String logradouro;

    @CollumnName("complemento")
    @NotNull(message = "O complemento é obrigatório")
    String complemento;

    @CollumnName("bairro")
    @NotNull(message = "O bairro é obrigatório")
    String bairro;

    @CollumnName("localidade")
    @NotNull(message = "O localidade é obrigatório")
    String localidade;

    @CollumnName("uf")
    @NotNull(message = "O uf é obrigatório")
    String uf;

    @CollumnName("ibge")
    @NotNull(message = "O ibge é obrigatório")
    String ibge;

    @CollumnName("gia")
    @NotNull(message = "O gia é obrigatório")
    String gia;

    @CollumnName("ddd")
    @NotNull(message = "O ddd é obrigatório")
    String ddd;

    @CollumnName("siafi")
    @NotNull(message = "O siafi é obrigatório")
    String siafi;

    @CollumnName("id_usuario")
    @NotNull()
    @JoinedTableFk()
    int idUsuario;

}
