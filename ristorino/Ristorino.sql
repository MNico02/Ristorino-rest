/* =========================================================
   RISTORINO - Esquema lógico en Microsoft SQL Server
   Nota: se respetan nombres tal como figuran en el diseño.
   ========================================================= */

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =====================
   1) Tablas base simples
   ===================== */

-- provincias
CREATE TABLE dbo.provincias (
    cod_provincia      INT           NOT NULL,
    nom_provincia      NVARCHAR(120) NOT NULL,
    CONSTRAINT PK_provincias PRIMARY KEY (cod_provincia)
);
GO

-- localidades (AK sobre (cod_provincia, nom_localidad))
CREATE TABLE dbo.localidades (
    nro_localidad  INT            NOT NULL,
    nom_localidad  NVARCHAR(120)  NOT NULL, -- (AK1.2)
    cod_provincia  INT            NOT NULL, -- (FK) (AK1.1)
    CONSTRAINT PK_localidades PRIMARY KEY (nro_localidad),
    CONSTRAINT FK_localidades_provincias
        FOREIGN KEY (cod_provincia) REFERENCES dbo.provincias (cod_provincia),
    CONSTRAINT UQ_localidades_provincia_nombre UNIQUE (cod_provincia, nom_localidad)
);
GO

-- restaurantes (AK en cuit)
CREATE TABLE dbo.restaurantes (
    nro_restaurante  INT           NOT NULL,
    razon_social     NVARCHAR(200) NOT NULL,
    cuit             NVARCHAR(20)  NOT NULL, -- (AK1.1)
    CONSTRAINT PK_restaurantes PRIMARY KEY (nro_restaurante),
    CONSTRAINT UQ_restaurantes_cuit UNIQUE (cuit)
);
GO

-- atributos
CREATE TABLE dbo.atributos (
    cod_atributo  INT            NOT NULL,
    nom_atributo  NVARCHAR(120)  NOT NULL,
    tipo_dato     NVARCHAR(50)   NOT NULL,
    CONSTRAINT PK_atributos PRIMARY KEY (cod_atributo)
);
GO

-- categorias_preferencias
CREATE TABLE dbo.categorias_preferencias (
    cod_categoria  INT            NOT NULL,
    nom_categoria  NVARCHAR(120)  NOT NULL,
    CONSTRAINT PK_categorias_preferencias PRIMARY KEY (cod_categoria)
);
GO

-- idiomas (AK en cod_idioma)
CREATE TABLE dbo.idiomas (
    nro_idioma  INT            NOT NULL,
    nom_idioma  NVARCHAR(120)  NOT NULL,
    cod_idioma  NVARCHAR(10)   NOT NULL,
    CONSTRAINT PK_idiomas PRIMARY KEY (nro_idioma),
    CONSTRAINT UQ_idiomas_cod UNIQUE (cod_idioma)
);
GO

/* ==========================
   2) Tablas con dependencias
   ========================== */

-- clientes (AK en correo)
CREATE TABLE dbo.clientes (
    nro_cliente    INT            NOT NULL,
    apellido       NVARCHAR(120)  NOT NULL,
    nombre         NVARCHAR(120)  NOT NULL,
    clave          NVARCHAR(255)  NOT NULL,
    correo         NVARCHAR(255)  NOT NULL, -- (AK1.1)
    telefonos      NVARCHAR(100)  NULL,
    nro_localidad  INT            NOT NULL,
    habilitado     BIT            NOT NULL DEFAULT (1),
    CONSTRAINT PK_clientes PRIMARY KEY (nro_cliente),
    CONSTRAINT UQ_clientes_correo UNIQUE (correo),
    CONSTRAINT FK_clientes_localidades
        FOREIGN KEY (nro_localidad) REFERENCES dbo.localidades (nro_localidad)
);
GO

