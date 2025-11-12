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
(1, N'El millonario', N'30-91245678-9');
INSERT INTO dbo.restaurantes (nro_restaurante, razon_social, cuit) VALUES
    (2, N'El gallinero', N'31-91245678-9');
INSERT INTO dbo.restaurantes (nro_restaurante, razon_social, cuit) VALUES
    (3, N'El monumental', N'32-91245678-9');

-- sucursales_restaurantes
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (1, 1, N'Casa Central El millonario', N'Av. Siempreviva', 742, N'Centro', 1, N'5000', N'351-555-1111', 50, 15, N'CEN');
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (1, 2, N'Sucursal norte El millonario', N'Av. Siempreviva', 742, N'Centro', 1, N'5000', N'351-555-1111', 50, 15, N'NOR MILLO');
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (2, 1, N'Casa Central El gallinero', N'Av. Siempreviva', 742, N'Centro', 1, N'5000', N'351-555-1111', 50, 15, N'CEN GALLI');
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (2, 2, N'Sucursal sur El gallinero', N'Av. Siempreviva', 742, N'Centro', 1, N'5000', N'351-555-1111', 50, 15, N'SUR GALLI');
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (3, 1, N'Casa Central El monumental', N'Av. Siempreviva', 742, N'Centro', 1, N'5000', N'351-555-1111', 50, 15, N'CEN MONU');
INSERT INTO dbo.sucursales_restaurantes
(nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio, nro_localidad, cod_postal, telefonos, total_comensales, min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES
    (3, 2, N'Sucursal norte El monumental', N'Av. Siempreviva', 742, N'Centro', 1, N'5000', N'351-555-1111', 50, 15, N'NOR MONU');


-- idiomas
INSERT INTO dbo.idiomas (nro_idioma, nom_idioma, cod_idioma) VALUES
                                                                 (1, N'Español', N'es-AR'),
                                                                 (2, N'English', N'en-US');
-- contenidos_restaurantes
-----------------------------
-- Restaurante 1 (contenidos 1..6)
-----------------------------
INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante) VALUES
    (1, 1, 1, N'https://img.example.com/r1/s1_trad_abue_sg.jpg',
     N'Menú Tradicional "Abuela" (Sin gluten): empanadas de carne al horno con tapa de maíz + sorrentinos de ricota y nuez en salsa fileto. Precio medio. Ideal para compartir.',
     '2025-09-01', '2025-12-31', 0.10, N'1-1');

INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante) VALUES
    (1, 1, 1, N'https://img.example.com/r1/s1_arg_ita_sg_combo.jpg',
     N'Combo Argentino & Italiano (Sin gluten): milanesa napolitana con papas al horno + penne rigate al pesto. Estilo tradicional, porciones generosas, precio medio.',
     '2025-09-01', '2025-12-31', 0.10, N'2-1');

INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante) VALUES
    (1, 1, 1, N'https://img.example.com/r1/s1_pastas_sg.jpg',
     N'Noche de Pastas Caseras (opción Sin gluten): tallarines amasados a la vista con bolognesa o tuco de cocción lenta + copa de vino de la casa. Ambiente tradicional.',
     '2025-09-01', '2025-12-31', 0.10, N'3-1');

INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante) VALUES
    (1, 1, 2, N'https://img.example.com/r1/s2_tacos_veg_premium.jpg',
     N'Tacos Degustación Premium (Vegetarianos): set de 6 tacos (hongos asados, calabaza especiada, frijoles y queso), salsas caseras y guacamole. Estilo casual, experiencia gourmet.',
     '2025-09-01', '2025-12-31', 0.10, N'4-1');

INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante) VALUES
    (1, 1, 2,  N'https://img.example.com/r1/s2_burrito_bowl_veg.jpg',
     N'Burrito Bowl Verde (Vegetariano): arroz cilantro-lima, mix de hojas, porotos negros, fajitas de verduras, pico de gallo y crema ácida. Presentación premium, servicio casual.',
     '2025-09-01', '2025-12-31', 0.10, N'5-1');

INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante) VALUES
    (1, 1, 2,N'https://img.example.com/r1/s2_mex_premium_dinner.jpg',
     N'Cena Mexicana Premium: enchiladas rojas vegetarianas + maridaje con tequila/agua fresca. Estilo casual chic, producto de alta calidad, ideal para celebración.',
     '2025-09-01', '2025-12-31', 0.10, N'6-1');
