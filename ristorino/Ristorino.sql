-- 1) Tablas más dependientes (niveles bajos)
IF OBJECT_ID('dbo.clicks_contenidos_restaurantes','U') IS NOT NULL DROP TABLE dbo.clicks_contenidos_restaurantes;
IF OBJECT_ID('dbo.preferencias_reservas_restaurantes','U') IS NOT NULL DROP TABLE dbo.preferencias_reservas_restaurantes;
IF OBJECT_ID('dbo.preferencias_clientes','U') IS NOT NULL DROP TABLE dbo.preferencias_clientes;
IF OBJECT_ID('dbo.reservas_restaurantes','U') IS NOT NULL DROP TABLE dbo.reservas_restaurantes;
IF OBJECT_ID('dbo.idiomas_zonas_suc_restaurantes','U') IS NOT NULL DROP TABLE dbo.idiomas_zonas_suc_restaurantes;
IF OBJECT_ID('dbo.zonas_turnos_sucurales_restaurantes','U') IS NOT NULL DROP TABLE dbo.zonas_turnos_sucurales_restaurantes;
IF OBJECT_ID('dbo.zonas_sucursales_restaurantes','U') IS NOT NULL DROP TABLE dbo.zonas_sucursales_restaurantes;
IF OBJECT_ID('dbo.turnos_sucursales_restaurantes','U') IS NOT NULL DROP TABLE dbo.turnos_sucursales_restaurantes;
IF OBJECT_ID('dbo.preferencias_restaurantes','U') IS NOT NULL DROP TABLE dbo.preferencias_restaurantes;
IF OBJECT_ID('dbo.contenidos_restaurantes','U') IS NOT NULL DROP TABLE dbo.contenidos_restaurantes;
IF OBJECT_ID('dbo.idiomas_dominio_cat_preferencias','U') IS NOT NULL DROP TABLE dbo.idiomas_dominio_cat_preferencias;
IF OBJECT_ID('dbo.idiomas_categorias_preferencias','U') IS NOT NULL DROP TABLE dbo.idiomas_categorias_preferencias;
IF OBJECT_ID('dbo.dominio_categorias_preferencias','U') IS NOT NULL DROP TABLE dbo.dominio_categorias_preferencias;
IF OBJECT_ID('dbo.configuracion_restaurantes','U') IS NOT NULL DROP TABLE dbo.configuracion_restaurantes;
IF OBJECT_ID('dbo.idiomas_estados','U') IS NOT NULL DROP TABLE dbo.idiomas_estados;

-- 2) Tablas intermedias
IF OBJECT_ID('dbo.sucursales_restaurantes','U') IS NOT NULL DROP TABLE dbo.sucursales_restaurantes;
IF OBJECT_ID('dbo.clientes','U') IS NOT NULL DROP TABLE dbo.clientes;
IF OBJECT_ID('dbo.estados_reservas','U') IS NOT NULL DROP TABLE dbo.estados_reservas;
IF OBJECT_ID('dbo.restaurantes','U') IS NOT NULL DROP TABLE dbo.restaurantes;
IF OBJECT_ID('dbo.idiomas','U') IS NOT NULL DROP TABLE dbo.idiomas;
IF OBJECT_ID('dbo.categorias_preferencias','U') IS NOT NULL DROP TABLE dbo.categorias_preferencias;
IF OBJECT_ID('dbo.atributos','U') IS NOT NULL DROP TABLE dbo.atributos;
IF OBJECT_ID('dbo.localidades','U') IS NOT NULL DROP TABLE dbo.localidades;
IF OBJECT_ID('dbo.provincias','U') IS NOT NULL DROP TABLE dbo.provincias;
IF OBJECT_ID('dbo.costos','U') IS NOT NULL DROP TABLE dbo.costos;

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =====================
   1) Tablas base simples
   ===================== */

-- provincias
CREATE TABLE dbo.provincias (
                                cod_provincia INT IDENTITY(1,1) NOT NULL,
                                nom_provincia NVARCHAR(120) COLLATE Latin1_General_CI_AI NOT NULL,
                                CONSTRAINT PK_provincias PRIMARY KEY (cod_provincia),
                                CONSTRAINT UQ_provincias_nombre UNIQUE (nom_provincia)
);

