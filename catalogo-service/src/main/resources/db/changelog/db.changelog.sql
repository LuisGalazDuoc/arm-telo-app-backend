--liquibase formatted sql

--changeset armatelo:1-crear-tabla-categorias
CREATE TABLE categorias (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(80)  NOT NULL,
    descripcion VARCHAR(300),
    icono       VARCHAR(50),
    CONSTRAINT pk_categorias PRIMARY KEY (id),
    CONSTRAINT uk_categorias_nombre UNIQUE (nombre)
);
--rollback DROP TABLE categorias;

--changeset armatelo:2-crear-tabla-productos
CREATE TABLE productos (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(150)  NOT NULL,
    descripcion   VARCHAR(1000),
    marca         VARCHAR(80),
    categoria_id  BIGINT        NOT NULL,
    precio        INT           NOT NULL,
    precio_oferta INT           NULL COMMENT 'null = sin oferta',
    stock         INT           NULL COMMENT 'null = stock por definir',
    stock_critico INT           NOT NULL DEFAULT 5,
    imagen        VARCHAR(500),
    CONSTRAINT pk_productos PRIMARY KEY (id),
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id)
);
--rollback DROP TABLE productos;

--changeset armatelo:3-categorias-iniciales
--comment: Las mismas 8 categorías (e ids) de src/data/seed.js del frontend.
INSERT INTO categorias (id, nombre, descripcion, icono) VALUES
(1, 'Procesadores',      'CPU AMD y Intel para todo presupuesto.',      'bi-cpu'),
(2, 'Tarjetas de video', 'GPU para juegos, diseño y trabajo.',          'bi-gpu-card'),
(3, 'Memoria RAM',       'Módulos DDR4 y DDR5.',                        'bi-memory'),
(4, 'Almacenamiento',    'SSD, discos duros y unidades NVMe.',          'bi-device-ssd'),
(5, 'Placas madre',      'Placas para plataformas AMD e Intel.',        'bi-motherboard'),
(6, 'Fuentes de poder',  'Fuentes certificadas de distintas potencias.', 'bi-lightning-charge'),
(7, 'Gabinetes',         'Gabinetes con buen flujo de aire.',           'bi-pc'),
(8, 'Periféricos',       'Teclados, mouse, audífonos y monitores.',     'bi-keyboard');
--rollback DELETE FROM categorias WHERE id BETWEEN 1 AND 8;

--changeset armatelo:4-productos-de-ejemplo
INSERT INTO productos (nombre, marca, descripcion, categoria_id, precio, precio_oferta, stock, stock_critico) VALUES
('Ryzen 5 5600',                'AMD',           'Procesador de 6 núcleos y 12 hilos.',            1, 129990, 114990, 15, 5),
('Core i5-12400F',              'Intel',         'Procesador de 6 núcleos para gaming.',           1, 139990, NULL,   8,  3),
('GeForce RTX 4060 8GB',        'NVIDIA',        'Tarjeta de video para jugar en 1080p y 1440p.',  2, 349990, 329990, 6,  2),
('Kingston Fury 16GB DDR4 3200', 'Kingston',     'Kit de 2x8GB.',                                  3, 44990,  NULL,   30, 5),
('SSD NVMe 1TB',                'Samsung',       'Unidad M.2 de alta velocidad.',                  4, 79990,  69990,  3,  5),
('Placa B550M',                 'ASUS',          'Placa madre micro-ATX para AM4.',                5, 109990, NULL,   10, 3),
('Fuente 650W 80 Plus Bronze',  'Corsair',       'Fuente certificada.',                            6, 64990,  NULL,   12, 4),
('Gabinete ATX Airflow',        'Cooler Master', 'Gabinete con 3 ventiladores.',                   7, 59990,  NULL,   NULL, 2),
('Teclado mecánico TKL',        'Logitech',      'Teclado mecánico sin pad numérico.',             8, 49990,  39990,  20, 5);
--rollback DELETE FROM productos;