-- sucursales_restaurantes
-- PK natural (nro_restaurante, nro_sucursal)
-- AK (nro_restaurante, cod_sucursal_restaurante) (AK1.x)
CREATE TABLE dbo.sucursales_restaurantes (
    nro_restaurante           INT            NOT NULL, -- (FK) (AK1.1)
    nro_sucursal              INT            NOT NULL,
    nom_sucursal              NVARCHAR(150)  NOT NULL,
    calle                     NVARCHAR(150)  NULL,
    nro_calle                 INT            NULL,
    barrio                    NVARCHAR(120)  NULL,
    nro_localidad             INT            NOT NULL, -- (FK)
    cod_postal                NVARCHAR(20)   NULL,
    telefonos                 NVARCHAR(100)  NULL,
    total_comensales          INT            NULL,
    min_tolerencia_reserva    INT            NULL,
    cod_sucursal_restaurante  NVARCHAR(50)   NOT NULL, -- (AK1.2)
    CONSTRAINT PK_sucursales_restaurantes PRIMARY KEY (nro_restaurante, nro_sucursal),
    CONSTRAINT UQ_sucursales_restaurantes_cod UNIQUE (nro_restaurante, cod_sucursal_restaurante),
    CONSTRAINT FK_suc_rest_restaurantes
        FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes (nro_restaurante),
    CONSTRAINT FK_suc_rest_localidades
        FOREIGN KEY (nro_localidad) REFERENCES dbo.localidades (nro_localidad)
);
GO

-- configuracion_restaurantes (por par restaurante-atributo)
CREATE TABLE dbo.configuracion_restaurantes (
    nro_restaurante  INT           NOT NULL, -- (FK)
    cod_atributo     INT           NOT NULL, -- (FK)
    valor            NVARCHAR(400) NOT NULL,
    CONSTRAINT PK_configuracion_restaurantes PRIMARY KEY (nro_restaurante, cod_atributo),
    CONSTRAINT FK_config_rest_restaurantes
        FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes (nro_restaurante),
    CONSTRAINT FK_config_rest_atributos
        FOREIGN KEY (cod_atributo) REFERENCES dbo.atributos (cod_atributo)
);
GO

-- dominio_categorias_preferencias (dominio por categoría)
CREATE TABLE dbo.dominio_categorias_preferencias (
    cod_categoria      INT            NOT NULL, -- (FK)
    nro_valor_dominio  INT            NOT NULL,
    nom_valor_dominio  NVARCHAR(150)  NOT NULL,
    CONSTRAINT PK_dominio_cat_pref PRIMARY KEY (cod_categoria, nro_valor_dominio),
    CONSTRAINT FK_dominio_cat_pref_categoria
        FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias (cod_categoria)
);
GO

-- idiomas_categorias_preferencias
CREATE TABLE dbo.idiomas_categorias_preferencias (
    cod_categoria  INT            NOT NULL, -- (FK)
    nro_idioma     INT            NOT NULL, -- (FK)
    categoria      NVARCHAR(150)  NOT NULL,
    desc_categoria NVARCHAR(500)  NULL,
    CONSTRAINT PK_idiomas_cat_pref PRIMARY KEY (cod_categoria, nro_idioma),
    CONSTRAINT FK_idiomas_cat_pref_categoria
        FOREIGN KEY (cod_categoria) REFERENCES dbo.categorias_preferencias (cod_categoria),
    CONSTRAINT FK_idiomas_cat_pref_idiomas
        FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas (nro_idioma)
);
GO

-- idiomas_dominio_cat_preferencias
CREATE TABLE dbo.idiomas_dominio_cat_preferencias (
    cod_categoria      INT            NOT NULL, -- (FK)
    nro_valor_dominio  INT            NOT NULL, -- (FK)
    nro_idioma         INT            NOT NULL, -- (FK)
    valor_dominio      NVARCHAR(150)  NOT NULL,
    desc_valor_dominio NVARCHAR(500)  NULL,
    CONSTRAINT PK_idiomas_dom_cat_pref PRIMARY KEY (cod_categoria, nro_valor_dominio, nro_idioma),
    CONSTRAINT FK_idiomas_dom_cat_pref_dom
        FOREIGN KEY (cod_categoria, nro_valor_dominio)
            REFERENCES dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio),
    CONSTRAINT FK_idiomas_dom_cat_pref_idioma
        FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas (nro_idioma)
);
GO