-- localidades (AK sobre (cod_provincia, nom_localidad))
CREATE TABLE dbo.localidades (
                                 nro_localidad  INT IDENTITY(1,1) NOT NULL,
                                 nom_localidad  NVARCHAR(120) COLLATE Latin1_General_CI_AI NOT NULL, -- (AK1.2)
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
                              nro_cliente      INT IDENTITY(1,1) NOT NULL,
                              apellido       NVARCHAR(120)  NOT NULL,
                              nombre         NVARCHAR(120)  NOT NULL,
                              clave          NVARCHAR(255)  NOT NULL,
                              correo         NVARCHAR(255)  NOT NULL, -- (AK1.1)
                              telefonos      NVARCHAR(100)  NOT NULL,
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

-- ============================================================
-- Tabla: contenidos_restaurantes
-- ============================================================
CREATE TABLE dbo.contenidos_restaurantes (
                                             nro_restaurante         INT             NOT NULL, -- (FK)
                                             nro_idioma              INT             NOT NULL, -- (FK)
                                             nro_contenido           INT       IDENTITY(1,1)       NOT NULL,
                                             nro_sucursal            INT             NULL,     -- (FK)
                                             contenido_promocional   NVARCHAR(MAX)   NULL,
                                             imagen_promocional      NVARCHAR(255)   NULL,     -- ruta/URL de imagen
                                             contenido_a_publicar    NVARCHAR(MAX)   NULL,
                                             fecha_ini_vigencia      DATE            NULL,
                                             fecha_fin_vigencia      DATE            NULL,
                                             costo_click             DECIMAL(12,2)   NULL,
                                             cod_contenido_restaurante   NVARCHAR(MAX)         NULL,
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
-- ============================================================
-- Tabla: clicks_contenidos_restaurantes
-- Registra los clicks de clientes en contenidos promocionales
-- ============================================================
CREATE TABLE dbo.clicks_contenidos_restaurantes (
                                                    nro_restaurante      INT            NOT NULL, -- (FK)
                                                    nro_idioma           INT            NOT NULL, -- (FK)
                                                    nro_contenido        INT            NOT NULL, -- (FK)
                                                    nro_click            INT            NOT NULL,
                                                    fecha_hora_registro  DATETIME       NOT NULL DEFAULT GETDATE(),
                                                    nro_cliente          INT            NULL,     -- (FK) - Puede ser NULL si el click es anónimo
                                                    costo_click          DECIMAL(12,2)  NULL,
                                                    notificado           BIT            NOT NULL DEFAULT (0),
                                                    CONSTRAINT PK_clicks_contenidos_restaurantes
                                                        PRIMARY KEY (nro_restaurante, nro_idioma, nro_contenido, nro_click),
                                                    CONSTRAINT FK_clicks_contenido
                                                        FOREIGN KEY (nro_restaurante, nro_idioma, nro_contenido)
                                                            REFERENCES dbo.contenidos_restaurantes (nro_restaurante, nro_idioma, nro_contenido),
                                                    CONSTRAINT FK_clicks_cliente
                                                        FOREIGN KEY (nro_cliente) REFERENCES dbo.clientes (nro_cliente)
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



-- 🔹 Provincias
INSERT INTO dbo.provincias (nom_provincia)
VALUES (N'Córdoba'), (N'Santa Fe'), (N'Buenos Aires');
GO

-- 🔹 Localidades
-- Córdoba
INSERT INTO dbo.localidades (nom_localidad, cod_provincia)
VALUES (N'Córdoba Capital', 1),
       (N'Villa María', 1),
       (N'Río Cuarto', 1);

-- Santa Fe
INSERT INTO dbo.localidades (nom_localidad, cod_provincia)
VALUES (N'Santa Fe Capital', 2),
       (N'Rosario', 2);

-- Buenos Aires
INSERT INTO dbo.localidades (nom_localidad, cod_provincia)
VALUES (N'La Plata', 3),
       (N'Mar del Plata', 3);
GO


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
(nro_restaurante, nro_idioma, nro_sucursal, contenido_promocional, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante)
VALUES
    (1, 1, 1, N'2x1 en pastas miércoles', N'https://ejemplo/imagen1.jpg', N'Promo semanal', '2025-09-01', '2025-12-31', 0.10, N'CONT-001');
GO
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



-- estados_reservas
INSERT INTO dbo.estados_reservas (cod_estado, nom_estado) VALUES
                                                              (1, N'Confirmada'),
                                                              (2, N'Cancelada');

-- idiomas_estados
INSERT INTO dbo.idiomas_estados (cod_estado, nro_idioma, estado) VALUES
                                                                     (1, 1, N'Confirmada'),
                                                                     (2, 1, N'Cancelada');


-- costos
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto) VALUES
    (N'CLICK', '2025-09-01', '2025-12-31', 0.10);
GO

CREATE OR ALTER PROCEDURE registrar_cliente
    @apellido       NVARCHAR(120),
    @nombre         NVARCHAR(120),
    @correo         NVARCHAR(255),
    @clave          NVARCHAR(255),
    @telefonos      NVARCHAR(100) = NULL,
    @nom_localidad  NVARCHAR(120),
    @nom_provincia  NVARCHAR(120)
    AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @cod_provincia INT;
    DECLARE @nro_localidad INT;

    IF EXISTS (SELECT 1 FROM dbo.clientes WHERE correo = @correo)
BEGIN
        RAISERROR('El correo ya está registrado.', 16, 1);
        RETURN;
END;

SELECT @cod_provincia = cod_provincia
FROM dbo.provincias
WHERE LOWER(nom_provincia) COLLATE Latin1_General_CI_AI = LOWER(@nom_provincia) COLLATE Latin1_General_CI_AI;

IF @cod_provincia IS NULL
BEGIN
INSERT INTO dbo.provincias (nom_provincia)
VALUES (@nom_provincia);
SET @cod_provincia = SCOPE_IDENTITY();
END;

SELECT @nro_localidad = nro_localidad
FROM dbo.localidades
WHERE LOWER(nom_localidad) COLLATE Latin1_General_CI_AI = LOWER(@nom_localidad) COLLATE Latin1_General_CI_AI
  AND cod_provincia = @cod_provincia;

IF @nro_localidad IS NULL
BEGIN
INSERT INTO dbo.localidades (nom_localidad, cod_provincia)
VALUES (@nom_localidad, @cod_provincia);
SET @nro_localidad = SCOPE_IDENTITY();
END;

    -- ✅ Hashear la clave en SHA-256 (en mayúsculas)
    DECLARE @clave_hash NVARCHAR(64);
    SET @clave_hash = UPPER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', @clave), 2));

INSERT INTO dbo.clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
VALUES (@apellido, @nombre, @clave_hash, @correo, @telefonos, @nro_localidad, 1);

SELECT SCOPE_IDENTITY() AS nro_cliente_creado;
END;
GO


--select * from clientes
CREATE OR ALTER PROCEDURE dbo.login_cliente
    @correo NVARCHAR(255),
    @clave NVARCHAR(255),
    @login_valido INT OUTPUT
    AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @clave_hash NVARCHAR(64);
    SET @clave_hash = UPPER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', @clave), 2));

    IF EXISTS (
        SELECT 1
        FROM dbo.clientes
        WHERE correo = @correo
          AND clave = @clave_hash
          AND habilitado = 1
    )
        SET @login_valido = 1;
ELSE
        SET @login_valido = 0;
END;
GO


CREATE OR ALTER PROCEDURE dbo.recomendar_restaurantes
    @tipoComida NVARCHAR(120) = NULL,
    @ciudad NVARCHAR(120) = NULL,
    @provincia NVARCHAR(120) = NULL,
    @momentoDelDia NVARCHAR(20) = NULL,     -- ej: 'mañana', 'mediodía', 'tarde', 'noche'
    @rangoPrecio NVARCHAR(50) = NULL,       -- ej: 'bajo', 'medio', 'alto'
    @cantidadPersonas INT = NULL,
    @tieneMenores NVARCHAR(10) = NULL,      -- 'si', 'no'
    @restriccionesAlimentarias NVARCHAR(120) = NULL,
    @preferenciasAmbiente NVARCHAR(120) = NULL,
    @nroCliente INT = NULL                  -- opcional: para preferencias del cliente
    AS
BEGIN
    SET NOCOUNT ON;

    /* ============================================================
       1) Normalizar parámetros de texto
       ============================================================*/
    SET @tipoComida = NULLIF(LTRIM(RTRIM(@tipoComida)), '');
    SET @ciudad = NULLIF(LTRIM(RTRIM(@ciudad)), '');
    SET @provincia = NULLIF(LTRIM(RTRIM(@provincia)), '');
    SET @momentoDelDia = NULLIF(LTRIM(RTRIM(@momentoDelDia)), '');
    SET @rangoPrecio = NULLIF(LTRIM(RTRIM(@rangoPrecio)), '');
    SET @tieneMenores = NULLIF(LTRIM(RTRIM(@tieneMenores)), '');
    SET @restriccionesAlimentarias = NULLIF(LTRIM(RTRIM(@restriccionesAlimentarias)), '');
    SET @preferenciasAmbiente = NULLIF(LTRIM(RTRIM(@preferenciasAmbiente)), '');

    /* ============================================================
       2) Determinar rango horario según momento del día
       ============================================================*/
    DECLARE @horaDesde TIME(0) = NULL;
    DECLARE @horaHasta TIME(0) = NULL;

    IF @momentoDelDia IS NOT NULL
BEGIN
        IF LOWER(@momentoDelDia) LIKE '%mañ%' BEGIN SET @horaDesde = '08:00'; SET @horaHasta = '11:59'; END;
        IF LOWER(@momentoDelDia) LIKE '%med%' BEGIN SET @horaDesde = '12:00'; SET @horaHasta = '15:30'; END;
        IF LOWER(@momentoDelDia) LIKE '%tar%' BEGIN SET @horaDesde = '16:00'; SET @horaHasta = '18:59'; END;
        IF LOWER(@momentoDelDia) LIKE '%noch%' BEGIN SET @horaDesde = '19:00'; SET @horaHasta = '23:59'; END;
END

    /* ============================================================
       3) Determinar provincia si no fue pasada pero sí la ciudad
       ============================================================*/
    IF @provincia IS NULL AND @ciudad IS NOT NULL
BEGIN
SELECT TOP 1 @provincia = p.nom_provincia
FROM dbo.localidades l
         INNER JOIN dbo.provincias p ON l.cod_provincia = p.cod_provincia
WHERE LOWER(l.nom_localidad) COLLATE Latin1_General_CI_AI LIKE '%' + LOWER(@ciudad) + '%';
END;

    /* ============================================================
       4) Buscar coincidencias principales
       ============================================================*/
    ;WITH candidatos AS (
    SELECT
        r.nro_restaurante,
        r.razon_social,
        s.nro_sucursal,
        s.nom_sucursal,
        l.nom_localidad,
        p.nom_provincia,
        s.total_comensales,
        z.cod_zona,
        z.desc_zona,
        z.cant_comensales,
        z.permite_menores,
        t.hora_desde,
        t.hora_hasta,

        /* --- PUNTOS DE COINCIDENCIA (score parcial) --- */
        CASE
            WHEN @tipoComida IS NOT NULL AND dp.nom_valor_dominio COLLATE Latin1_General_CI_AI LIKE '%' + @tipoComida + '%' THEN 1 ELSE 0 END
            + CASE
                  WHEN @preferenciasAmbiente IS NOT NULL AND dp.nom_valor_dominio COLLATE Latin1_General_CI_AI LIKE '%' + @preferenciasAmbiente + '%' THEN 1 ELSE 0 END
            + CASE
                  WHEN @restriccionesAlimentarias IS NOT NULL AND dp.nom_valor_dominio COLLATE Latin1_General_CI_AI LIKE '%' + @restriccionesAlimentarias + '%' THEN 1 ELSE 0 END
            AS coincidencias

    FROM dbo.restaurantes r
             INNER JOIN dbo.sucursales_restaurantes s
                        ON r.nro_restaurante = s.nro_restaurante
             INNER JOIN dbo.localidades l
                        ON s.nro_localidad = l.nro_localidad
             INNER JOIN dbo.provincias p
                        ON l.cod_provincia = p.cod_provincia
             LEFT JOIN dbo.zonas_sucursales_restaurantes z
                       ON s.nro_restaurante = z.nro_restaurante AND s.nro_sucursal = z.nro_sucursal
             LEFT JOIN dbo.turnos_sucursales_restaurantes t
                       ON s.nro_restaurante = t.nro_restaurante AND s.nro_sucursal = t.nro_sucursal
             LEFT JOIN dbo.preferencias_restaurantes prr
                       ON r.nro_restaurante = prr.nro_restaurante
             LEFT JOIN dbo.dominio_categorias_preferencias dp
                       ON prr.cod_categoria = dp.cod_categoria
                           AND prr.nro_valor_dominio = dp.nro_valor_dominio

    WHERE
        /* -------- FILTROS DINÁMICOS -------- */
        (@ciudad IS NULL OR LOWER(l.nom_localidad) COLLATE Latin1_General_CI_AI LIKE '%' + LOWER(@ciudad) + '%')
      AND (@provincia IS NULL OR LOWER(p.nom_provincia) COLLATE Latin1_General_CI_AI LIKE '%' + LOWER(@provincia) + '%')
      AND (@horaDesde IS NULL OR @horaHasta IS NULL OR (t.hora_desde <= @horaHasta AND t.hora_hasta >= @horaDesde))
      AND (@tieneMenores IS NULL
        OR (@tieneMenores = 'si' AND z.permite_menores = 1)
        OR (@tieneMenores = 'no' AND z.permite_menores = 0))
      AND (@cantidadPersonas IS NULL OR z.cant_comensales >= @cantidadPersonas OR s.total_comensales >= @cantidadPersonas)
)
     SELECT TOP 10
        nro_restaurante,
             razon_social,
            nom_sucursal,
            nom_localidad,
            nom_provincia,
            MAX(desc_zona) AS desc_zona,
            MIN(hora_desde) AS hora_desde,
            MAX(hora_hasta) AS hora_hasta,
            MAX(coincidencias) AS coincidencias
     FROM candidatos
     GROUP BY nro_restaurante, razon_social, nom_sucursal, nom_localidad, nom_provincia
     ORDER BY MAX(coincidencias) DESC, razon_social;
END;
GO


/*EXEC dbo.recomendar_restaurantes
    @tipoComida = N'italiana',
   @ciudad = N'Córdoba',
   @momentoDelDia = N'noche',
    @rangoPrecio = N'bajo',
    @cantidadPersonas = 4,
   -- @tieneMenores = N'no',
    @preferenciasAmbiente = N'con amigos';*/



	/*===========================================================
   RESTAURANTE DE PRUEBA QUE COINCIDA CON LOS FILTROS

	ATENCION ESTO LO CREE PARA COMPROBAR QUE REALMENTE ANDE, SE SUPONE QUE LA INFO DE LOS RESTAURANTES LAS TRAIGO DESDE LOS ENDPOINT
   =========================================================== */

-- 1️⃣ Restaurante
INSERT INTO dbo.restaurantes (nro_restaurante, razon_social, cuit)
VALUES (2, N'Ristorante Italiano Córdoba', N'30-98765432-1');

-- 2️⃣ Sucursal del restaurante
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (2, 1, N'Sucursal Nueva Córdoba', N'Av. Hipólito Yrigoyen', 1250, N'Nueva Córdoba', 1, N'5000', N'351-666-2222', 80, 10, N'NCOR');

-- 3️⃣ Zona del restaurante
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (2, 1, 1, N'Salón para adultos', 60, 0, 1);

-- 4️⃣ Turno nocturno (para “noche”)
INSERT INTO dbo.turnos_sucursales_restaurantes
(nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
VALUES
    (2, 1, '20:00', '23:30', 1);

-- 5️⃣ Preferencia del restaurante: tipo de comida italiana
-- categoría 1 = “Tipo de cocina” (según tus inserts iniciales)
-- dominio 1 = “Italiana”
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal)
VALUES
    (2, 1, 1, 1, N'Cocina italiana gourmet', 1);

-- 6️⃣ Restricción alimentaria (opcional)
-- categoría 2 = “Restricciones alimentarias”
INSERT INTO dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio)
VALUES (2, 3, N'Vegetariana');

INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal)
VALUES
    (2, 2, 3, 1, N'Ofrece opciones vegetarianas', 1);


