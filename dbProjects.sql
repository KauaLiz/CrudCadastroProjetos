
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255),
    role VARCHAR(255),
    senha VARCHAR(255),
    CONSTRAINT users_role_check
        CHECK (role IN ('ADMINISTRADOR', 'MEMBRO'))
);

CREATE TABLE projeto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    descricao VARCHAR(255),
    risco VARCHAR(255),
    status VARCHAR(255),
    data_inicio DATE,
    previsao_termino DATE,
    data_termino DATE,
    orcamento NUMERIC(38,2),
    gerente_id BIGINT
);

CREATE TABLE projeto_membros (
    projeto_id BIGINT NOT NULL,
    membro_id BIGINT,

    CONSTRAINT fk_projeto
        FOREIGN KEY (projeto_id)
        REFERENCES projeto(id)
);
```
