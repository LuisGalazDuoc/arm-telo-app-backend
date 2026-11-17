--liquibase formatted sql

--changeset armatelo:1-crear-tabla-mensajes
CREATE TABLE mensajes (
    id      BIGINT        NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(80)   NOT NULL,
    correo  VARCHAR(120)  NOT NULL,
    asunto  VARCHAR(150)  NOT NULL,
    mensaje VARCHAR(2000) NOT NULL,
    fecha   DATETIME(6)   NOT NULL,
    CONSTRAINT pk_mensajes PRIMARY KEY (id)
);
--rollback DROP TABLE mensajes;