-- contenidos_restaurantes
CREATE TABLE dbo.contenidos_restaurantes (
    nro_restaurante       INT             NOT NULL, -- (FK)
    nro_idioma            INT             NOT NULL, -- (FK)
    nro_contenido         INT             NOT NULL,
    nro_sucursal          INT             NULL,     -- (FK)
    contenido_promocional NVARCHAR(MAX)   NULL,
    imagen_promocional    NVARCHAR(255)   NULL,     -- ruta/URL de imagen
    contenido_a_publicar  NVARCHAR(MAX)   NULL,
    fecha_ini_vigencia    DATE            NULL,
    fecha_fin_vigencia    DATE            NULL,
    costo_click           DECIMAL(12,2)   NULL,
    CONSTRAINT PK_contenidos_restaurantes
        PRIMARY KEY (nro_restaurante, nro_idioma, nro_contenido),
    CONSTRAINT FK_cont_rest_rest
        FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes (nro_restaurante),
    CONSTRAINT FK_cont_rest_idioma
        FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas (nro_idioma),
    CONSTRAINT FK_cont_rest_sucursal
        FOREIGN KEY (nro_restaurante, nro_sucursal)
            REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);
GO

-- preferencias_restaurantes
CREATE TABLE dbo.preferencias_restaurantes (
    nro_restaurante      INT            NOT NULL, -- (FK)
    cod_categoria        INT            NOT NULL, -- (FK)
    nro_valor_dominio    INT            NOT NULL, -- (FK)
    nro_preferencia      INT            NOT NULL,
    observaciones        NVARCHAR(500)  NULL,
    nro_sucursal         INT            NULL,     -- (FK)
    CONSTRAINT PK_preferencias_restaurantes
        PRIMARY KEY (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
    CONSTRAINT FK_pref_rest_rest
        FOREIGN KEY (nro_restaurante) REFERENCES dbo.restaurantes (nro_restaurante),
    CONSTRAINT FK_pref_rest_dom
        FOREIGN KEY (cod_categoria, nro_valor_dominio)
            REFERENCES dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio),
    CONSTRAINT FK_pref_rest_sucursal
        FOREIGN KEY (nro_restaurante, nro_sucursal)
            REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);
GO

-- turnos_sucursales_restaurantes
CREATE TABLE dbo.turnos_sucursales_restaurantes (
    nro_restaurante  INT       NOT NULL, -- (FK)
    nro_sucursal     INT       NOT NULL, -- (FK)
    hora_desde       TIME(0)   NOT NULL,
    hora_hasta       TIME(0)   NOT NULL,
    habilitado       BIT       NOT NULL DEFAULT (1),
    CONSTRAINT PK_turnos_sucursales_restaurantes
        PRIMARY KEY (nro_restaurante, nro_sucursal, hora_desde),
    CONSTRAINT FK_turnos_sucursales_sucursal
        FOREIGN KEY (nro_restaurante, nro_sucursal)
            REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);
GO

-- zonas_sucursales_restaurantes
CREATE TABLE dbo.zonas_sucursales_restaurantes (
    nro_restaurante  INT            NOT NULL, -- (FK)
    nro_sucursal     INT            NOT NULL, -- (FK)
    cod_zona         INT            NOT NULL,
    desc_zona        NVARCHAR(200)  NULL,
    cant_comensales  INT            NULL,
    permite_menores  BIT            NOT NULL DEFAULT (1),
    habilitada       BIT            NOT NULL DEFAULT (1),
    CONSTRAINT PK_zonas_sucursales_restaurantes
        PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona),
    CONSTRAINT FK_zonas_sucursales_sucursal
        FOREIGN KEY (nro_restaurante, nro_sucursal)
            REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal)
);
GO

-- idiomas_zonas_suc_restaurantes
CREATE TABLE dbo.idiomas_zonas_suc_restaurantes (
    nro_restaurante  INT            NOT NULL, -- (FK)
    nro_sucursal     INT            NOT NULL, -- (FK)
    cod_zona         INT            NOT NULL, -- (FK)
    nro_idioma       INT            NOT NULL, -- (FK)
    zona             NVARCHAR(150)  NOT NULL,
    desc_zona        NVARCHAR(400)  NULL,
    CONSTRAINT PK_idiomas_zonas_suc_rest
        PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona, nro_idioma),
    CONSTRAINT FK_idiomas_zonas_zona
        FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
            REFERENCES dbo.zonas_sucursales_restaurantes (nro_restaurante, nro_sucursal, cod_zona),
    CONSTRAINT FK_idiomas_zonas_idioma
        FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas (nro_idioma)
);
GO