-- 7️⃣ Sucursal alternativa en otra provincia (Rosario, Santa Fe)
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (2, 2, N'Sucursal Rosario Centro', N'Av. Pellegrini', 850, N'Centro',
     (SELECT nro_localidad FROM dbo.localidades WHERE nom_localidad = N'Rosario'),
     N'2000', N'341-555-2222', 70, 10, N'ROS');

-- 8️⃣ Zona en Rosario
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (2, 2, 1, N'Salón familiar', 50, 1, 1);

-- 9️⃣ Turno en Rosario (también nocturno)
INSERT INTO dbo.turnos_sucursales_restaurantes
(nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
VALUES
    (2, 2, '20:00', '23:30', 1);

-- 10️⃣ Preferencias iguales (para que no influya otro factor)
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal)
VALUES
    (2, 1, 1, 2, N'Cocina italiana en Rosario', 2);



/* ============================================================
   Procedimiento: get_datos_restaurante_promocion
   Descripción: Devuelve los datos básicos del restaurante y
                sucursal para generar el contenido promocional.
   ============================================================*/
go
CREATE OR ALTER PROCEDURE dbo.get_contenidos_a_generar
    AS
BEGIN
    SET NOCOUNT ON;

SELECT
    nro_contenido,
    nro_restaurante,
    ISNULL(nro_sucursal, 0) AS nro_sucursal,
    ISNULL(nro_idioma, 1) AS nro_idioma,
    contenido_a_publicar,
    ISNULL(imagen_promocional, '') AS imagen_promocional,
    ISNULL(costo_click, 0) AS costo_click
