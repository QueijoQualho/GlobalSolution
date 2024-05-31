package com.fiap.br.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.fiap.br.util.annotations.CollumnName;
import com.fiap.br.util.annotations.JoinTable;
import com.fiap.br.util.annotations.TableName;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@TableName("T_GSBH_usuarios")
public class Usuario {

    @CollumnName("id_usuario")
    private int id;

    @CollumnName("nm_usuario")
    @NotNull(message = "O nome é obrigatório")
    @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
    private String nome;

    @CollumnName("email")
    @Email(regexp = ".+[@].+[\\.].+")
    @NotNull(message = "O email é obrigatório")
    private String email;

    @CollumnName("senha")
    @NotNull(message = "O senha é obrigatório")
    private String senha;

    @CollumnName("telefone")
    @NotNull(message = "O telefone é obrigatório")
    private String telefone;

    @Valid
    @JoinTable(value = Endereco.class)
    private List<Endereco> enderecos = new ArrayList<>();

}