-- zonas_turnos_sucurales_restaurantes  (nombre con "sucurales" tal como el diseño)
CREATE TABLE dbo.zonas_turnos_sucurales_restaurantes (
    nro_restaurante  INT      NOT NULL, -- (FK)
    nro_sucursal     INT      NOT NULL, -- (FK)
    cod_zona         INT      NOT NULL, -- (FK)
    hora_desde       TIME(0)  NOT NULL, -- (FK)
    permite_menores  BIT      NOT NULL DEFAULT (1),
    CONSTRAINT PK_zonas_turnos_sucurales_restaurantes
        PRIMARY KEY (nro_restaurante, nro_sucursal, cod_zona, hora_desde),
    CONSTRAINT FK_zonas_turnos_zona
        FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
            REFERENCES dbo.zonas_sucursales_restaurantes (nro_restaurante, nro_sucursal, cod_zona),
    CONSTRAINT FK_zonas_turnos_turno
        FOREIGN KEY (nro_restaurante, nro_sucursal, hora_desde)
            REFERENCES dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde)
);
GO

-- estados_reservas
CREATE TABLE dbo.estados_reservas (
    cod_estado  INT            NOT NULL,
    nom_estado  NVARCHAR(120)  NOT NULL,
    CONSTRAINT PK_estados_reservas PRIMARY KEY (cod_estado)
);
GO

-- idiomas_estados
CREATE TABLE dbo.idiomas_estados (
    cod_estado  INT            NOT NULL, -- (FK)
    nro_idioma  INT            NOT NULL, -- (FK)
    estado      NVARCHAR(150)  NOT NULL,
    CONSTRAINT PK_idiomas_estados PRIMARY KEY (cod_estado, nro_idioma),
    CONSTRAINT FK_idiomas_estados_estado
        FOREIGN KEY (cod_estado) REFERENCES dbo.estados_reservas (cod_estado),
    CONSTRAINT FK_idiomas_estados_idioma
        FOREIGN KEY (nro_idioma) REFERENCES dbo.idiomas (nro_idioma)
);
GO

-- reservas_restaurantes (AK en cod_reserva_sucursal)
CREATE TABLE dbo.reservas_restaurantes (
    nro_cliente          INT          NOT NULL, -- (FK)
    nro_reserva          INT          NOT NULL,
    cod_reserva_sucursal NVARCHAR(50) NOT NULL, -- (AK1.1)
    fecha_reserva        DATE         NOT NULL,
    hora_reserva         TIME(0)      NOT NULL,
    nro_restaurante      INT          NOT NULL, -- (FK)
    nro_sucursal         INT          NOT NULL, -- (FK)
    cod_zona             INT          NOT NULL, -- (FK)
    hora_desde           TIME(0)      NOT NULL, -- (FK)
    cant_adultos         INT          NOT NULL,
    cant_menores         INT          NOT NULL DEFAULT (0),
    cod_estado           INT          NOT NULL, -- (FK)
    fecha_cancelacion    DATETIME     NULL,
    costo_reserva        DECIMAL(12,2) NULL,
    CONSTRAINT PK_reservas_restaurantes PRIMARY KEY (nro_cliente, nro_reserva),
    CONSTRAINT UQ_reservas_restaurantes_cod UNIQUE (cod_reserva_sucursal),
    CONSTRAINT FK_reservas_cliente
        FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes (nro_cliente),
    CONSTRAINT FK_reservas_sucursal
        FOREIGN KEY (nro_restaurante, nro_sucursal)
            REFERENCES dbo.sucursales_restaurantes (nro_restaurante, nro_sucursal),
    CONSTRAINT FK_reservas_zona
        FOREIGN KEY (nro_restaurante, nro_sucursal, cod_zona)
            REFERENCES dbo.zonas_sucursales_restaurantes (nro_restaurante, nro_sucursal, cod_zona),
    CONSTRAINT FK_reservas_turno
        FOREIGN KEY (nro_restaurante, nro_sucursal, hora_desde)
            REFERENCES dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde),
    CONSTRAINT FK_reservas_estado
        FOREIGN KEY (cod_estado) REFERENCES dbo.estados_reservas (cod_estado)
);
GO