FROM dbo.contenidos_restaurantes
WHERE contenido_promocional IS NULL;
END
GO


CREATE OR ALTER PROCEDURE dbo.actualizar_contenido_promocional
    @nro_contenido INT,
    @contenido_promocional NVARCHAR(MAX),
    @duracion_horas INT = 24  -- por defecto 24 horas de vigencia
    AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @fecha_inicio DATETIME = GETDATE();
    DECLARE @fecha_fin DATETIME = DATEADD(HOUR, @duracion_horas, @fecha_inicio);

UPDATE dbo.contenidos_restaurantes
SET
    contenido_promocional = @contenido_promocional,
    fecha_ini_vigencia = @fecha_inicio,
    fecha_fin_vigencia = @fecha_fin
WHERE nro_contenido = @nro_contenido;
END
GO




-- 🍝 Restaurante 1 - Sucursal 1
INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, contenido_a_publicar, imagen_promocional, costo_click, cod_contenido_restaurante)
VALUES
(1, 2, 1,
 N'Noche italiana de pastas caseras y vinos locales. Celebrá la tradición en nuestro salón principal con música en vivo.',
 N'https://example.com/imagenes/pasta-night.jpg',
 0.15,
 N'CONT-R1S1-PASTAS');

-- 🥩 Restaurante 2 - Sucursal 1
INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, contenido_a_publicar, imagen_promocional, costo_click, cod_contenido_restaurante)
VALUES
    (2, 1, 1,
     N'Gran asado familiar los domingos. Parrillada libre con cortes premium y postres artesanales. valor por persona 3000pesos incluye bebida',
     N'https://example.com/imagenes/asado-familiar.jpg',
     0.18,
     N'CONT-R2S1-ASADO');