INSERT INTO dbo.contenidos_restaurantes
(nro_restaurante, nro_idioma, nro_sucursal, imagen_promocional, contenido_a_publicar, fecha_ini_vigencia, fecha_fin_vigencia, costo_click, cod_contenido_restaurante) VALUES
    (2, 1, 2,N'https://img.example.com/r1/s2_mex_premium_dinner.jpg',
     N'CHORIPAN',
     '2025-09-01', '2025-12-31', 0.10, N'1-2');

select * from contenidos_restaurantes
select * from clicks_contenidos_restaurantes


-- Resto 1
    INSERT INTO dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado) VALUES
    (1, 1, '20:00', '21:30', 1),
    (1, 1, '21:30', '23:00', 1),
    (1, 1, '23:00', '00:30', 1),
    (1, 1, '00:30', '02:00', 1);

INSERT INTO dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado) VALUES
                                                                                                                       (1, 2, '20:00', '21:30', 1),
                                                                                                                       (1, 2, '21:30', '23:00', 1),
                                                                                                                       (1, 2, '23:00', '00:30', 1),
                                                                                                                       (1, 2, '00:30', '02:00', 1);

-- Resto 2
INSERT INTO dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado) VALUES
                                                                                                                       (2, 1, '20:00', '21:30', 1),
                                                                                                                       (2, 1, '21:30', '23:00', 1),
                                                                                                                       (2, 1, '23:00', '00:30', 1),
                                                                                                                       (2, 1, '00:30', '02:00', 1);

INSERT INTO dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado) VALUES
                                                                                                                       (2, 2, '20:00', '21:30', 1),
                                                                                                                       (2, 2, '21:30', '23:00', 1),
                                                                                                                       (2, 2, '23:00', '00:30', 1),
                                                                                                                       (2, 2, '00:30', '02:00', 1);

-- Resto 3
INSERT INTO dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado) VALUES
                                                                                                                       (3, 1, '20:00', '21:30', 1),
                                                                                                                       (3, 1, '21:30', '23:00', 1),
                                                                                                                       (3, 1, '23:00', '00:30', 1),
                                                                                                                       (3, 1, '00:30', '02:00', 1);

INSERT INTO dbo.turnos_sucursales_restaurantes (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado) VALUES
                                                                                                                       (3, 2, '20:00', '21:30', 1),
                                                                                                                       (3, 2, '21:30', '23:00', 1),
                                                                                                                       (3, 2, '23:00', '00:30', 1),
                                                                                                                       (3, 2, '00:30', '02:00', 1);

-- zonas_sucursales_restaurantes
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (1, 1, 1, N'Salón principal', 40, 1, 1);
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (1, 2, 1, N'Salón principal', 40, 1, 1);
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (2, 1, 1, N'Salón principal', 40, 1, 1);
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (2, 2, 1, N'Salón principal', 40, 1, 1);
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (3, 1, 1, N'Salón principal', 40, 1, 1);
INSERT INTO dbo.zonas_sucursales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES
    (3, 2, 1, N'Salón principal', 40, 1, 1);

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


/* ===========================================================
   1) CATEGORÍAS
   ===========================================================*/
INSERT INTO dbo.categorias_preferencias (cod_categoria, nom_categoria) VALUES
(1, N'Tipo de cocina'),
(2, N'Especialidades alimentarias'),
(3, N'Estilo');

-- Etiquetas de categorías (idioma: es-AR -> nro_idioma=1)
INSERT INTO dbo.idiomas_categorias_preferencias
(cod_categoria, nro_idioma, categoria, desc_categoria) VALUES
                                                           (1, 1, N'Tipo de cocina',             N'Tradición o escuela culinaria del restaurante'),
                                                           (2, 1, N'Especialidades alimentarias',N'Preferencias o restricciones alimentarias'),
                                                           (3, 1, N'Estilo',                     N'Formato/estilo de servicio o ambientación');


/* ===========================================================
   2) DOMINIOS por categoría
   ===========================================================*/