-- preferencias_clientes
CREATE TABLE dbo.preferencias_clientes (
    nro_cliente        INT            NOT NULL, -- (FK)
    cod_categoria      INT            NOT NULL, -- (FK)
    nro_valor_dominio  INT            NOT NULL, -- (FK)
    observaciones      NVARCHAR(500)  NULL,
    CONSTRAINT PK_preferencias_clientes
        PRIMARY KEY (nro_cliente, cod_categoria, nro_valor_dominio),
    CONSTRAINT FK_pref_cli_cliente
        FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes (nro_cliente),
    CONSTRAINT FK_pref_cli_dom
        FOREIGN KEY (cod_categoria, nro_valor_dominio)
            REFERENCES dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio)
);
GO

-- preferencias_reservas_restaurantes
CREATE TABLE dbo.preferencias_reservas_restaurantes (
    nro_cliente        INT            NOT NULL, -- (FK a reservas)
    nro_reserva        INT            NOT NULL, -- (FK a reservas)
    nro_restaurante    INT            NOT NULL, -- (FK a pref_rest)
    cod_categoria      INT            NOT NULL, -- (FK a dominio y pref_rest)
    nro_valor_dominio  INT            NOT NULL, -- (FK a dominio y pref_rest)
    nro_preferencia    INT            NOT NULL, -- (FK a pref_rest)
    observaciones      NVARCHAR(500)  NULL,
    CONSTRAINT PK_pref_reservas_rest
        PRIMARY KEY (nro_cliente, nro_reserva, nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia),
    CONSTRAINT FK_pref_res_rest_reserva
        FOREIGN KEY (nro_cliente, nro_reserva)
            REFERENCES dbo.reservas_restaurantes (nro_cliente, nro_reserva),
    CONSTRAINT FK_pref_res_rest_dom
        FOREIGN KEY (cod_categoria, nro_valor_dominio)
            REFERENCES dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio),
    CONSTRAINT FK_pref_res_rest_pref_rest
        FOREIGN KEY (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
            REFERENCES dbo.preferencias_restaurantes (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia)
);
GO

-- costos
CREATE TABLE dbo.costos (
    tipo_costo         NVARCHAR(50)  NOT NULL,
    fecha_ini_vigencia DATE          NOT NULL,
    fecha_fin_vigencia DATE          NULL,
    monto              DECIMAL(12,2) NOT NULL,
    CONSTRAINT PK_costos PRIMARY KEY (tipo_costo, fecha_ini_vigencia)
);
GO


/* ===========================
   3) Inserts básicos de prueba
   =========================== */

-- provincias
INSERT INTO dbo.provincias (cod_provincia, nom_provincia) VALUES
(1, N'Córdoba');

-- localidades
INSERT INTO dbo.localidades (nro_localidad, nom_localidad, cod_provincia) VALUES
(1, N'Córdoba Capital', 1);

-- restaurantes
INSERT INTO dbo.restaurantes (nro_restaurante, razon_social, cuit) VALUES
(1, N'Ristorino S.A.', N'30-12345678-9');

-- atributos
INSERT INTO dbo.atributos (cod_atributo, nom_atributo, tipo_dato) VALUES
(1, N'WiFi', N'BIT'),
(2, N'Música en vivo', N'BIT');

-- categorias_preferencias
INSERT INTO dbo.categorias_preferencias (cod_categoria, nom_categoria) VALUES
(1, N'Tipo de cocina'),
(2, N'Restricciones alimentarias');

-- idiomas
INSERT INTO dbo.idiomas (nro_idioma, nom_idioma, cod_idioma) VALUES
(1, N'Español', N'es-AR'),
(2, N'English', N'en-US');