-- 🍣 Restaurante 2 - Sucursal 2
INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, contenido_a_publicar, imagen_promocional, costo_click, cod_contenido_restaurante)
VALUES
    (2, 1, 2,
     N'Noche de sushi libre y tragos de autor. Disfrutá sabores orientales en un ambiente moderno y relajado.',
     N'https://example.com/imagenes/sushi-night.jpg',
     0.20,
     N'CONT-R2S2-SUSHI');

select * from contenidos_restaurantes
go


/* ============================================================
Procedimiento: registrar_click_contenido
Descripción: Registra un click en un contenido promocional
       de un restaurante. El nro_click se genera
       automáticamente de forma incremental.
============================================================*/
CREATE OR ALTER PROCEDURE dbo.registrar_click_contenido
    @nro_restaurante INT,
    @nro_idioma INT,
    @nro_contenido INT,
    @nro_cliente INT = NULL,        -- Opcional: NULL si es click anónimo
    @costo_click DECIMAL(12,2) = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @nuevo_nro_click INT;
    DECLARE @ErrorMessage NVARCHAR(4000);

BEGIN TRY
BEGIN TRANSACTION;

        -- Verificar que el contenido existe
        IF NOT EXISTS (
            SELECT 1
            FROM dbo.contenidos_restaurantes
            WHERE nro_restaurante = @nro_restaurante
              AND nro_idioma = @nro_idioma
              AND nro_contenido = @nro_contenido
        )
