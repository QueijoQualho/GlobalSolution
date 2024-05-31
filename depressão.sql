
create table T_GSBH_usuarios (
    id_usuario INT GENERATED as IDENTITY primary key,
    nm_usuario VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

CREATE TABLE T_GSBH_endereco (
    id_endereco INT GENERATED as IDENTITY primary key,
    cep VARCHAR(20) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    complemento VARCHAR(255) NOT NULL,
    bairro VARCHAR(255) NOT NULL,
    localidade VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    ibge VARCHAR(10) NOT NULL,
    gia VARCHAR(10) NOT NULL,
    ddd VARCHAR(4) NOT NULL,
    siafi VARCHAR(10) NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES T_GSBH_usuarios(id_usuario) ON DELETE CASCADE
);

-- Inserindo dados na tabela T_GSBH_usuarios
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Maria Oliveira', 'maria.oliveira@example.com', 'senha123', '987654321');
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Carlos Pereira', 'carlos.pereira@example.com', 'senha123', '555666777');
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Ana Souza', 'ana.souza@example.com', 'senha123', '444555666');
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Paulo Lima', 'paulo.lima@example.com', 'senha123', '333444555');

-- Inserindo dados na tabela T_GSBH_endereco
INSERT INTO T_GSBH_endereco (cep, logradouro, complemento, bairro, localidade, uf, ibge, gia, ddd, siafi, id_usuario) VALUES ('23456789', 'Rua B', 'Apto 2', 'Centro', 'Rio de Janeiro', 'RJ', '2345678', '1002', '21', '2002', 2);
INSERT INTO T_GSBH_endereco (cep, logradouro, complemento, bairro, localidade, uf, ibge, gia, ddd, siafi, id_usuario) VALUES ('34567890', 'Rua C', 'Apto 3', 'Centro', 'Belo Horizonte', 'MG', '3456789', '1003', '31', '2003', 3);
INSERT INTO T_GSBH_endereco (cep, logradouro, complemento, bairro, localidade, uf, ibge, gia, ddd, siafi, id_usuario) VALUES ('45678901', 'Rua D', 'Apto 4', 'Centro', 'Porto Alegre', 'RS', '4567890', '1004', '51', '2004', 4);
INSERT INTO T_GSBH_endereco (cep, logradouro, complemento, bairro, localidade, uf, ibge, gia, ddd, siafi, id_usuario) VALUES ('56789012', 'Rua E', 'Apto 5', 'Centro', 'Curitiba', 'PR', '5678901', '1005', '41', '2005', 5);


