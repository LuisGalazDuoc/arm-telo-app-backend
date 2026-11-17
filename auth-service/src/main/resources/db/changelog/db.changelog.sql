--liquibase formatted sql

--changeset armatelo:1-crear-tabla-usuarios
CREATE TABLE usuarios (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(80)  NOT NULL,
    apellidos      VARCHAR(120) NOT NULL,
    correo         VARCHAR(120) NOT NULL,
    password       VARCHAR(255) NOT NULL,
    rol            VARCHAR(20)  NOT NULL,
    telefono       VARCHAR(30),
    region         VARCHAR(80),
    comuna         VARCHAR(80),
    calle          VARCHAR(150),
    departamento   VARCHAR(60),
    fecha_registro DATETIME(6)  NOT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_correo UNIQUE (correo)
);
--rollback DROP TABLE usuarios;

--changeset armatelo:2-usuarios-iniciales
--comment: Cuentas de prueba. Contraseñas cifradas con BCrypt: Admin1234 y Cliente1234.
INSERT INTO usuarios (nombre, apellidos, correo, password, rol, fecha_registro) VALUES
('Administrador', 'Armatelo', 'admin@armatelo.cl',   '$2a$10$QYP72Yr4.CJf4lJRAmzX2OP1LLeAoWW1UcQnk77vC9Ah2pY0Mvsn6', 'ADMIN',   NOW(6)),
('Cliente',       'Demo',     'cliente@armatelo.cl', '$2a$10$FvVYQduroecdJpcZjrnIyeX3tdAjfYkHcD7P/YBhAqryB8g/vSOUe', 'CLIENTE', NOW(6));
--rollback DELETE FROM usuarios WHERE correo IN ('admin@armatelo.cl', 'cliente@armatelo.cl');