BEGIN
            RAISERROR('El contenido especificado no existe.', 16, 1);
            RETURN;
END;

        -- Si no se especifica costo_click, tomar el del contenido
        IF @costo_click IS NULL
BEGIN
SELECT @costo_click = costo_click
FROM dbo.contenidos_restaurantes
WHERE nro_restaurante = @nro_restaurante
  AND nro_idioma = @nro_idioma
  AND nro_contenido = @nro_contenido;
END;

        -- Obtener el siguiente número de click para este contenido
SELECT @nuevo_nro_click = ISNULL(MAX(nro_click), 0) + 1
FROM dbo.clicks_contenidos_restaurantes
WHERE nro_restaurante = @nro_restaurante
  AND nro_idioma = @nro_idioma
  AND nro_contenido = @nro_contenido;

-- Insertar el registro de click
INSERT INTO dbo.clicks_contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_contenido, nro_click,
 fecha_hora_registro, nro_cliente, costo_click, notificado)
VALUES
    (@nro_restaurante, @nro_idioma, @nro_contenido, @nuevo_nro_click,
     GETDATE(), @nro_cliente, @costo_click, 0);

COMMIT TRANSACTION;

-- Devolver el número de click generado
SELECT @nuevo_nro_click AS nro_click_generado,
       GETDATE() AS fecha_hora_registro;

