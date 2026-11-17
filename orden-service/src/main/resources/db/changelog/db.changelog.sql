--liquibase formatted sql

--changeset armatelo:1-crear-tabla-ordenes
CREATE TABLE ordenes (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    numero            VARCHAR(30),
    usuario_id        BIGINT       NULL COMMENT 'id en auth-service; null = compra como invitado',
    cliente_nombre    VARCHAR(80)  NOT NULL,
    cliente_apellidos VARCHAR(120) NOT NULL,
    cliente_correo    VARCHAR(120) NOT NULL,
    dir_calle         VARCHAR(150),
    dir_departamento  VARCHAR(60),
    dir_region        VARCHAR(80),
    dir_comuna        VARCHAR(80),
    dir_indicaciones  VARCHAR(300),
    entrega           VARCHAR(20)  NOT NULL,
    subtotal          INT          NOT NULL,
    envio             INT          NOT NULL,
    total             INT          NOT NULL,
    estado            VARCHAR(20)  NOT NULL,
    motivo            VARCHAR(300),
    fecha             DATETIME(6)  NOT NULL,
    fecha_pago        DATETIME(6),
    CONSTRAINT pk_ordenes PRIMARY KEY (id)
);
CREATE INDEX idx_ordenes_usuario ON ordenes (usuario_id);
CREATE INDEX idx_ordenes_correo ON ordenes (cliente_correo);
--rollback DROP TABLE ordenes;

--changeset armatelo:2-crear-tabla-detalle-orden
--comment: producto_id y categoria_id apuntan a catalogo-service (otra base), por eso no son claves foráneas.
CREATE TABLE detalle_orden (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    orden_id     BIGINT       NOT NULL,
    producto_id  BIGINT,
    categoria_id BIGINT,
    nombre       VARCHAR(150) NOT NULL,
    precio       INT          NOT NULL,
    cantidad     INT          NOT NULL,
    subtotal     INT          NOT NULL,
    CONSTRAINT pk_detalle_orden PRIMARY KEY (id),
    CONSTRAINT fk_detalle_orden_orden FOREIGN KEY (orden_id) REFERENCES ordenes (id) ON DELETE CASCADE
);
--rollback DROP TABLE detalle_orden;