-- clientes
INSERT INTO dbo.clientes (nro_cliente, apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado) VALUES
(1, N'Pérez', N'Juan', N'$2b$12$hashdemo', N'juan.perez@example.com', N'351-555-0000', 1, 1);

-- sucursales_restaurantes
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
(1, 1, N'Casa Central', N'Av. Siempreviva', 742, N'Centro', 1, N'5000', N'351-555-1111', 50, 15, N'CEN');

-- configuracion_restaurantes
INSERT INTO dbo.configuracion_restaurantes (nro_restaurante, cod_atributo, valor) VALUES
(1, 1, N'1'), -- WiFi: habilitado
(1, 2, N'0'); -- Música en vivo: deshabilitado

-- dominio_categorias_preferencias
INSERT INTO dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES
(1, 1, N'Italiana'),
(1, 2, N'Parrilla');

-- idiomas_categorias_preferencias
INSERT INTO dbo.idiomas_categorias_preferencias (cod_categoria, nro_idioma, categoria, desc_categoria) VALUES
(1, 1, N'Tipo de cocina', N'Clasificación de la cocina ofrecida'),
(1, 2, N'Cuisine type', N'Cuisine classification');

-- idiomas_dominio_cat_preferencias
INSERT INTO dbo.idiomas_dominio_cat_preferencias (cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio, desc_valor_dominio) VALUES
(1, 1, 1, N'Italiana', N'Cocina italiana'),
(1, 1, 2, N'Italian',  N'Italian cuisine');

-- contenidos_restaurantes
INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_contenido, nro_sucursal, contenido_promocional, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click)
VALUES
(1, 1, 1, 1, N'2x1 en pastas miércoles', N'https://ejemplo/imagen1.jpg', N'Promo semanal', '2025-09-01', '2025-12-31', 0.10);

-- preferencias_restaurantes
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal)
VALUES
(1, 1, 1, 1, N'Especialidad de la casa', 1);

-- turnos_sucursales_restaurantes
INSERT INTO dbo.turnos_sucursales_restaurantes
(nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
VALUES
(1, 1, '20:00', '23:00', 1);

-- zonas_sucursales_restaurantes
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
(1, 1, 1, N'Salón principal', 40, 1, 1);

-- idiomas_zonas_suc_restaurantes
INSERT INTO dbo.idiomas_zonas_suc_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, nro_idioma, zona, desc_zona)
VALUES
(1, 1, 1, 1, N'Salón', N'Área central del local');

-- zonas_turnos_sucurales_restaurantes  (respeta el nombre del diseño)
INSERT INTO dbo.zonas_turnos_sucurales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
VALUES
(1, 1, 1, '20:00', 1);

-- estados_reservas
INSERT INTO dbo.estados_reservas (cod_estado, nom_estado) VALUES
(1, N'Confirmada'),
(2, N'Cancelada');

-- idiomas_estados
INSERT INTO dbo.idiomas_estados (cod_estado, nro_idioma, estado) VALUES
(1, 1, N'Confirmada'),
(2, 1, N'Cancelada');

-- reservas_restaurantes
INSERT INTO dbo.reservas_restaurantes
(nro_cliente, nro_reserva, cod_reserva_sucursal, fecha_reserva, hora_reserva,
 nro_restaurante, nro_sucursal, cod_zona, hora_desde, cant_adultos, cant_menores,
 cod_estado, fecha_cancelacion, costo_reserva)
VALUES
(1, 1, N'R-0001', '2025-09-20', '20:30',
 1, 1, 1, '20:00', 2, 0,
 1, NULL, 500.00);

-- preferencias_clientes
INSERT INTO dbo.preferencias_clientes
(nro_cliente, cod_categoria, nro_valor_dominio, observaciones)
VALUES
(1, 1, 1, N'Prefiere cocina italiana');

-- preferencias_reservas_restaurantes
INSERT INTO dbo.preferencias_reservas_restaurantes
(nro_cliente, nro_reserva, nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones)
VALUES
(1, 1, 1, 1, 1, 1, N'Mesa cerca de la ventana');

-- costos
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto) VALUES
(N'CLICK', '2025-09-01', '2025-12-31', 0.10);
GO

PRINT 'Esquema creado e inserts de ejemplo cargados correctamente.';