END TRY
BEGIN CATCH
IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        SET @ErrorMessage = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
END CATCH
END;
GO
/* PAra probar el procedimimento
INSERT INTO dbo.clientes (apellido, nombre, clave, correo, telefonos, nro_localidad, habilitado)
VALUES 
    (N'Pérez', N'Juan', 
     UPPER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', 'password123'), 2)), 
     N'juan.perez@example.com', 
     N'351-123-4567', 
     1, -- Córdoba Capital
     1);
go

EXEC dbo.registrar_click_contenido 
    @nro_restaurante = 1,
    @nro_idioma = 1,
    @nro_contenido = 1,
    @nro_cliente = 1,
    @costo_click = 0.10;
    go
EXEC dbo.registrar_click_contenido 
    @nro_restaurante = 1,
    @nro_idioma = 1,
    @nro_contenido = 1;
    go
select * from dbo.clicks_contenidos_restaurantes
go */



go
CREATE OR ALTER PROCEDURE dbo.get_promociones
    @nro_restaurante INT = NULL,
    @nro_sucursal INT = NULL
    AS
BEGIN
    SET NOCOUNT ON;

SELECT
    -- cr.nro_contenido,
    cr.nro_restaurante,
    cr.nro_sucursal,
    cr.contenido_promocional,
    cr.fecha_ini_vigencia,
    cr.fecha_fin_vigencia
-- cr.nro_idioma,
-- cr.costo_click,
--cr.cod_contenido_restaurante
FROM dbo.contenidos_restaurantes cr
WHERE
    (@nro_restaurante IS NULL OR cr.nro_restaurante = @nro_restaurante)
  AND (@nro_sucursal IS NULL OR cr.nro_sucursal = @nro_sucursal)
ORDER BY
    cr.nro_restaurante,
    cr.nro_sucursal,
    cr.nro_contenido;
END
GO
--EXEC dbo.get_promociones @nro_restaurante = 2, @nro_sucursal = 2;
CREATE OR ALTER PROCEDURE dbo.get_restaurante_info
    @nro_restaurante INT
    AS
BEGIN
    SET NOCOUNT ON;

SELECT
    nro_restaurante,
    razon_social
FROM
    dbo.restaurantes
WHERE
    nro_restaurante = @nro_restaurante;
END;
GO