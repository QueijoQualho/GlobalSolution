-- tabelas
create table T_GSBH_usuarios (
    id_usuario INT GENERATED as IDENTITY primary key,
    nm_usuario VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    created_at DATE DEFAULT SYSDATE
);

CREATE TABLE T_GSBH_endereco (
    id_endereco INT GENERATED as IDENTITY,
    cep VARCHAR(20) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    nr_endereco VARCHAR(50) not null,
    bairro VARCHAR(255) NOT NULL,
    localidade VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    id_usuario INT NOT NULL,
    primary key(id_endereco, id_usuario),
    FOREIGN KEY (id_usuario) REFERENCES T_GSBH_usuarios(id_usuario) ON DELETE CASCADE
);

-- trigger
CREATE OR REPLACE TRIGGER trg_aplicar_mascara_cep
BEFORE INSERT OR UPDATE ON T_GSBH_endereco
FOR EACH ROW
BEGIN
    IF :NEW.cep IS NOT NULL THEN
        :NEW.cep := REGEXP_REPLACE(:NEW.cep, '(\d{5})(\d{3})', '\1-\2');
    END IF;
END;


-- Inserindo dados na tabela T_GSBH_usuarios
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Maria Oliveira', 'maria.oliveira@example.com', 'senha123', '987654321');
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Carlos Pereira', 'carlos.pereira@example.com', 'senha123', '555666777');
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Ana Souza', 'ana.souza@example.com', 'senha123', '444555666');
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('Paulo Lima', 'paulo.lima@example.com', 'senha123', '333444555');
INSERT INTO T_GSBH_usuarios (nm_usuario, email, senha, telefone) VALUES ('admin', 'admin@root.com', 'senha123', '333444555');

-- Inserindo dados na tabela T_GSBH_endereco
INSERT INTO T_GSBH_endereco (cep, logradouro, nr_endereco, bairro, localidade, uf, id_usuario) VALUES ('23456789', 'Rua B', '2', 'Centro', 'Rio de Janeiro', 'RJ', 2);
INSERT INTO T_GSBH_endereco (cep, logradouro, nr_endereco, bairro, localidade, uf, id_usuario) VALUES ('34567890', 'test', '323', 'Centro', 'Belo Horizonte', 'MG', 3);
INSERT INTO T_GSBH_endereco (cep, logradouro, nr_endereco, bairro, localidade, uf, id_usuario) VALUES ('45678901', 'Rua D', '434', 'Centro', 'Porto Alegre', 'RS', 4);
INSERT INTO T_GSBH_endereco (cep, logradouro, nr_endereco, bairro, localidade, uf, id_usuario) VALUES ('56789012', 'Rua E', '25', 'Centro', 'Curitiba', 'PR', 1);

-- relatório usando classificação crescente de dados
SELECT * FROM T_GSBH_usuarios ORDER BY nm_usuario ASC;

-- relatório usando between e like
SELECT * FROM T_GSBH_endereco WHERE id_usuario BETWEEN 1 AND 5 AND logradouro LIKE '%Rua%';

--  relatórios usando função caracter
SELECT SUBSTR(nm_usuario, 1, 3) AS primeiras_tres_letras FROM T_GSBH_usuarios;

-- relatório usando função data - a seu critério
SELECT EXTRACT(YEAR FROM created_at) AS ano,
       COUNT(*) AS total_usuarios
FROM T_GSBH_usuarios
GROUP BY EXTRACT(YEAR FROM created_at);

-- relatório usando group by
SELECT uf, COUNT(*) as total_usuarios FROM T_GSBH_endereco GROUP BY uf;

-- relatório usando junção de equivalência
SELECT u.*, e.*
FROM T_GSBH_usuarios u
INNER JOIN T_GSBH_endereco e ON u.id_usuario = e.id_usuario;

-- relatório usando junção de diferença
SELECT u.*, e.*
FROM T_GSBH_usuarios u
LEFT JOIN T_GSBH_endereco e ON u.id_usuario = e.id_usuario
WHERE e.id_usuario IS NULL;