-- Cat=1  Tipo de cocina
INSERT INTO dbo.dominio_categorias_preferencias
(cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES
                                                          (1,  1, N'Italiana'),
                                                          (1,  2, N'Mexicana'),
                                                          (1,  3, N'Española'),
                                                          (1,  4, N'Francesa'),
                                                          (1,  5, N'Japonesa'),
                                                          (1,  6, N'China'),
                                                          (1,  7, N'Tailandesa'),
                                                          (1,  8, N'India'),
                                                          (1,  9, N'Mediterránea'),
                                                          (1, 10, N'Argentina'),
                                                          (1, 11, N'Peruana'),
                                                          (1, 12, N'Árabe / Medio Oriente'),
                                                          (1, 13, N'Fusión'),
                                                          (1, 14, N'Internacional');

-- Etiquetas Cat=1 (es-AR)
INSERT INTO dbo.idiomas_dominio_cat_preferencias
(cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio, desc_valor_dominio) VALUES
                                                                                      (1,  1, 1, N'Italiana',                 NULL),
                                                                                      (1,  2, 1, N'Mexicana',                 NULL),
                                                                                      (1,  3, 1, N'Española',                 NULL),
                                                                                      (1,  4, 1, N'Francesa',                 NULL),
                                                                                      (1,  5, 1, N'Japonesa',                 NULL),
                                                                                      (1,  6, 1, N'China',                    NULL),
                                                                                      (1,  7, 1, N'Tailandesa',               NULL),
                                                                                      (1,  8, 1, N'India',                    NULL),
                                                                                      (1,  9, 1, N'Mediterránea',             NULL),
                                                                                      (1, 10, 1, N'Argentina',                NULL),
                                                                                      (1, 11, 1, N'Peruana',                  NULL),
                                                                                      (1, 12, 1, N'Árabe / Medio Oriente',    NULL),
                                                                                      (1, 13, 1, N'Fusión',                   NULL),
                                                                                      (1, 14, 1, N'Internacional',            NULL);


-- Cat=2  Especialidades alimentarias
INSERT INTO dbo.dominio_categorias_preferencias
(cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES
                                                          (2, 1, N'Vegetariana'),
                                                          (2, 2, N'Vegana'),
                                                          (2, 3, N'Sin gluten / Celíaco'),
                                                          (2, 4, N'Sin lactosa'),
                                                          (2, 5, N'Baja en calorías'),
                                                          (2, 6, N'Orgánica'),
                                                          (2, 7, N'Diabéticos (sin azúcar añadida)');

-- Etiquetas Cat=2 (es-AR)
INSERT INTO dbo.idiomas_dominio_cat_preferencias
(cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio, desc_valor_dominio) VALUES
                                                                                      (2, 1, 1, N'Vegetariana',                         NULL),
                                                                                      (2, 2, 1, N'Vegana',                              NULL),
                                                                                      (2, 3, 1, N'Sin gluten / Celíaco',                NULL),
                                                                                      (2, 4, 1, N'Sin lactosa',                         NULL),
                                                                                      (2, 5, 1, N'Baja en calorías',                    NULL),
                                                                                      (2, 6, 1, N'Orgánica',                            NULL),
                                                                                      (2, 7, 1, N'Diabéticos (sin azúcar añadida)',     NULL);


-- Cat=3  Estilo
INSERT INTO dbo.dominio_categorias_preferencias
(cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES
                                                          (3,  1, N'Gourmet'),
                                                          (3,  2, N'Casual'),
                                                          (3,  3, N'Comida rápida / Fast food'),
                                                          (3,  4, N'Buffet libre'),
                                                          (3,  5, N'Bistró'),
                                                          (3,  6, N'Food truck'),
                                                          (3,  7, N'Restaurante tradicional'),
                                                          (3,  8, N'Bar / Tapas'),
                                                          (3,  9, N'Cafetería'),
                                                          (3, 10, N'Delivery'),
                                                          (3, 11, N'Fine dining');

-- Etiquetas Cat=3 (es-AR)
INSERT INTO dbo.idiomas_dominio_cat_preferencias
(cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio, desc_valor_dominio) VALUES
                                                                                      (3,  1, 1, N'Gourmet',                 NULL),
                                                                                      (3,  2, 1, N'Casual',                  NULL),
                                                                                      (3,  3, 1, N'Comida rápida / Fast food', NULL),
                                                                                      (3,  4, 1, N'Buffet libre',            NULL),
                                                                                      (3,  5, 1, N'Bistró',                  NULL),
                                                                                      (3,  6, 1, N'Food truck',              NULL),
                                                                                      (3,  7, 1, N'Restaurante tradicional', NULL),
                                                                                      (3,  8, 1, N'Bar / Tapas',             NULL),
                                                                                      (3,  9, 1, N'Cafetería',               NULL),
                                                                                      (3, 10, 1, N'Delivery',                NULL),
                                                                                      (3, 11, 1, N'Fine dining',             NULL);

-- Categoría
INSERT INTO dbo.categorias_preferencias (cod_categoria, nom_categoria) VALUES
    (4, N'Nivel de precio');

-- Etiqueta de categoría (idioma es-AR = 1)
INSERT INTO dbo.idiomas_categorias_preferencias
(cod_categoria, nro_idioma, categoria, desc_categoria) VALUES
    (4, 1, N'Nivel de precio', N'Rango de precios percibido del restaurante');

-- Dominios (1..4)
INSERT INTO dbo.dominio_categorias_preferencias
(cod_categoria, nro_valor_dominio, nom_valor_dominio) VALUES
                                                          (4, 1, N'Económico / Bajo'),
                                                          (4, 2, N'Medio'),
                                                          (4, 3, N'Alto / Premium'),
                                                          (4, 4, N'De lujo');

-- Etiquetas de dominios (idioma es-AR = 1)
INSERT INTO dbo.idiomas_dominio_cat_preferencias
(cod_categoria, nro_valor_dominio, nro_idioma, valor_dominio, desc_valor_dominio) VALUES
                                                                                      (4, 1, 1, N'Económico / Bajo', NULL),
                                                                                      (4, 2, 1, N'Medio',            NULL),
                                                                                      (4, 3, 1, N'Alto / Premium',   NULL),
                                                                                      (4, 4, 1, N'De lujo',          NULL);

select * from categorias_preferencias
    go
select * from dominio_categorias_preferencias
    go
select * from preferencias_restaurantes where nro_restaurante=3

    go

--------------------------------------------------------------
-- RESTAURANTE 1
-- Suc 1: Cocina Argentina + Italiana; Especialidad Sin gluten; Estilo Tradicional; Precio Medio
-- Suc 2: Cocina Mexicana; Especialidad Vegetariana; Estilo Casual; Precio Alto/Premium
--------------------------------------------------------------

-- Cat 1: Tipo de cocina
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
    (1, 1, 10, 1, N'Cocina argentina en sucursal 1', 1),   -- Argentina
    (1, 1,  1, 2, N'Cocina italiana en sucursal 1', 1),    -- Italiana
    (1, 1,  2, 3, N'Cocina mexicana en sucursal 2', 2);    -- Mexicana

-- Cat 2: Especialidades alimentarias
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (1, 2, 3, 1, N'Platos Sin gluten / Celíaco (sucursal 1)', 1),
                                                                                                      (1, 2, 1, 2, N'Opciones vegetarianas (sucursal 2)', 2);

-- Cat 3: Estilo
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (1, 3, 7, 1, N'Estilo restaurante tradicional (sucursal 1)', 1), -- Restaurante tradicional
                                                                                                      (1, 3, 2, 2, N'Estilo casual (sucursal 2)', 2);                  -- Casual

-- Cat 4: Nivel de precio
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (1, 4, 2, 1, N'Rango de precio medio (sucursal 1)', 1),          -- Medio
                                                                                                      (1, 4, 3, 2, N'Rango alto/premium (sucursal 2)', 2);             -- Alto / Premium


--------------------------------------------------------------
-- RESTAURANTE 2
-- Suc 1: Cocina Italiana + Mediterránea; Especialidad Sin lactosa; Estilo Bistró; Precio Económico
-- Suc 2: Cocina Internacional; Especialidad Vegana; Estilo Delivery; Precio Medio
--------------------------------------------------------------

-- Cat 1: Tipo de cocina
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (2, 1,  1, 1, N'Cocina italiana en sucursal 1', 1),     -- Italiana
                                                                                                      (2, 1, 9, 2, N'Cocina mediterránea en sucursal 1', 1), -- Mediterránea (en tu carga fue nro 9; si usaste 9, cambia aquí)
                                                                                                      (2, 1, 14, 3, N'Cocina internacional en sucursal 2', 2);-- Internacional (en tu carga fue nro 14; ajusta si difiere)

-- Cat 2: Especialidades alimentarias
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (2, 2, 4, 1, N'Opciones sin lactosa (sucursal 1)', 1),
                                                                                                      (2, 2, 2, 2, N'Menú vegano (sucursal 2)', 2);

-- Cat 3: Estilo
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (2, 3, 5, 1, N'Estilo bistró (sucursal 1)', 1),     -- Bistró
                                                                                                      (2, 3,10, 2, N'Estilo delivery (sucursal 2)', 2);   -- Delivery

-- Cat 4: Nivel de precio
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (2, 4, 1, 1, N'Rango económico/bajo (sucursal 1)', 1), -- Económico / Bajo
                                                                                                      (2, 4, 2, 2, N'Rango medio (sucursal 2)', 2);          -- Medio


--------------------------------------------------------------
-- RESTAURANTE 3
-- Suc 1: Cocina Japonesa + Peruana; Especialidad Orgánica; Estilo Fine dining; Precio De lujo
-- Suc 2: Cocina Árabe / Medio Oriente; Especialidad Baja en calorías; Estilo Bar/Tapas; Precio Medio
--------------------------------------------------------------

-- Cat 1: Tipo de cocina
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (3, 1,  5, 1, N'Cocina japonesa en sucursal 1', 1),        -- Japonesa
                                                                                                      (3, 1, 11, 2, N'Cocina peruana en sucursal 1', 1),         -- Peruana
                                                                                                      (3, 1, 12, 3, N'Árabe / Medio Oriente en sucursal 2', 2);  -- Árabe / Medio Oriente

-- Cat 2: Especialidades alimentarias
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (3, 2, 6, 1, N'Opciones orgánicas (sucursal 1)', 1),
                                                                                                      (3, 2, 5, 2, N'Baja en calorías (sucursal 2)', 2);

-- Cat 3: Estilo
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (3, 3,11, 1, N'Fine dining (sucursal 1)', 1),  -- Fine dining
                                                                                                      (3, 3, 8, 2, N'Bar / Tapas (sucursal 2)', 2);  -- Bar / Tapas

-- Cat 4: Nivel de precio
INSERT INTO dbo.preferencias_restaurantes
(nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal) VALUES
                                                                                                      (3, 4, 4, 1, N'Rango de lujo (sucursal 1)', 1), -- De lujo
                                                                                                      (3, 4, 2, 2, N'Rango medio (sucursal 2)', 2);   -- Medio




go
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




/* ============================================================
Procedimiento: registrar_click_contenido
Descripción: Registra un click en un contenido promocional
       de un restaurante. El nro_click se genera
       automáticamente de forma incremental.
============================================================*/
CREATE OR ALTER PROCEDURE dbo.registrar_click_contenido
    @cod_restaurante VARCHAR(1024),   -- <- ahora recibe el código HEX cifrado
    @nro_contenido   INT,
    @nro_cliente     INT = NULL
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @nuevo_nro_click INT;
    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @nro_idioma INT;
    DECLARE @idioma_count INT;
    DECLARE @costo_click DECIMAL(12,2);

    ------------------------------------------------------------
    -- 0) Resolver nro_restaurante real desde el código HEX
    ------------------------------------------------------------
    DECLARE @cod_rest_bin VARBINARY(1024) =
        CONVERT(VARBINARY(1024), '0x' + @cod_restaurante, 1);

    DECLARE @nro_restaurante INT;

SELECT TOP (1)
            @nro_restaurante = r.nro_restaurante
FROM dbo.restaurantes AS r
WHERE r.nro_restaurante = CONVERT(
        INT,
        CONVERT(VARCHAR(1024),
                DECRYPTBYPASSPHRASE(
                        CONVERT(VARCHAR(20), r.nro_restaurante),
                        @cod_rest_bin
                )
        )
                          );

IF @nro_restaurante IS NULL
BEGIN
        RAISERROR('Código de restaurante inválido.', 16, 1);
        RETURN;
END

BEGIN TRY
BEGIN TRANSACTION;

    /* 1) Verificar existencia y ambigüedad de idioma */
SELECT
    @idioma_count = COUNT(DISTINCT nro_idioma),
    @nro_idioma   = MIN(nro_idioma)
FROM dbo.contenidos_restaurantes
WHERE nro_restaurante = @nro_restaurante
  AND nro_contenido   = @nro_contenido;

IF @idioma_count IS NULL OR @idioma_count = 0
BEGIN
        RAISERROR('El contenido especificado no existe para ese restaurante.', 16, 1);
ROLLBACK TRANSACTION; RETURN;
END;

    IF @idioma_count > 1
BEGIN
        RAISERROR('El contenido es ambiguo (múltiples idiomas). Especifique nro_idioma.', 16, 1);
ROLLBACK TRANSACTION; RETURN;
END;

    /* 2) Tomar el costo del contenido */
SELECT @costo_click = cr.costo_click
FROM dbo.contenidos_restaurantes cr
WHERE cr.nro_restaurante = @nro_restaurante
  AND cr.nro_idioma      = @nro_idioma
  AND cr.nro_contenido   = @nro_contenido;

IF @costo_click IS NULL
BEGIN
        RAISERROR('El contenido no tiene costo_click definido.', 16, 1);
ROLLBACK TRANSACTION; RETURN;
END;

    /* 3) Obtener siguiente nro_click */
SELECT @nuevo_nro_click = ISNULL(MAX(nro_click), 0) + 1
FROM dbo.clicks_contenidos_restaurantes
WHERE nro_restaurante = @nro_restaurante
  AND nro_idioma      = @nro_idioma
  AND nro_contenido   = @nro_contenido;

/* 4) Insertar el click */
INSERT INTO dbo.clicks_contenidos_restaurantes
(
    nro_restaurante, nro_idioma, nro_contenido, nro_click,
    fecha_hora_registro, nro_cliente, costo_click, notificado
)
VALUES
    (
        @nro_restaurante, @nro_idioma, @nro_contenido, @nuevo_nro_click,
        GETDATE(), @nro_cliente, @costo_click, 0
    );

COMMIT TRANSACTION;

/* 5) Devolver info generada */
SELECT
    @nuevo_nro_click AS nro_click_generado,
    @nro_idioma      AS nro_idioma_resuelto,
    @costo_click     AS costo_click_usado,
    GETDATE()        AS fecha_hora_registro;
END TRY
BEGIN CATCH
IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    SET @ErrorMessage = ERROR_MESSAGE();
    RAISERROR(@ErrorMessage, 16, 1);
END CATCH
END;
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
    @nro_sucursal    INT = NULL
    AS
BEGIN
    SET NOCOUNT ON;

SELECT

    nro_restaurante = CONVERT(
            VARCHAR(1024),
            ENCRYPTBYPASSPHRASE(
                    CONVERT(VARCHAR(20), cr.nro_restaurante), -- passphrase
                    CONVERT(VARCHAR(20), cr.nro_restaurante)  -- texto claro
            ),
            2  -- salida en hex (sin 0x)
                      ),
    cr.nro_contenido,
    cr.nro_sucursal,
    cr.contenido_promocional,
    cr.imagen_promocional,
    cr.fecha_ini_vigencia,
    cr.fecha_fin_vigencia
-- cr.nro_idioma,
-- cr.costo_click,
-- cr.cod_contenido_restaurante
FROM dbo.contenidos_restaurantes cr
WHERE (@nro_restaurante IS NULL OR cr.nro_restaurante = @nro_restaurante)
  AND (@nro_sucursal    IS NULL OR cr.nro_sucursal    = @nro_sucursal)
ORDER BY cr.nro_restaurante,
         cr.nro_sucursal,
         cr.nro_contenido;
END
GO

CREATE OR ALTER PROCEDURE dbo.get_restaurante_info
    @cod_restaurante  VARCHAR(1024)   -- <- ahora recibe el código HEX cifrado
    AS
BEGIN
    SET NOCOUNT ON;

    ------------------------------------------------------------
    -- 1) Resolver nro_restaurante real a partir del código HEX
    ------------------------------------------------------------
    DECLARE @cod_rest_bin VARBINARY(1024) =
        CONVERT(VARBINARY(1024), '0x' + @cod_restaurante, 1);

    DECLARE @nro_restaurante INT;

    /*
      Estrategia: probamos desencriptar @cod_rest_bin usando, fila por fila,
      la passphrase = CONVERT(VARCHAR(20), r.nro_restaurante).
      Solo para la fila correcta DECRYPTBYPASSPHRASE devuelve el texto original,
      que es el mismo nro_restaurante, y el WHERE matchea.
      Hacemos esta resolución una sola vez y luego usamos @nro_restaurante
      (sargable) en los SELECTs siguientes.
    */
SELECT TOP (1)
            @nro_restaurante = r.nro_restaurante
FROM dbo.restaurantes AS r
WHERE r.nro_restaurante = CONVERT(
        INT,
        CONVERT(VARCHAR(1024),
                DECRYPTBYPASSPHRASE(
                        CONVERT(VARCHAR(20), r.nro_restaurante),
                        @cod_rest_bin
                )
        )
                          );

-- Si no lo encontró, devolvemos conjuntos vacíos (o podrías RAISERROR)
IF @nro_restaurante IS NULL
BEGIN
        -- RS1: Datos del restaurante (vacío)
SELECT CAST(NULL AS INT) AS nro_restaurante,
       CAST(NULL AS VARCHAR(200)) AS razon_social
    WHERE 1 = 0;

-- RS2: Sucursales (vacío)
SELECT CAST(NULL AS INT)  AS nro_restaurante,
       CAST(NULL AS INT)  AS nro_sucursal,
       CAST(NULL AS VARCHAR(100)) AS nom_sucursal,
       CAST(NULL AS VARCHAR(100)) AS calle,
       CAST(NULL AS INT)  AS nro_calle,
       CAST(NULL AS VARCHAR(100)) AS barrio,
       CAST(NULL AS INT)  AS nro_localidad,
       CAST(NULL AS VARCHAR(100)) AS nom_localidad,
       CAST(NULL AS CHAR(2)) AS cod_provincia,
       CAST(NULL AS VARCHAR(100)) AS nom_provincia,
       CAST(NULL AS VARCHAR(10)) AS cod_postal,
       CAST(NULL AS VARCHAR(200)) AS telefonos,
       CAST(NULL AS INT)  AS total_comensales,
       CAST(NULL AS INT)  AS min_tolerencia_reserva,
       CAST(NULL AS VARCHAR(100)) AS cod_sucursal_restaurante
    WHERE 1 = 0;

-- RS3: Turnos (vacío)
SELECT CAST(NULL AS INT) AS nro_restaurante,
       CAST(NULL AS INT) AS nro_sucursal,
       CAST(NULL AS TIME) AS hora_desde,
       CAST(NULL AS TIME) AS hora_hasta,
       CAST(NULL AS BIT) AS habilitado
    WHERE 1 = 0;

-- RS4: Zonas (vacío)
SELECT CAST(NULL AS INT) AS nro_restaurante,
       CAST(NULL AS INT) AS nro_sucursal,
       CAST(NULL AS VARCHAR(20)) AS cod_zona,
       CAST(NULL AS VARCHAR(100)) AS desc_zona,
       CAST(NULL AS INT) AS cant_comensales,
       CAST(NULL AS BIT) AS permite_menores,
       CAST(NULL AS BIT) AS habilitada
    WHERE 1 = 0;

-- RS5: Preferencias (vacío)
SELECT CAST(NULL AS INT) AS nro_restaurante,
       CAST(NULL AS INT) AS nro_sucursal,
       CAST(NULL AS VARCHAR(10)) AS cod_categoria,
       CAST(NULL AS VARCHAR(100)) AS nom_categoria,
       CAST(NULL AS INT) AS nro_valor_dominio,
       CAST(NULL AS VARCHAR(100)) AS nom_valor_dominio,
       CAST(NULL AS INT) AS nro_preferencia,
       CAST(NULL AS VARCHAR(4000)) AS observaciones
    WHERE 1 = 0;

RETURN;
END

    /* =========================================================
       RS1: Datos del restaurante
       ========================================================= */
SELECT
    r.nro_restaurante,
    r.razon_social
FROM dbo.restaurantes AS r
WHERE r.nro_restaurante = @nro_restaurante;

/* =========================================================
   RS2: Sucursales + Localidad/Provincia
   ========================================================= */
SELECT
    s.nro_restaurante,
    s.nro_sucursal,
    s.nom_sucursal,
    s.calle,
    s.nro_calle,
    s.barrio,
    s.nro_localidad,
    l.nom_localidad,
    l.cod_provincia,
    p.nom_provincia,
    s.cod_postal,
    s.telefonos,
    s.total_comensales,
    s.min_tolerencia_reserva,
    s.cod_sucursal_restaurante
FROM dbo.sucursales_restaurantes AS s
         INNER JOIN dbo.localidades AS l
                    ON l.nro_localidad = s.nro_localidad
         INNER JOIN dbo.provincias AS p
                    ON p.cod_provincia = l.cod_provincia
WHERE s.nro_restaurante = @nro_restaurante
ORDER BY s.nro_sucursal;

/* =========================================================
   RS3: Turnos por sucursal
   ========================================================= */
SELECT
    t.nro_restaurante,
    t.nro_sucursal,
    t.hora_desde,
    t.hora_hasta,
    t.habilitado
FROM dbo.turnos_sucursales_restaurantes AS t
WHERE t.nro_restaurante = @nro_restaurante
ORDER BY t.nro_sucursal, t.hora_desde;

/* =========================================================
   RS4: Zonas por sucursal
   ========================================================= */
SELECT
    z.nro_restaurante,
    z.nro_sucursal,
    z.cod_zona,
    z.desc_zona,
    z.cant_comensales,
    z.permite_menores,
    z.habilitada
FROM dbo.zonas_sucursales_restaurantes AS z
WHERE z.nro_restaurante = @nro_restaurante
ORDER BY z.nro_sucursal, z.cod_zona;

/* =========================================================
   RS5: Preferencias por sucursal
   ========================================================= */
SELECT
    pr.nro_restaurante,
    pr.nro_sucursal,
    pr.cod_categoria,
    cp.nom_categoria,
    pr.nro_valor_dominio,
    dcp.nom_valor_dominio,
    pr.nro_preferencia,
    pr.observaciones
FROM dbo.preferencias_restaurantes AS pr
         INNER JOIN dbo.dominio_categorias_preferencias AS dcp
                    ON dcp.cod_categoria       = pr.cod_categoria
                        AND dcp.nro_valor_dominio   = pr.nro_valor_dominio
         INNER JOIN dbo.categorias_preferencias AS cp
                    ON cp.cod_categoria        = pr.cod_categoria
WHERE pr.nro_restaurante = @nro_restaurante
  AND pr.nro_sucursal IS NOT NULL
ORDER BY pr.nro_sucursal, pr.cod_categoria, pr.nro_valor_dominio, pr.nro_preferencia;

END
GO
select * from dbo.clicks_contenidos_restaurantes
    go
CREATE OR ALTER PROCEDURE dbo.sp_clicks_pendientes
    @nro_restaurante INT = NULL,
    @top             INT = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    IF @top IS NULL
BEGIN
SELECT *
FROM (
         SELECT
             ccr.nro_click,
             ccr.nro_restaurante,
             ccr.nro_contenido,
             ISNULL(ccr.nro_cliente, 0) AS nro_cliente,
             ccr.fecha_hora_registro,
             ccr.costo_click,
             ccr.notificado,
             cr.cod_contenido_restaurante
         FROM dbo.clicks_contenidos_restaurantes AS ccr
                  INNER JOIN dbo.contenidos_restaurantes AS cr
                             ON cr.nro_restaurante = ccr.nro_restaurante
                                 AND cr.nro_contenido   = ccr.nro_contenido
         WHERE ISNULL(ccr.notificado,0) = 0
           AND (@nro_restaurante IS NULL OR ccr.nro_restaurante = @nro_restaurante)
     ) AS base
ORDER BY nro_restaurante, fecha_hora_registro, nro_click;
END
ELSE
BEGIN
SELECT TOP (@top) *
FROM (
         SELECT
             ccr.nro_click,
             ccr.nro_restaurante,
             ccr.nro_contenido,
             ISNULL(ccr.nro_cliente, 0) AS nro_cliente,
             ccr.fecha_hora_registro,
             ccr.costo_click,
             ccr.notificado,
             cr.cod_contenido_restaurante
         FROM dbo.clicks_contenidos_restaurantes AS ccr
                  INNER JOIN dbo.contenidos_restaurantes AS cr
                             ON cr.nro_restaurante = ccr.nro_restaurante
                                 AND cr.nro_contenido   = ccr.nro_contenido
         WHERE ISNULL(ccr.notificado,0) = 0
           AND (@nro_restaurante IS NULL OR ccr.nro_restaurante = @nro_restaurante)
     ) AS base
ORDER BY nro_restaurante, fecha_hora_registro, nro_click;
END
END;
GO



CREATE OR ALTER PROCEDURE dbo.sp_clicks_confirmar_notificados_obj
    @items_json      NVARCHAR(MAX),   -- Ej: '[{"nro_click":101},{"nro_click":102}]'
    @nro_restaurante INT = NULL       -- (opcional) para acotar por restaurante
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    -- 1️⃣ Crear tabla temporal para los IDs extraídos del JSON
    DECLARE @ids TABLE (nro_click INT PRIMARY KEY);

INSERT INTO @ids (nro_click)
SELECT DISTINCT TRY_CAST(nro_click AS INT)
FROM OPENJSON(@items_json)
    WITH (nro_click INT '$.nro_click')
WHERE TRY_CAST(nro_click AS INT) IS NOT NULL;

-- 2️⃣ Actualizar solo los clics no notificados aún
UPDATE c
SET c.notificado = 1
    FROM dbo.clicks_contenidos_restaurantes AS c
    INNER JOIN @ids AS i
ON i.nro_click = c.nro_click
WHERE ISNULL(c.notificado,0) = 0
  AND (@nro_restaurante IS NULL OR c.nro_restaurante = @nro_restaurante);

-- 3️⃣ Devolver los registros afectados
SELECT c.nro_click,
       c.nro_restaurante,
       c.nro_idioma,
       c.nro_contenido,
       c.fecha_hora_registro,
       c.notificado
FROM dbo.clicks_contenidos_restaurantes AS c
         INNER JOIN @ids AS i
                    ON i.nro_click = c.nro_click
WHERE (@nro_restaurante IS NULL OR c.nro_restaurante = @nro_restaurante)
ORDER BY c.nro_restaurante, c.fecha_hora_registro, c.nro_click;
END;
GO


