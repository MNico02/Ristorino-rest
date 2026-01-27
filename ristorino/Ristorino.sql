
---------------------------
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
--IDIOMAS
INSERT INTO dbo.idiomas (nro_idioma, nom_idioma, cod_idioma) VALUES
                                                                 (1, N'Español', N'es-AR'),
                                                                 (2, N'English', N'en-US');
INSERT INTO dbo.estados_reservas (cod_estado, nom_estado)
VALUES (1,N'Pendiente'),
       (2,N'Cancelada'),
       (3,N'Sin Evaluar'),
       (4,N'Evaluada')
    INSERT INTO dbo.idiomas_estados(cod_estado,nro_idioma,estado) VALUES
    (1,1,N'Pendiente'),
    (1,2,N'Pending'),
    (2,1,N'Cancelada'),
    (2,2,N'Cancelled'),
    (3,1,N'Sin Evaluar'),
    (3,2,N'Not Yet Evaluated'),
    (4,1,N'Evaluada'),
    (4,2,N'Evaluated')



-- MES 1
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
    DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1),
    EOMONTH(DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
    500),
    ('RESERVA',
    DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1),
    EOMONTH(DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
    1000);

-- MES 2
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 1, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 1, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 1, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 1, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 3
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 2, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 2, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 2, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 2, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 4
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 3, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 3, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 3, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 3, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 5
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 4, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 4, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 4, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 4, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 6
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 5, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 5, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 5, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 5, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 7
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 6, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 6, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 6, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 6, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 8
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 7, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 7, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 7, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 7, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 9
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 8, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 8, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 8, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 8, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 10
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 9, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 9, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 9, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 9, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 11
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 10, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 10, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 10, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 10, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);

-- MES 12
INSERT INTO dbo.costos (tipo_costo, fecha_ini_vigencia, fecha_fin_vigencia, monto)
VALUES
    ('CLICK',
     DATEADD(MONTH, 11, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 11, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     500),
    ('RESERVA',
     DATEADD(MONTH, 11, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1)),
     EOMONTH(DATEADD(MONTH, 11, DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1))),
     1000);



---
----
----    CORRER PROCESO BATCH RESTAURANTE. Luego insertar los datos para los idiomas
----
----
/*
 INSERT INTO idiomas_categorias_preferencias
(cod_categoria, nro_idioma, categoria, desc_categoria)
VALUES
(1,1,N'ESTILOS',N''),
(1,2,N'STYLES',N''),
(2,1,N'ESPECIALIDADES',N''),
(2,2,N'SPECIALTIES',N''),
(3,1,N'TIPOS_COMIDAS',N''),
(3,2,N'TYPES_OF_FOOD',N'');


--ESTILOS
-- Casual
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(1,1,1,N'Casual',N''),
(1,1,2,N'Casual',N'');

-- Familiar
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(1,2,1,N'Familiar',N''),
(1,2,2,N'Family',N'');

-- Gourmet
INSERT INTO idiomas_dominio_cat_preferencias VALUES
(1,3,1,N'Gourmet',N''),
(1,3,2,N'Gourmet',N'');

-- Minimalista
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(1,4,1,N'Minimalista',N''),
(1,4,2,N'Minimalist',N'');

-- Moderno
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(1,5,1,N'Moderno',N''),
(1,5,2,N'Modern',N'');

-- Celíaco
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(2,1,1,N'Celíaco',N''),
(2,1,2,N'Celiac',N'');


--ESPECIALIDADES
-- Vegetariano
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(2,2,1,N'Vegetariano',N''),
(2,2,2,N'Vegetarian',N'');

-- Pescetariano
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(2,3,1,N'Pescetariano',N''),
(2,3,2,N'Pescatarian',N'');

-- Sin gluten
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(2,4,1,N'Sin gluten',N''),
(2,4,2,N'Gluten free',N'');

-- Vegano
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(2,5,1,N'Vegano',N''),
(2,5,2,N'Vegan',N'');

--TIPÓ DE COMIDA
-- Italiana tradicional
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(3,1,1,N'Italiana tradicional',N''),
(3,1,2,N'Traditional Italian',N'');

-- Fusión japonesa-peruana
INSERT INTO idiomas_dominio_cat_preferencias (cod_categoria,nro_valor_dominio,nro_idioma,valor_dominio,desc_valor_dominio)
VALUES
(3,2,1,N'Fusión japonesa-peruana',N''),
(3,2,2,N'Japanese-Peruvian fusion',N'');*/



go
---------
CREATE OR ALTER PROCEDURE registrar_cliente
    @apellido           NVARCHAR(120),
    @nombre             NVARCHAR(120),
    @correo             NVARCHAR(255),
    @clave              NVARCHAR(255),
    @telefonos          NVARCHAR(100) = NULL,
    @nom_localidad      NVARCHAR(120),
    @nom_provincia      NVARCHAR(120),

    -- 👇 NUEVOS (preferencias)
    @cod_categoria      INT = NULL,
    @nro_valor_dominio  INT = NULL,
    @observaciones      NVARCHAR(500) = NULL
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @cod_provincia INT;
    DECLARE @nro_localidad INT;
    DECLARE @nro_cliente   INT;

    -- ❌ correo duplicado
    IF EXISTS (SELECT 1 FROM dbo.clientes WHERE correo = @correo)
BEGIN
        RAISERROR('El correo ya está registrado.', 16, 1);
        RETURN;
END;

    -- ===============================
    -- Provincia
    -- ===============================
SELECT @cod_provincia = cod_provincia
FROM dbo.provincias
WHERE LOWER(nom_provincia) COLLATE Latin1_General_CI_AI =
      LOWER(@nom_provincia) COLLATE Latin1_General_CI_AI;

IF @cod_provincia IS NULL
BEGIN
INSERT INTO dbo.provincias (nom_provincia)
VALUES (@nom_provincia);

SET @cod_provincia = SCOPE_IDENTITY();
END;

    -- ===============================
    -- Localidad
    -- ===============================
SELECT @nro_localidad = nro_localidad
FROM dbo.localidades
WHERE LOWER(nom_localidad) COLLATE Latin1_General_CI_AI =
      LOWER(@nom_localidad) COLLATE Latin1_General_CI_AI
  AND cod_provincia = @cod_provincia;

IF @nro_localidad IS NULL
BEGIN
INSERT INTO dbo.localidades (nom_localidad, cod_provincia)
VALUES (@nom_localidad, @cod_provincia);

SET @nro_localidad = SCOPE_IDENTITY();
END;

    -- ===============================
    -- Hash de clave (SHA-256)
    -- ===============================
    DECLARE @clave_hash NVARCHAR(64);
    SET @clave_hash = UPPER(
        CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', @clave), 2)
    );

BEGIN TRAN;

    -- ===============================
    -- Cliente
    -- ===============================
INSERT INTO dbo.clientes (
    apellido,
    nombre,
    clave,
    correo,
    telefonos,
    nro_localidad,
    habilitado
)
VALUES (
           @apellido,
           @nombre,
           @clave_hash,
           @correo,
           @telefonos,
           @nro_localidad,
           1
       );

SET @nro_cliente = SCOPE_IDENTITY();

    -- ===============================
    -- Preferencia (si viene)
    -- ===============================
    IF @cod_categoria IS NOT NULL
       AND @nro_valor_dominio IS NOT NULL
BEGIN
INSERT INTO dbo.preferencias_clientes (
    nro_cliente,
    cod_categoria,
    nro_valor_dominio,
    observaciones
)
VALUES (
           @nro_cliente,
           @cod_categoria,
           @nro_valor_dominio,
           @observaciones
       );
END;

COMMIT TRAN;

-- salida
SELECT @nro_cliente AS nro_cliente_creado;
END;
GO
SELECT *
FROM dbo.clientes c
         LEFT JOIN dbo.preferencias_clientes pc
                   ON pc.nro_cliente = c.nro_cliente;


go
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
    @momentoDelDia NVARCHAR(20) = NULL,
    @rangoPrecio NVARCHAR(50) = NULL,
    @cantidadPersonas INT = NULL,
    @tieneMenores NVARCHAR(10) = NULL,
    @restriccionesAlimentarias NVARCHAR(120) = NULL,
    @preferenciasAmbiente NVARCHAR(120) = NULL,
    @nombreRestaurante NVARCHAR(200) = NULL,   -- 👈 NUEVO
    @nroCliente INT = NULL
    AS
BEGIN
    SET NOCOUNT ON;

    /* ============================================================
       1) Normalizar parámetros
       ============================================================*/
    SET @tipoComida = NULLIF(LTRIM(RTRIM(@tipoComida)), '');
    SET @ciudad = NULLIF(LTRIM(RTRIM(@ciudad)), '');
    SET @provincia = NULLIF(LTRIM(RTRIM(@provincia)), '');
    SET @momentoDelDia = NULLIF(LTRIM(RTRIM(@momentoDelDia)), '');
    SET @rangoPrecio = NULLIF(LTRIM(RTRIM(@rangoPrecio)), '');
    SET @tieneMenores = NULLIF(LTRIM(RTRIM(@tieneMenores)), '');
    SET @restriccionesAlimentarias = NULLIF(LTRIM(RTRIM(@restriccionesAlimentarias)), '');
    SET @preferenciasAmbiente = NULLIF(LTRIM(RTRIM(@preferenciasAmbiente)), '');
    SET @nombreRestaurante = NULLIF(LTRIM(RTRIM(@nombreRestaurante)), '');

    /* ============================================================
       2) Rango horario
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
       3) Resolver provincia por ciudad
       ============================================================*/
    IF @provincia IS NULL AND @ciudad IS NOT NULL
BEGIN
SELECT TOP 1 @provincia = p.nom_provincia
FROM dbo.localidades l
         INNER JOIN dbo.provincias p ON l.cod_provincia = p.cod_provincia
WHERE LOWER(l.nom_localidad) COLLATE Latin1_General_CI_AI
          LIKE '%' + LOWER(@ciudad) + '%';
END;

    /* ============================================================
       4) Candidatos
       ============================================================*/
    ;WITH candidatos AS (
    SELECT
        nro_restaurante = CONVERT(
                VARCHAR(1024),
                ENCRYPTBYPASSPHRASE(
                        CONVERT(VARCHAR(20), r.nro_restaurante),
                        CONVERT(VARCHAR(20), r.nro_restaurante)
                ),
                2
                          ),
        r.razon_social,
        s.nro_sucursal,
        s.nom_sucursal,
        l.nom_localidad,
        p.nom_provincia,
        z.desc_zona,
        t.hora_desde,
        t.hora_hasta,

        /* --- COINCIDENCIAS --- */
        CASE
            WHEN @tipoComida IS NOT NULL
                AND dp.nom_valor_dominio COLLATE Latin1_General_CI_AI
                     LIKE '%' + @tipoComida + '%' THEN 1 ELSE 0 END
            + CASE
                  WHEN @preferenciasAmbiente IS NOT NULL
                      AND dp.nom_valor_dominio COLLATE Latin1_General_CI_AI
                           LIKE '%' + @preferenciasAmbiente + '%' THEN 1 ELSE 0 END
            + CASE
                  WHEN @restriccionesAlimentarias IS NOT NULL
                      AND dp.nom_valor_dominio COLLATE Latin1_General_CI_AI
                           LIKE '%' + @restriccionesAlimentarias + '%' THEN 1 ELSE 0 END
            + CASE
                  WHEN @nombreRestaurante IS NOT NULL
                      AND (
                           LOWER(r.razon_social) COLLATE Latin1_General_CI_AI
                               LIKE '%' + LOWER(@nombreRestaurante) + '%'
                               OR LOWER(s.nom_sucursal) COLLATE Latin1_General_CI_AI
                               LIKE '%' + LOWER(@nombreRestaurante) + '%'
                           )
                      THEN 1 ELSE 0 END
            AS coincidencias

    FROM dbo.restaurantes r
             INNER JOIN dbo.sucursales_restaurantes s
                        ON r.nro_restaurante = s.nro_restaurante
             INNER JOIN dbo.localidades l
                        ON s.nro_localidad = l.nro_localidad
             INNER JOIN dbo.provincias p
                        ON l.cod_provincia = p.cod_provincia
             LEFT JOIN dbo.zonas_sucursales_restaurantes z
                       ON s.nro_restaurante = z.nro_restaurante
                           AND s.nro_sucursal = z.nro_sucursal
             LEFT JOIN dbo.turnos_sucursales_restaurantes t
                       ON s.nro_restaurante = t.nro_restaurante
                           AND s.nro_sucursal = t.nro_sucursal
             LEFT JOIN dbo.preferencias_restaurantes prr
                       ON r.nro_restaurante = prr.nro_restaurante
             LEFT JOIN dbo.dominio_categorias_preferencias dp
                       ON prr.cod_categoria = dp.cod_categoria
                           AND prr.nro_valor_dominio = dp.nro_valor_dominio

    WHERE
        (@ciudad IS NULL OR LOWER(l.nom_localidad) COLLATE Latin1_General_CI_AI LIKE '%' + LOWER(@ciudad) + '%')
      AND (@provincia IS NULL OR LOWER(p.nom_provincia) COLLATE Latin1_General_CI_AI LIKE '%' + LOWER(@provincia) + '%')
      AND (@horaDesde IS NULL OR @horaHasta IS NULL
        OR (t.hora_desde <= @horaHasta AND t.hora_hasta >= @horaDesde))
      AND (@tieneMenores IS NULL
        OR (@tieneMenores = 'si' AND z.permite_menores = 1)
        OR (@tieneMenores = 'no' AND z.permite_menores = 0))
      AND (@cantidadPersonas IS NULL
        OR z.cant_comensales >= @cantidadPersonas
        OR s.total_comensales >= @cantidadPersonas)
      AND (
        @nombreRestaurante IS NULL
            OR LOWER(r.razon_social) COLLATE Latin1_General_CI_AI
            LIKE '%' + LOWER(@nombreRestaurante) + '%'
            OR LOWER(s.nom_sucursal) COLLATE Latin1_General_CI_AI
            LIKE '%' + LOWER(@nombreRestaurante) + '%'
        )
)

     SELECT TOP 10
        nro_restaurante,
             nro_sucursal,
            razon_social,
            nom_sucursal,
            nom_localidad,
            nom_provincia,
            MAX(desc_zona) AS desc_zona,
            MIN(hora_desde) AS hora_desde,
            MAX(hora_hasta) AS hora_hasta,
            MAX(coincidencias) AS coincidencias
     FROM candidatos
     GROUP BY
         nro_restaurante,
         nro_sucursal,
         razon_social,
         nom_sucursal,
         nom_localidad,
         nom_provincia
     ORDER BY
         MAX(coincidencias) DESC,
         razon_social;
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

SELECT TOP (5)
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
    @contenido_promocional NVARCHAR(MAX)
    AS
BEGIN
    SET NOCOUNT ON;

UPDATE dbo.contenidos_restaurantes
SET
    contenido_promocional = @contenido_promocional
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
    @cod_restaurante VARCHAR(1024),      -- código HEX cifrado
    @nro_contenido   INT,
    @correo_cliente  NVARCHAR(255) = NULL
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @nuevo_nro_click INT;
    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @nro_idioma INT;
    DECLARE @idioma_count INT;
    DECLARE @costo_click DECIMAL(12,2);
    DECLARE @nro_cliente INT = NULL;
    DECLARE @nro_restaurante INT;
    DECLARE @cod_rest_bin VARBINARY(1024);

    ------------------------------------------------------------
    -- 0) Validar código recibido
    ------------------------------------------------------------
    IF @cod_restaurante IS NULL OR LTRIM(RTRIM(@cod_restaurante)) = ''
BEGIN
        RAISERROR('Código de restaurante vacío.', 16, 1);
        RETURN;
END;

    ------------------------------------------------------------
    -- 1) Convertir HEX a VARBINARY (SEGURO)
    ------------------------------------------------------------
    SET @cod_rest_bin =
        CASE
            WHEN LEFT(@cod_restaurante, 2) = '0x'
                THEN TRY_CONVERT(VARBINARY(1024), @cod_restaurante, 1)
            ELSE
                TRY_CONVERT(VARBINARY(1024), '0x' + @cod_restaurante, 1)
END;

    IF @cod_rest_bin IS NULL
BEGIN
        RAISERROR('Código de restaurante inválido (no es HEX válido).', 16, 1);
        RETURN;
END;

    ------------------------------------------------------------
    -- 2) Resolver nro_restaurante real
    ------------------------------------------------------------
SELECT TOP (1)
            @nro_restaurante = r.nro_restaurante
FROM dbo.restaurantes r
WHERE r.nro_restaurante = TRY_CONVERT(
    INT,
        CONVERT(VARCHAR(50),
                DECRYPTBYPASSPHRASE(CONVERT(VARCHAR(20), r.nro_restaurante), @cod_rest_bin)
        )
                          );

IF @nro_restaurante IS NULL
BEGIN
        RAISERROR('Código de restaurante no corresponde a ningún restaurante.', 16, 1);
        RETURN;
END;

    ------------------------------------------------------------
    -- 3) Resolver nro_cliente desde correo (OPCIONAL)
    --    Si no existe / está deshabilitado => se registra igual con nro_cliente = NULL
    ------------------------------------------------------------
    SET @correo_cliente = NULLIF(LTRIM(RTRIM(@correo_cliente)), '');

    IF @correo_cliente IS NOT NULL
BEGIN
SELECT @nro_cliente = c.nro_cliente
FROM dbo.clientes c
WHERE c.correo = @correo_cliente
  AND (c.habilitado = 1 OR c.habilitado IS NULL);  -- por si tu tabla no tiene habilitado o lo manejás distinto

-- Si no lo encuentra, queda NULL y seguimos (NO error)
-- IF @nro_cliente IS NULL ... (NO HACER)
END;

BEGIN TRY
BEGIN TRANSACTION;

        ------------------------------------------------------------
        -- 4) Verificar idioma del contenido
        ------------------------------------------------------------
SELECT
    @idioma_count = COUNT(DISTINCT nro_idioma),
    @nro_idioma   = MIN(nro_idioma)
FROM dbo.contenidos_restaurantes
WHERE nro_restaurante = @nro_restaurante
  AND nro_contenido   = @nro_contenido;

IF @idioma_count IS NULL OR @idioma_count = 0
BEGIN
            RAISERROR('El contenido no existe para ese restaurante.', 16, 1);
ROLLBACK;
RETURN;
END;

        IF @idioma_count > 1
BEGIN
            RAISERROR('Contenido ambiguo (múltiples idiomas).', 16, 1);
ROLLBACK;
RETURN;
END;

        ------------------------------------------------------------
        -- 5) Obtener costo
        ------------------------------------------------------------
SELECT @costo_click = costo_click
FROM dbo.contenidos_restaurantes
WHERE nro_restaurante = @nro_restaurante
  AND nro_idioma      = @nro_idioma
  AND nro_contenido   = @nro_contenido;

IF @costo_click IS NULL
BEGIN
            RAISERROR('El contenido no tiene costo_click.', 16, 1);
ROLLBACK;
RETURN;
END;

        ------------------------------------------------------------
        -- 6) Generar nro_click (mejor con locks)
        ------------------------------------------------------------
SELECT @nuevo_nro_click = ISNULL(MAX(nro_click), 0) + 1
FROM dbo.clicks_contenidos_restaurantes WITH (UPDLOCK, HOLDLOCK)
WHERE nro_restaurante = @nro_restaurante
  AND nro_idioma      = @nro_idioma
  AND nro_contenido   = @nro_contenido;

------------------------------------------------------------
-- 7) Insertar click
------------------------------------------------------------
INSERT INTO dbo.clicks_contenidos_restaurantes
(
    nro_restaurante,
    nro_idioma,
    nro_contenido,
    nro_click,
    fecha_hora_registro,
    nro_cliente,
    costo_click,
    notificado
)
VALUES
    (
        @nro_restaurante,
        @nro_idioma,
        @nro_contenido,
        @nuevo_nro_click,
        GETDATE(),
        @nro_cliente,      -- puede ser NULL y está perfecto
        @costo_click,
        0
    );

COMMIT;

------------------------------------------------------------
-- 8) Resultado
------------------------------------------------------------
SELECT
    1               AS success,
    'OK'            AS message,
    @nuevo_nro_click AS nro_click_generado,
    @nro_idioma      AS nro_idioma_resuelto,
    @costo_click     AS costo_click_usado,
    @nro_cliente     AS nro_cliente_resuelto,
    GETDATE()        AS fecha_hora_registro;

END TRY
BEGIN CATCH
IF @@TRANCOUNT > 0 ROLLBACK;
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



GO
exec get_promociones
go
CREATE OR ALTER PROCEDURE dbo.get_promociones
    @cod_restaurante VARCHAR(1024) = NULL,  -- cifrado
    @nro_sucursal    INT = NULL,
    @idioma    VARCHAR(10) = 'es'     -- 'es', 'en', 'es_AR', 'en_US'
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    ------------------------------------------------------------
    -- 0) Resolver nro_idioma (estático)
    ------------------------------------------------------------
    DECLARE @nro_idioma INT;

    SET @nro_idioma =
        CASE
            WHEN @idioma LIKE 'es%' THEN 1
            WHEN @idioma LIKE 'en%' THEN 2
            ELSE 1 -- default español
END;

    ------------------------------------------------------------
    -- 1) Resolver nro_restaurante REAL desde el código cifrado
    ------------------------------------------------------------
    DECLARE @nro_restaurante INT = NULL;

    IF @cod_restaurante IS NOT NULL
BEGIN
        DECLARE @cod_rest_bin VARBINARY(1024) =
            CONVERT(VARBINARY(1024), '0x' + @cod_restaurante, 1);

SELECT TOP (1)
            @nro_restaurante = r.nro_restaurante
FROM dbo.restaurantes r
WHERE r.nro_restaurante = CONVERT(
        INT,
        CONVERT(VARCHAR(1024),
                DECRYPTBYPASSPHRASE(
                        CONVERT(VARCHAR(20), r.nro_restaurante),
                        @cod_rest_bin
                )
        )
                          );
END

    ------------------------------------------------------------
    -- 2) Promociones VIGENTES + por idioma
    ------------------------------------------------------------
SELECT
    -- 🔐 DEVOLVEMOS SIEMPRE CIFRADO
    nro_restaurante = CONVERT(
            VARCHAR(1024),
            ENCRYPTBYPASSPHRASE(
                    CONVERT(VARCHAR(20), cr.nro_restaurante),
                    CONVERT(VARCHAR(20), cr.nro_restaurante)
            ),
            2
                      ),
    cr.nro_contenido,
    cr.nro_sucursal,
    cr.contenido_promocional,
    cr.imagen_promocional,
    cr.fecha_ini_vigencia,
    cr.fecha_fin_vigencia
FROM dbo.contenidos_restaurantes cr
WHERE
    (@nro_restaurante IS NULL OR cr.nro_restaurante = @nro_restaurante)
  AND (@nro_sucursal IS NULL OR cr.nro_sucursal = @nro_sucursal)

  -- 👇 FILTRO POR IDIOMA
  AND cr.nro_idioma = @nro_idioma

  -- 👇 SOLO VIGENTES
  AND cr.fecha_fin_vigencia >= CAST(GETDATE() AS DATE)
  AND cr.fecha_ini_vigencia <= CAST(GETDATE() AS DATE)

ORDER BY
    cr.nro_restaurante,
    cr.nro_sucursal,
    cr.nro_contenido;
END
GO


GO
GO
CREATE OR ALTER PROCEDURE dbo.get_restaurante_info
    @cod_restaurante VARCHAR(1024),   -- código cifrado (HEX)
    @idioma_front    VARCHAR(10)      -- 'es', 'en', 'es_AR', 'en_US'
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    ------------------------------------------------------------
    -- 0) Resolver nro_idioma (estático)
    ------------------------------------------------------------
    DECLARE @nro_idioma INT;

    SET @nro_idioma =
        CASE
            WHEN @idioma_front LIKE 'es%' THEN 1
            WHEN @idioma_front LIKE 'en%' THEN 2
            ELSE 1 -- default español
END;

    ------------------------------------------------------------
    -- 1) Resolver nro_restaurante real desde el código cifrado
    ------------------------------------------------------------
    DECLARE @cod_rest_bin VARBINARY(1024) =
        CONVERT(VARBINARY(1024), '0x' + @cod_restaurante, 1);

    DECLARE @nro_restaurante INT;

SELECT TOP (1)
            @nro_restaurante = r.nro_restaurante
FROM dbo.restaurantes r
WHERE r.nro_restaurante = CONVERT(
        INT,
        CONVERT(VARCHAR(1024),
                DECRYPTBYPASSPHRASE(
                        CONVERT(VARCHAR(20), r.nro_restaurante),
                        @cod_rest_bin
                )
        )
                          );

------------------------------------------------------------
-- 2) Si no se pudo resolver → devolver RS vacíos
------------------------------------------------------------
IF @nro_restaurante IS NULL
BEGIN
SELECT CAST(NULL AS VARCHAR(1024)) AS nro_restaurante,
       CAST(NULL AS VARCHAR(200))  AS razon_social
    WHERE 1 = 0;

SELECT CAST(NULL AS VARCHAR(1024)) AS nro_restaurante,
       CAST(NULL AS INT) AS nro_sucursal
    WHERE 1 = 0;

SELECT CAST(NULL AS VARCHAR(1024)) AS nro_restaurante,
       CAST(NULL AS INT) AS nro_sucursal
    WHERE 1 = 0;

SELECT CAST(NULL AS VARCHAR(1024)) AS nro_restaurante,
       CAST(NULL AS INT) AS nro_sucursal
    WHERE 1 = 0;

SELECT CAST(NULL AS VARCHAR(1024)) AS nro_restaurante,
       CAST(NULL AS INT) AS nro_sucursal
    WHERE 1 = 0;

RETURN;
END;

    ------------------------------------------------------------
    -- 3) Reutilizamos el mismo código cifrado
    ------------------------------------------------------------
    DECLARE @nro_restaurante_cifrado VARCHAR(1024) = @cod_restaurante;

    /* =========================================================
       RS1: Datos del restaurante
       ========================================================= */
SELECT
    @nro_restaurante_cifrado AS nro_restaurante,
    r.razon_social
FROM dbo.restaurantes r
WHERE r.nro_restaurante = @nro_restaurante;

/* =========================================================
   RS2: Sucursales + Localidad / Provincia
   ========================================================= */
SELECT
    @nro_restaurante_cifrado AS nro_restaurante,
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
FROM dbo.sucursales_restaurantes s
         INNER JOIN dbo.localidades l
                    ON l.nro_localidad = s.nro_localidad
         INNER JOIN dbo.provincias p
                    ON p.cod_provincia = l.cod_provincia
WHERE s.nro_restaurante = @nro_restaurante
ORDER BY s.nro_sucursal;

/* =========================================================
   RS3: Turnos
   ========================================================= */
SELECT
    @nro_restaurante_cifrado AS nro_restaurante,
    t.nro_sucursal,
    t.hora_desde,
    t.hora_hasta,
    t.habilitado
FROM dbo.turnos_sucursales_restaurantes t
WHERE t.nro_restaurante = @nro_restaurante
ORDER BY t.nro_sucursal, t.hora_desde;

/* =========================================================
   RS4: Zonas (multi-idioma)
   ========================================================= */
SELECT
    @nro_restaurante_cifrado AS nro_restaurante,
    z.nro_sucursal,
    z.cod_zona,

    ISNULL(iz.zona, z.desc_zona)      AS zona,
    ISNULL(iz.desc_zona, z.desc_zona) AS desc_zona,

    z.cant_comensales,
    z.permite_menores,
    z.habilitada
FROM dbo.zonas_sucursales_restaurantes z
         LEFT JOIN dbo.idiomas_zonas_suc_restaurantes iz
                   ON iz.nro_restaurante = z.nro_restaurante
                       AND iz.nro_sucursal    = z.nro_sucursal
                       AND iz.cod_zona        = z.cod_zona
                       AND iz.nro_idioma      = @nro_idioma
WHERE z.nro_restaurante = @nro_restaurante
ORDER BY z.nro_sucursal, z.cod_zona;

/* =========================================================
   RS5: Preferencias (categorías + dominio multi-idioma)
   ========================================================= */
SELECT
    @nro_restaurante_cifrado AS nro_restaurante,
    pr.nro_sucursal,
    pr.cod_categoria,

    ISNULL(icp.categoria, cp.nom_categoria) AS nom_categoria,

    pr.nro_valor_dominio,

    ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio) AS nom_valor_dominio,

    pr.nro_preferencia,
    pr.observaciones
FROM dbo.preferencias_restaurantes pr

         INNER JOIN dbo.categorias_preferencias cp
                    ON cp.cod_categoria = pr.cod_categoria

         INNER JOIN dbo.dominio_categorias_preferencias dcp
                    ON dcp.cod_categoria      = pr.cod_categoria
                        AND dcp.nro_valor_dominio = pr.nro_valor_dominio

         LEFT JOIN dbo.idiomas_categorias_preferencias icp
                   ON icp.cod_categoria = pr.cod_categoria
                       AND icp.nro_idioma    = @nro_idioma

         LEFT JOIN dbo.idiomas_dominio_cat_preferencias idcp
                   ON idcp.cod_categoria      = pr.cod_categoria
                       AND idcp.nro_valor_dominio = pr.nro_valor_dominio
                       AND idcp.nro_idioma        = @nro_idioma

WHERE pr.nro_restaurante = @nro_restaurante
  AND pr.nro_sucursal IS NOT NULL
ORDER BY pr.nro_sucursal,
         pr.cod_categoria,
         pr.nro_valor_dominio;

END
GO


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
             cl.correo AS correo_cliente,
             ccr.fecha_hora_registro,
             ccr.costo_click,
             ccr.notificado,
             cr.cod_contenido_restaurante
         FROM dbo.clicks_contenidos_restaurantes AS ccr
                  INNER JOIN dbo.contenidos_restaurantes AS cr
                             ON cr.nro_restaurante = ccr.nro_restaurante
                                 AND cr.nro_contenido   = ccr.nro_contenido
                  LEFT JOIN dbo.clientes AS cl
                            ON cl.nro_cliente = ccr.nro_cliente
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
             cl.correo AS correo_cliente,
             ccr.fecha_hora_registro,
             ccr.costo_click,
             ccr.notificado,
             cr.cod_contenido_restaurante
         FROM dbo.clicks_contenidos_restaurantes AS ccr
                  INNER JOIN dbo.contenidos_restaurantes AS cr
                             ON cr.nro_restaurante = ccr.nro_restaurante
                                 AND cr.nro_contenido   = ccr.nro_contenido
                  LEFT JOIN dbo.clientes AS cl
                            ON cl.nro_cliente = ccr.nro_cliente
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

CREATE OR ALTER PROCEDURE dbo.ins_contenidos_restaurante_lote
    @nro_restaurante      INT,
    @promociones_json     NVARCHAR(MAX),  -- 👈 JSON con array de promociones
    @costo_aplicado       DECIMAL(12,2) OUTPUT
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

BEGIN TRY
BEGIN TRAN;

        ------------------------------------------------------------
        -- 1) Obtener el costo vigente actual
        ------------------------------------------------------------
        DECLARE @costo_actual DECIMAL(12,2);
        DECLARE @fecha_fin_costo DATE;
        DECLARE @fecha_actual DATE = CAST(GETDATE() AS DATE);

SELECT TOP 1
            @costo_actual = c.monto,
        @fecha_fin_costo = c.fecha_fin_vigencia
FROM dbo.costos c
WHERE c.tipo_costo = 'CLICK'
  AND c.fecha_ini_vigencia <= @fecha_actual
  AND c.fecha_fin_vigencia >= @fecha_actual
ORDER BY c.fecha_ini_vigencia DESC;

SET @costo_actual = ISNULL(@costo_actual, 0.00);
        SET @fecha_fin_costo = ISNULL(@fecha_fin_costo, DATEADD(YEAR, 1, @fecha_actual));

        ------------------------------------------------------------
        -- 2) Crear tabla temporal con los datos del JSON
        ------------------------------------------------------------
CREATE TABLE #promociones_temp (
                                   nro_contenido           INT,
                                   nro_sucursal            INT,
                                   contenido_a_publicar    NVARCHAR(MAX),
                                   imagen_promocional      NVARCHAR(255),
                                   cod_contenido_restaurante NVARCHAR(MAX)
);

INSERT INTO #promociones_temp (
    nro_contenido,
    nro_sucursal,
    contenido_a_publicar,
    imagen_promocional,
    cod_contenido_restaurante
)
SELECT
    nro_contenido,
    nro_sucursal,
    contenido_a_publicar,
    imagen_promocional,
    cod_contenido_restaurante
FROM OPENJSON(@promociones_json)
    WITH (
    nro_contenido           INT             '$.nro_contenido',
    nro_sucursal            INT             '$.nro_sucursal',
    contenido_a_publicar    NVARCHAR(MAX)   '$.contenido_a_publicar',
    imagen_promocional      NVARCHAR(255)   '$.imagen_promocional',
    cod_contenido_restaurante NVARCHAR(MAX) '$.cod_contenido_restaurante'
    );

------------------------------------------------------------
-- 3) Eliminar duplicados de la tabla temporal
------------------------------------------------------------
DELETE t
        FROM #promociones_temp t
        WHERE EXISTS (
            SELECT 1
            FROM dbo.contenidos_restaurantes cr
            WHERE cr.nro_restaurante = @nro_restaurante
              AND cr.cod_contenido_restaurante = t.cod_contenido_restaurante
        );

        ------------------------------------------------------------
        -- 4) Insertar en ESPAÑOL (nro_idioma = 1)
        ------------------------------------------------------------
INSERT INTO dbo.contenidos_restaurantes (
    nro_restaurante,
    nro_idioma,
    nro_sucursal,
    contenido_a_publicar,
    imagen_promocional,
    costo_click,
    fecha_ini_vigencia,
    fecha_fin_vigencia,
    cod_contenido_restaurante
)
SELECT
    @nro_restaurante,
    1,  -- ESPAÑOL
    t.nro_sucursal,
    t.contenido_a_publicar,
    t.imagen_promocional,
    @costo_actual,
    @fecha_actual,
    @fecha_fin_costo,
    t.cod_contenido_restaurante
FROM #promociones_temp t;

------------------------------------------------------------
-- 5) Insertar en INGLÉS (nro_idioma = 2)
------------------------------------------------------------
INSERT INTO dbo.contenidos_restaurantes (
    nro_restaurante,
    nro_idioma,
    nro_sucursal,
    contenido_a_publicar,
    imagen_promocional,
    costo_click,
    fecha_ini_vigencia,
    fecha_fin_vigencia,
    cod_contenido_restaurante
)
SELECT
    @nro_restaurante,
    2,  -- INGLÉS
    t.nro_sucursal,
    t.contenido_a_publicar,
    t.imagen_promocional,
    @costo_actual,
    @fecha_actual,
    @fecha_fin_costo,
    t.cod_contenido_restaurante
FROM #promociones_temp t;

------------------------------------------------------------
-- 6) Asignar costo de salida
------------------------------------------------------------
SET @costo_aplicado = @costo_actual;

DROP TABLE #promociones_temp;

COMMIT;
END TRY
BEGIN CATCH
IF XACT_STATE() <> 0
            ROLLBACK;

        SET @costo_aplicado = NULL;
        THROW;
END CATCH
END;
GO









CREATE OR ALTER PROCEDURE dbo.get_categorias_preferencias
    AS
BEGIN
    SET NOCOUNT ON;

    -- RS1: Categorías
SELECT
    cod_categoria,
    nom_categoria
FROM dbo.categorias_preferencias
ORDER BY cod_categoria;

-- RS2: Dominios por categoría
SELECT
    cod_categoria,
    nro_valor_dominio,
    nom_valor_dominio
FROM dbo.dominio_categorias_preferencias
ORDER BY cod_categoria, nro_valor_dominio;
END;
GO





select * from dbo.localidades

select* from dbo.sucursales_restaurantes
select* from dbo.contenidos_restaurantes
select * from dbo.preferencias_restaurantes
select * from dbo.categorias_preferencias
select * from dbo.dominio_categorias_preferencias

    go
CREATE OR ALTER PROCEDURE dbo.get_cliente_por_correo
    @correo VARCHAR(255)
    AS
BEGIN
    SET NOCOUNT ON;

SELECT
    c.apellido,
    c.nombre,
    c.correo,
    c.telefonos
FROM dbo.clientes c
WHERE c.correo = @correo
  and habilitado = '1'
END;
GO


CREATE OR ALTER PROCEDURE dbo.ins_reserva_confirmada_ristorino
    (
    @correo                 NVARCHAR(255),

    -- lo que devuelve el restaurante (UUID o nro o string)
    @cod_reserva_restaurante NVARCHAR(50),

    @fecha_reserva           DATE,
    @hora_reserva            TIME(0),
    @nro_restaurante         INT,
    @nro_sucursal            INT,
    @cod_zona                INT,
    @cant_adultos            INT,
    @cant_menores            INT = 0,
    @costo_reserva           DECIMAL(12,2) = NULL,

    -- opcional: estado (si no lo mandás, queda 1)
    @cod_estado              INT = 1
    )
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

BEGIN TRY
BEGIN TRAN;

        /* 0) Validaciones básicas */
        IF @correo IS NULL OR LTRIM(RTRIM(@correo)) = ''
            THROW 51010, 'El correo es obligatorio.', 1;

        IF @cod_reserva_restaurante IS NULL OR LTRIM(RTRIM(@cod_reserva_restaurante)) = ''
            THROW 51011, 'El cod_reserva_restaurante es obligatorio.', 1;

        IF (@cant_adultos + ISNULL(@cant_menores,0)) <= 0
            THROW 51012, 'La cantidad de comensales debe ser mayor a 0.', 1;

        /* 1) Construir cod_reserva_sucursal = "codRestaurante-nroSucursal" */
        DECLARE @cod_reserva_sucursal NVARCHAR(50);
        SET @cod_reserva_sucursal = CONCAT(@cod_reserva_restaurante, '-', CONVERT(NVARCHAR(10), @nro_sucursal));

        IF LEN(@cod_reserva_sucursal) > 50
            THROW 51017, 'El cod_reserva_sucursal excede 50 caracteres.', 1;

        /* 2) Idempotencia: si ya existe esa reserva (AK), devolverla */
        IF EXISTS (SELECT 1 FROM dbo.reservas_restaurantes WHERE cod_reserva_sucursal = @cod_reserva_sucursal)
BEGIN
SELECT TOP 1
                nro_cliente, nro_reserva, cod_reserva_sucursal,
       fecha_reserva, hora_reserva,
       nro_restaurante, nro_sucursal, cod_zona,
       hora_desde, cant_adultos, cant_menores,
       cod_estado, fecha_cancelacion, costo_reserva
FROM dbo.reservas_restaurantes
WHERE cod_reserva_sucursal = @cod_reserva_sucursal;

COMMIT;
RETURN;
END

        /* 3) Resolver nro_cliente por correo */
        DECLARE @nro_cliente INT;

SELECT @nro_cliente = c.nro_cliente
FROM dbo.clientes c
WHERE c.correo = @correo
  AND c.habilitado = 1;

IF @nro_cliente IS NULL
            THROW 51013, 'El cliente no existe o está deshabilitado en Ristorino.', 1;

        /* 4) Validar sucursal/zona/turno existan en Ristorino (por FKs) */
        IF NOT EXISTS (
            SELECT 1
            FROM dbo.sucursales_restaurantes s
            WHERE s.nro_restaurante = @nro_restaurante
              AND s.nro_sucursal    = @nro_sucursal
        )
            THROW 51014, 'La sucursal no existe en Ristorino.', 1;

        IF NOT EXISTS (
            SELECT 1
            FROM dbo.zonas_sucursales_restaurantes z
            WHERE z.nro_restaurante = @nro_restaurante
              AND z.nro_sucursal    = @nro_sucursal
              AND z.cod_zona        = @cod_zona
              AND z.habilitada      = 1
        )
            THROW 51015, 'La zona no existe o no está habilitada en Ristorino.', 1;

        IF NOT EXISTS (
            SELECT 1
            FROM dbo.turnos_sucursales_restaurantes t
            WHERE t.nro_restaurante = @nro_restaurante
              AND t.nro_sucursal    = @nro_sucursal
              AND t.hora_desde      = @hora_reserva
              AND t.habilitado      = 1
        )
            THROW 51016, 'El turno (hora_desde) no existe o no está habilitado en Ristorino.', 1;

        DECLARE
@capacidad_zona     INT,
    @ocupacion_actual   INT,
    @cant_solicitada    INT,
    @disponible         INT;

/* Cantidad solicitada */
SET @cant_solicitada = ISNULL(@cant_adultos,0) + ISNULL(@cant_menores,0);

/* Capacidad máxima de la zona */
SELECT
    @capacidad_zona = z.cant_comensales
FROM dbo.zonas_sucursales_restaurantes z
WHERE z.nro_restaurante = @nro_restaurante
  AND z.nro_sucursal    = @nro_sucursal
  AND z.cod_zona        = @cod_zona
  AND z.habilitada      = 1;

IF @capacidad_zona IS NULL
BEGIN
    RAISERROR('La zona no tiene capacidad definida.', 16, 1);
    RETURN;
END;

/* Ocupación actual en ese turno (solo reservas activas) */
SELECT
    @ocupacion_actual = ISNULL(SUM(r.cant_adultos + ISNULL(r.cant_menores,0)), 0)
FROM dbo.reservas_restaurantes r
WHERE r.nro_restaurante = @nro_restaurante
  AND r.nro_sucursal    = @nro_sucursal
  AND r.cod_zona        = @cod_zona
  AND r.fecha_reserva   = @fecha_reserva
  AND r.hora_desde      = @hora_reserva
  AND r.fecha_cancelacion IS NULL
  AND r.cod_estado = 1;   -- Confirmada / Activa

/* Capacidad restante */
SET @disponible = @capacidad_zona - @ocupacion_actual;

/* Validación final */
IF @disponible < @cant_solicitada
BEGIN
    RAISERROR (
        'Capacidad insuficiente. Capacidad total: %d. Ocupado: %d. Disponible: %d.',
        16,
        1,
        @capacidad_zona,
        @ocupacion_actual,
        @disponible
    );
    RETURN;
END




        /* 5) Calcular nro_reserva incremental por cliente */
        DECLARE @nro_reserva INT;

SELECT @nro_reserva = ISNULL(MAX(r.nro_reserva), 0) + 1
FROM dbo.reservas_restaurantes r
WHERE r.nro_cliente = @nro_cliente;

/* 6) Insertar reserva */
INSERT INTO dbo.reservas_restaurantes
(
    nro_cliente,
    nro_reserva,
    cod_reserva_sucursal,
    fecha_reserva,
    hora_reserva,
    nro_restaurante,
    nro_sucursal,
    cod_zona,
    hora_desde,
    cant_adultos,
    cant_menores,
    cod_estado,
    fecha_cancelacion,
    costo_reserva
)
VALUES
    (
        @nro_cliente,
        @nro_reserva,
        @cod_reserva_sucursal,
        @fecha_reserva,
        @hora_reserva,
        @nro_restaurante,
        @nro_sucursal,
        @cod_zona,
        @hora_reserva,          -- hora_desde = hora_reserva
        @cant_adultos,
        ISNULL(@cant_menores,0),
        @cod_estado,
        NULL,
        @costo_reserva
    );

/* 7) Devolver fila insertada */
SELECT
    nro_cliente, nro_reserva, cod_reserva_sucursal,
    fecha_reserva, hora_reserva,
    nro_restaurante, nro_sucursal, cod_zona,
    hora_desde, cant_adultos, cant_menores,
    cod_estado, fecha_cancelacion, costo_reserva
FROM dbo.reservas_restaurantes
WHERE nro_cliente = @nro_cliente
  AND nro_reserva = @nro_reserva;

COMMIT;
END TRY
BEGIN CATCH
IF XACT_STATE() <> 0 ROLLBACK;

        -- Si chocó UNIQUE por cod_reserva_sucursal (carrera), devolver la existente
        IF ERROR_NUMBER() IN (2627, 2601)
           AND (CHARINDEX('UQ_reservas_restaurantes_cod', ERROR_MESSAGE()) > 0
                OR CHARINDEX('cod_reserva_sucursal', ERROR_MESSAGE()) > 0)
BEGIN
            DECLARE @cod_reserva_sucursal2 NVARCHAR(50);
            SET @cod_reserva_sucursal2 = CONCAT(@cod_reserva_restaurante, '-', CONVERT(NVARCHAR(10), @nro_sucursal));

SELECT TOP 1
                nro_cliente, nro_reserva, cod_reserva_sucursal,
       fecha_reserva, hora_reserva,
       nro_restaurante, nro_sucursal, cod_zona,
       hora_desde, cant_adultos, cant_menores,
       cod_estado, fecha_cancelacion, costo_reserva
FROM dbo.reservas_restaurantes
WHERE cod_reserva_sucursal = @cod_reserva_sucursal2;
RETURN;
END

        ;THROW
END CATCH
END;
GO

go
CREATE OR ALTER PROCEDURE dbo.sp_listar_restaurantes_home
    @idioma_front VARCHAR(10) = 'es'   -- 'es', 'en', 'es_AR', 'en_US'
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    ------------------------------------------------------------
    -- 0) Resolver nro_idioma (estático)
    ------------------------------------------------------------
    DECLARE @nro_idioma INT;

    SET @nro_idioma =
        CASE
            WHEN @idioma_front LIKE 'es%' THEN 1
            WHEN @idioma_front LIKE 'en%' THEN 2
            ELSE 1 -- default español
END;

    ------------------------------------------------------------
    -- 1) Restaurantes para HOME
    ------------------------------------------------------------
SELECT
    -- 🔐 cifrado estable
    CONVERT(
            VARCHAR(1024),
            ENCRYPTBYPASSPHRASE(
                    CONVERT(VARCHAR(20), r.nro_restaurante),
                    CONVERT(VARCHAR(20), r.nro_restaurante)
            ),
            2
    ) AS nro_restaurante,

    r.razon_social,

    -- 📦 categorías agrupadas SIN duplicados (multi-idioma)
    (
        SELECT
            x.nom_categoria AS categoria,
            STRING_AGG(x.nom_valor_dominio, ',') AS valores
        FROM (
                 SELECT DISTINCT
                     -- categoría según idioma
                     ISNULL(icp.categoria, cp.nom_categoria) AS nom_categoria,

                     -- valor de dominio según idioma
                     ISNULL(idcp.valor_dominio, dcp.nom_valor_dominio) AS nom_valor_dominio
                 FROM dbo.preferencias_restaurantes pr

                          INNER JOIN dbo.categorias_preferencias cp
                                     ON cp.cod_categoria = pr.cod_categoria

                          INNER JOIN dbo.dominio_categorias_preferencias dcp
                                     ON dcp.cod_categoria      = pr.cod_categoria
                                         AND dcp.nro_valor_dominio = pr.nro_valor_dominio

                     -- categoría traducida
                          LEFT JOIN dbo.idiomas_categorias_preferencias icp
                                    ON icp.cod_categoria = pr.cod_categoria
                                        AND icp.nro_idioma    = @nro_idioma

                     -- dominio traducido
                          LEFT JOIN dbo.idiomas_dominio_cat_preferencias idcp
                                    ON idcp.cod_categoria      = pr.cod_categoria
                                        AND idcp.nro_valor_dominio = pr.nro_valor_dominio
                                        AND idcp.nro_idioma        = @nro_idioma

                 WHERE pr.nro_restaurante = r.nro_restaurante
             ) x
        GROUP BY x.nom_categoria
        FOR JSON PATH
        ) AS categorias_json

FROM dbo.restaurantes r
ORDER BY r.razon_social;
END
GO

CREATE OR ALTER PROCEDURE dbo.obtener_reservas_cliente_por_correo
    (
    @correo_cliente NVARCHAR(255)
    )
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    --------------------------------------------------------
    -- 1) Obtener nro_cliente válido
    --------------------------------------------------------
    DECLARE @nro_cliente INT;

SELECT
    @nro_cliente = c.nro_cliente
FROM dbo.clientes c
WHERE c.correo = @correo_cliente
  AND c.habilitado = 1;

IF @nro_cliente IS NULL
BEGIN
        RAISERROR('El cliente no existe o está deshabilitado.', 16, 1);
        RETURN;
END;

    --------------------------------------------------------
    -- 2) Obtener reservas del cliente
    --------------------------------------------------------
SELECT
    -- Cliente
    r.nro_cliente,

    -- Reserva
    r.nro_reserva,
    r.cod_reserva_sucursal,
    r.fecha_reserva,
    r.hora_reserva,
    r.fecha_cancelacion,
    r.costo_reserva,
    r.cant_adultos,
    r.cant_menores,

    -- Estado
    r.cod_estado,
    er.nom_estado,

    -- Restaurante
    r.nro_restaurante,
    res.razon_social AS nombre_restaurante,

    -- Sucursal
    r.nro_sucursal,
    sr.nom_sucursal AS nombre_sucursal,

    -- Zona / Turno (útil para mostrar detalle)
    r.cod_zona,
    r.hora_desde

FROM dbo.reservas_restaurantes r

         INNER JOIN dbo.estados_reservas er
                    ON er.cod_estado = r.cod_estado

         INNER JOIN dbo.restaurantes res
                    ON res.nro_restaurante = r.nro_restaurante

         INNER JOIN dbo.sucursales_restaurantes sr
                    ON sr.nro_restaurante = r.nro_restaurante
                        AND sr.nro_sucursal = r.nro_sucursal

WHERE r.nro_cliente = @nro_cliente

ORDER BY
    r.fecha_reserva DESC,
    r.hora_reserva DESC;
END;
GO

 CREATE OR ALTER PROCEDURE dbo.cancelar_reserva_ristorino_por_codigo
    @cod_reserva_sucursal NVARCHAR(50)
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE
@nro_cliente INT,
        @nro_reserva INT,
        @cod_estado INT,
        @fecha_cancelacion DATETIME;

BEGIN TRY

SELECT
    @nro_cliente = r.nro_cliente,
    @nro_reserva = r.nro_reserva,
    @cod_estado = r.cod_estado,
    @fecha_cancelacion = r.fecha_cancelacion
FROM dbo.reservas_restaurantes r
WHERE r.cod_reserva_sucursal = @cod_reserva_sucursal;

IF (@nro_cliente IS NULL)
BEGIN
SELECT CAST(0 AS BIT) AS success,
       'NOT_FOUND' AS status,
       'Reserva no encontrada en Ristorino.' AS message;
RETURN;
END

        /* Idempotencia */
        IF (@cod_estado = 2 OR @fecha_cancelacion IS NOT NULL)
BEGIN
SELECT CAST(1 AS BIT) AS success,
       'ALREADY_CANCELLED' AS status,
       'La reserva ya estaba cancelada en Ristorino.' AS message;
RETURN;
END

BEGIN TRAN;

UPDATE dbo.reservas_restaurantes
SET fecha_cancelacion = GETDATE(),
    cod_estado = 2
WHERE nro_cliente = @nro_cliente
  AND nro_reserva = @nro_reserva;

COMMIT;

SELECT CAST(1 AS BIT) AS success,
       'CANCELLED' AS status,
       'Cancelación reflejada en Ristorino.' AS message;

END TRY
BEGIN CATCH
IF @@TRANCOUNT > 0 ROLLBACK;

        DECLARE @msg NVARCHAR(4000) = ERROR_MESSAGE();
SELECT CAST(0 AS BIT) AS success,
       'ERROR' AS status,
       @msg AS message;
END CATCH
END;
GO

CREATE OR ALTER PROCEDURE dbo.get_zonas_sucursal
    @nro_restaurante INT,
    @nro_sucursal    INT
    AS
BEGIN
    SET NOCOUNT ON;

    -- Validación básica: que exista la sucursal
    IF NOT EXISTS (
        SELECT 1
        FROM dbo.sucursales_restaurantes sr
        WHERE sr.nro_restaurante = @nro_restaurante
          AND sr.nro_sucursal    = @nro_sucursal
    )
BEGIN
        -- Devuelve vacío (podés THROW si preferís)
SELECT
    CAST(NULL AS INT)          AS codZona,
    CAST(NULL AS NVARCHAR(150)) AS nomZona,
    CAST(NULL AS INT)          AS cantComensales,
    CAST(NULL AS BIT)          AS permiteMenores,
    CAST(NULL AS BIT)          AS habilitada
    WHERE 1 = 0;
RETURN;
END

SELECT
    z.cod_zona        AS codZona,
    z.desc_zona AS nomZona,
    ISNULL(z.cant_comensales, 0) AS cantComensales,
    ISNULL(z.permite_menores, 1) AS permiteMenores,
    ISNULL(z.habilitada, 1)      AS habilitada
FROM dbo.zonas_sucursales_restaurantes z
         LEFT JOIN dbo.idiomas_zonas_suc_restaurantes iz
                   ON iz.nro_restaurante = z.nro_restaurante
                       AND iz.nro_sucursal    = z.nro_sucursal
                       AND iz.cod_zona        = z.cod_zona
                       AND iz.nro_idioma      = 1   -- 👈 ajustá idioma si corresponde
WHERE z.nro_restaurante = @nro_restaurante
  AND z.nro_sucursal    = @nro_sucursal
ORDER BY z.cod_zona;
END
GO

CREATE OR ALTER PROCEDURE dbo.modificar_reserva_ristorino_por_codigo_sucursal
    @cod_reserva_sucursal NVARCHAR(50),

    @fecha_reserva  DATE,
    @hora_reserva   TIME(0),
    @cod_zona       INT,
    @cant_adultos   INT,
    @cant_menores   INT,
    @costo_reserva Decimal(12,2)
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE
        -- datos actuales
@nro_restaurante   INT,
        @nro_sucursal      INT,
        @fecha_actual      DATE,
        @hora_actual       TIME(0),
        @cod_estado        INT,

        -- tolerancia
        @min_tolerencia    INT,
        @ahora             DATETIME = GETDATE(),
        @inicio_reserva    DATETIME,
        @minutos_antes     INT,

        -- validaciones
        @cant_personas     INT;

BEGIN TRY
        ------------------------------------------------------------
        -- 1) Validaciones básicas
        ------------------------------------------------------------
IF @cod_reserva_sucursal IS NULL OR LTRIM(RTRIM(@cod_reserva_sucursal)) = ''
BEGIN
SELECT CAST(0 AS BIT) AS success, 'INVALID' AS status,
       'cod_reserva_sucursal es obligatorio.' AS message;
RETURN;
END

        SET @cant_personas = ISNULL(@cant_adultos,0) + ISNULL(@cant_menores,0);

        IF @cant_personas <= 0
BEGIN
SELECT CAST(0 AS BIT) AS success, 'INVALID' AS status,
       'La cantidad de personas debe ser mayor a 0.' AS message;
RETURN;
END

        IF @fecha_reserva IS NULL OR @hora_reserva IS NULL
BEGIN
SELECT CAST(0 AS BIT) AS success, 'INVALID' AS status,
       'Debe informar fecha_reserva y hora_reserva.' AS message;
RETURN;
END

        ------------------------------------------------------------
        -- 2) Buscar reserva actual en Ristorino
        ------------------------------------------------------------
SELECT
    @nro_restaurante = rr.nro_restaurante,
    @nro_sucursal    = rr.nro_sucursal,
    @fecha_actual    = rr.fecha_reserva,
    @hora_actual     = rr.hora_reserva,
    @cod_estado      = rr.cod_estado
FROM dbo.reservas_restaurantes rr
WHERE rr.cod_reserva_sucursal = @cod_reserva_sucursal;

IF @nro_restaurante IS NULL
BEGIN
SELECT CAST(0 AS BIT) AS success, 'NOT_FOUND' AS status,
       'Reserva no encontrada en Ristorino.' AS message;
RETURN;
END

        ------------------------------------------------------------
        -- 3) (Opcional recomendado) Solo permitir modificar si está pendiente
        --    Ajustá el valor según tu tabla estados_reservas.
        --    Si no querés esta regla, comentá este bloque.
        ------------------------------------------------------------
        IF @cod_estado <> 1
BEGIN
SELECT CAST(0 AS BIT) AS success, 'NOT_ALLOWED' AS status,
       'Solo se pueden modificar reservas pendientes.' AS message;
RETURN;
END

        ------------------------------------------------------------
        -- 4) Validar tolerancia mínima (igual que cancelar)
        --    Se calcula contra la FECHA/HORA ACTUAL de la reserva.
        --    (si preferís contra la nueva, te lo ajusto)
        ------------------------------------------------------------
SELECT @min_tolerencia = s.min_tolerencia_reserva
FROM dbo.sucursales_restaurantes s
WHERE s.nro_restaurante = @nro_restaurante
  AND s.nro_sucursal    = @nro_sucursal;

IF (@min_tolerencia IS NULL) SET @min_tolerencia = 0;

        SET @inicio_reserva =
            DATEADD(SECOND, 0,
              DATEADD(DAY, DATEDIFF(DAY, 0, @fecha_actual),
              CAST(@hora_actual AS DATETIME)));

        SET @minutos_antes = DATEDIFF(MINUTE, @ahora, @inicio_reserva);

        IF (@minutos_antes < @min_tolerencia)
BEGIN
SELECT CAST(0 AS BIT) AS success, 'NOT_ALLOWED' AS status,
       CONCAT('No se puede modificar: tolerancia mínima ', @min_tolerencia,
              ' min. Faltan ', @minutos_antes, ' min para la reserva.') AS message;
RETURN;
END

        ------------------------------------------------------------
        -- 5) Validar FKs: zona y turno existen para la sucursal/restaurante
        --    (esto evita errores FK y devuelve mensajes más claros)
        ------------------------------------------------------------
        IF NOT EXISTS (
            SELECT 1
            FROM dbo.zonas_sucursales_restaurantes z
            WHERE z.nro_restaurante = @nro_restaurante
              AND z.nro_sucursal    = @nro_sucursal
              AND z.cod_zona        = @cod_zona
        )
BEGIN
SELECT CAST(0 AS BIT) AS success, 'INVALID_ZONE' AS status,
       'La zona no existe para esa sucursal en Ristorino.' AS message;
RETURN;
END

        -- En tu tabla el turno FK es por hora_desde, y normalmente coincide con la hora elegida.
        IF NOT EXISTS (
            SELECT 1
            FROM dbo.turnos_sucursales_restaurantes t
            WHERE t.nro_restaurante = @nro_restaurante
              AND t.nro_sucursal    = @nro_sucursal
              AND t.hora_desde      = @hora_reserva
        )
BEGIN
SELECT CAST(0 AS BIT) AS success, 'INVALID_TURNO' AS status,
       'El turno (hora) no existe para esa sucursal en Ristorino.' AS message;
RETURN;
END

        ------------------------------------------------------------
        -- 6) Actualizar reserva (incluye hora_desde para mantener FK)
        ------------------------------------------------------------
BEGIN TRAN;

UPDATE dbo.reservas_restaurantes
SET
    fecha_reserva = @fecha_reserva,
    hora_reserva  = @hora_reserva,
    cod_zona      = @cod_zona,
    hora_desde    = @hora_reserva,   -- ? mantener consistencia con FK de turnos
    cant_adultos  = @cant_adultos,
    cant_menores  = @cant_menores,
    costo_reserva = @costo_reserva
WHERE cod_reserva_sucursal = @cod_reserva_sucursal;

IF @@ROWCOUNT = 0
BEGIN
ROLLBACK;
SELECT CAST(0 AS BIT) AS success, 'NOT_UPDATED' AS status,
       'No se pudo actualizar la reserva en Ristorino.' AS message;
RETURN;
END

COMMIT;

SELECT CAST(1 AS BIT) AS success, 'UPDATED' AS status,
       'Reserva actualizada en Ristorino.' AS message;

END TRY
BEGIN CATCH
IF @@TRANCOUNT > 0 ROLLBACK;

SELECT CAST(0 AS BIT) AS success, 'ERROR' AS status,
       ERROR_MESSAGE() AS message;
END CATCH
END;
GO


go
  CREATE OR ALTER PROCEDURE dbo.obtener_costo_vigente
    @tipo_costo NVARCHAR(50),
    @fecha DATE
    AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @monto DECIMAL(12,2);

SELECT TOP 1
        @monto = c.monto
FROM dbo.costos c
WHERE c.tipo_costo = @tipo_costo
  AND @fecha BETWEEN c.fecha_ini_vigencia
    AND ISNULL(c.fecha_fin_vigencia, '9999-12-31')
ORDER BY c.fecha_ini_vigencia DESC;

-- ? No encontrado
IF @monto IS NULL
BEGIN
        RAISERROR (
            'No existe un costo vigente para el tipo %s en la fecha indicada.',
            16,
            1,
            @tipo_costo
        );
        RETURN;
END

    -- ? Resultado
SELECT @monto AS monto;
END;
GO
/*EXEC dbo.obtener_costo_vigente
     @tipo_costo = 'RESERVA',
     @fecha = '2026-06-15';*/



CREATE OR ALTER PROCEDURE dbo.get_estados
    @idioma_front VARCHAR(10) = 'es' AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
        DECLARE @nro_idioma INT;

    SET @nro_idioma =
        CASE
            WHEN @idioma_front LIKE 'es%' THEN 1
            WHEN @idioma_front LIKE 'en%' THEN 2
            ELSE 1 -- default español
END;
SELECT DISTINCT
    ISNULL(ie.cod_estado,er.cod_estado) as cod_estado ,
    ISNULL(ie.estado,er.nom_estado) as nom_estado
FROM dbo.estados_reservas er
         LEFT JOIN dbo.idiomas_estados ie
                   on er.cod_estado=ie.cod_estado
                       and ie.nro_idioma = @nro_idioma
END;
GO


create or alter procedure dbo.obtener_nroRestaurantes
    as
begin
select
    r.nro_restaurante as nroRestaurante
from dbo.restaurantes r
end
go



CREATE OR ALTER PROCEDURE dbo.sync_restaurante_desde_json_full
    @json NVARCHAR(MAX)
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF @json IS NULL OR ISJSON(@json) <> 1
        THROW 51000, 'JSON inválido o NULL.', 1;

BEGIN TRY
BEGIN TRAN;

        ------------------------------------------------------------
        -- 1) RESTAURANTE
        ------------------------------------------------------------
        DECLARE @nro_restaurante INT;
        DECLARE @razon_social   NVARCHAR(200);
        DECLARE @cuit           NVARCHAR(20);

SELECT
    @nro_restaurante = j.nro_restaurante,
    @razon_social    = j.razon_social,
    @cuit            = j.cuit
FROM OPENJSON(@json)
    WITH (
    nro_restaurante INT           '$.nroRestaurante',
    razon_social    NVARCHAR(200) '$.razonSocial',
    cuit            NVARCHAR(20)  '$.cuit'
    ) j;

IF @nro_restaurante IS NULL
            THROW 51001, 'El JSON no trae nroRestaurante.', 1;

        IF EXISTS (SELECT 1 FROM dbo.restaurantes WHERE nro_restaurante = @nro_restaurante)
BEGIN
UPDATE dbo.restaurantes
SET razon_social = @razon_social,
    cuit        = @cuit
WHERE nro_restaurante = @nro_restaurante;
END
ELSE
BEGIN
INSERT INTO dbo.restaurantes (nro_restaurante, razon_social, cuit)
VALUES (@nro_restaurante, @razon_social, @cuit);
END

        ------------------------------------------------------------
        -- 2) STAGING: SUCURSALES
        ------------------------------------------------------------
        DECLARE @Sucursales TABLE(
            nro_sucursal INT NOT NULL,
            nom_sucursal NVARCHAR(150) NOT NULL,
            calle NVARCHAR(150) NULL,
            nro_calle INT NULL,
            barrio NVARCHAR(120) NULL,
            nro_localidad INT NOT NULL,
            cod_postal NVARCHAR(20) NULL,
            telefonos NVARCHAR(100) NULL,
            total_comensales INT NULL,
            min_tolerencia_reserva INT NULL,
            cod_sucursal_restaurante NVARCHAR(50) NOT NULL
        );

INSERT INTO @Sucursales
SELECT
    s.nroSucursal,
    s.nomSucursal,
    s.calle,
    s.nroCalle,
    s.barrio,
    s.nroLocalidad,
    s.codPostal,
    s.telefonos,
    s.totalComensales,
    s.minTolerenciaReserva,
    CONCAT(@nro_restaurante, '-', s.nroSucursal)
FROM OPENJSON(@json, '$.sucursales')
    WITH (
    nroSucursal INT,
    nomSucursal NVARCHAR(150),
    calle NVARCHAR(150),
    nroCalle INT,
    barrio NVARCHAR(120),
    nroLocalidad INT,
    codPostal NVARCHAR(20),
    telefonos NVARCHAR(100),
    totalComensales INT,
    minTolerenciaReserva INT
    ) s;

------------------------------------------------------------
-- 3) UPSERT: SUCURSALES (NO DELETE)
------------------------------------------------------------
MERGE dbo.sucursales_restaurantes AS T
    USING @Sucursales AS S
    ON  T.nro_restaurante = @nro_restaurante
    AND T.nro_sucursal    = S.nro_sucursal
    WHEN MATCHED THEN
UPDATE SET
    nom_sucursal            = S.nom_sucursal,
    calle                  = S.calle,
    nro_calle              = S.nro_calle,
    barrio                 = S.barrio,
    nro_localidad          = S.nro_localidad,
    cod_postal             = S.cod_postal,
    telefonos              = S.telefonos,
    total_comensales       = S.total_comensales,
    min_tolerencia_reserva = S.min_tolerencia_reserva,
    cod_sucursal_restaurante = S.cod_sucursal_restaurante
    WHEN NOT MATCHED THEN
INSERT (nro_restaurante, nro_sucursal, nom_sucursal, calle, nro_calle, barrio,
nro_localidad, cod_postal, telefonos, total_comensales,
min_tolerencia_reserva, cod_sucursal_restaurante)
VALUES (@nro_restaurante, S.nro_sucursal, S.nom_sucursal, S.calle, S.nro_calle, S.barrio,
    S.nro_localidad, S.cod_postal, S.telefonos, S.total_comensales,
    S.min_tolerencia_reserva, S.cod_sucursal_restaurante);

------------------------------------------------------------
-- 4) STAGING: ZONAS
------------------------------------------------------------
DECLARE @Zonas TABLE(
            nro_sucursal INT NOT NULL,
            cod_zona INT NOT NULL,
            desc_zona NVARCHAR(200) NULL,
            cant_comensales INT NULL,
            permite_menores BIT NULL,
            habilitada BIT NULL
        );

INSERT INTO @Zonas
SELECT
    s.nroSucursal,
    z.codZona,
    z.nomZona,
    z.cantComensales,
    z.permiteMenores,
    z.habilitada
FROM OPENJSON(@json,'$.sucursales')
    WITH (nroSucursal INT, zonas NVARCHAR(MAX) AS JSON) s
    OUTER APPLY OPENJSON(s.zonas)
WITH (
    codZona INT,
    nomZona NVARCHAR(200),
    cantComensales INT,
    permiteMenores BIT,
    habilitada BIT
    ) z;

------------------------------------------------------------
-- 5) UPSERT: ZONAS (soft delete si desaparecen y tienen reservas)
------------------------------------------------------------
MERGE dbo.zonas_sucursales_restaurantes AS T
    USING @Zonas AS S
    ON  T.nro_restaurante = @nro_restaurante
    AND T.nro_sucursal    = S.nro_sucursal
    AND T.cod_zona        = S.cod_zona
    WHEN MATCHED THEN
UPDATE SET
    desc_zona       = S.desc_zona,
    cant_comensales = S.cant_comensales,
    permite_menores = ISNULL(S.permite_menores, 1),
    habilitada      = ISNULL(S.habilitada, 1)
    WHEN NOT MATCHED THEN
INSERT (nro_restaurante, nro_sucursal, cod_zona, desc_zona, cant_comensales, permite_menores, habilitada)
VALUES (@nro_restaurante, S.nro_sucursal, S.cod_zona, S.desc_zona, S.cant_comensales,
    ISNULL(S.permite_menores,1), ISNULL(S.habilitada,1));

;WITH ZonasFaltantes AS (
    SELECT T.nro_sucursal, T.cod_zona
    FROM dbo.zonas_sucursales_restaurantes T
    WHERE T.nro_restaurante = @nro_restaurante
      AND NOT EXISTS (
        SELECT 1 FROM @Zonas S
        WHERE S.nro_sucursal = T.nro_sucursal
          AND S.cod_zona     = T.cod_zona
    )
)
UPDATE Z
SET habilitada = 0
    FROM dbo.zonas_sucursales_restaurantes Z
        JOIN ZonasFaltantes F
ON F.nro_sucursal = Z.nro_sucursal AND F.cod_zona = Z.cod_zona
WHERE Z.nro_restaurante = @nro_restaurante
  AND EXISTS (
    SELECT 1 FROM dbo.reservas_restaurantes R
    WHERE R.nro_restaurante = Z.nro_restaurante
  AND R.nro_sucursal    = Z.nro_sucursal
  AND R.cod_zona        = Z.cod_zona
    );

------------------------------------------------------------
-- 6) STAGING: TURNOS
------------------------------------------------------------
DECLARE @Turnos TABLE(
            nro_sucursal INT NOT NULL,
            hora_desde TIME(0) NOT NULL,
            hora_hasta TIME(0) NULL,
            habilitado BIT NULL
        );

INSERT INTO @Turnos
SELECT
    s.nroSucursal,
    t.horaDesde,
    t.horaHasta,
    t.habilitado
FROM OPENJSON(@json,'$.sucursales')
    WITH (nroSucursal INT, turnos NVARCHAR(MAX) AS JSON) s
    OUTER APPLY OPENJSON(s.turnos)
WITH (horaDesde TIME(0), horaHasta TIME(0), habilitado BIT) t;

------------------------------------------------------------
-- 7) UPSERT: TURNOS (soft delete habilitado=0 si desaparecen)
------------------------------------------------------------
MERGE dbo.turnos_sucursales_restaurantes AS T
    USING @Turnos AS S
    ON  T.nro_restaurante = @nro_restaurante
    AND T.nro_sucursal    = S.nro_sucursal
    AND T.hora_desde      = S.hora_desde
    WHEN MATCHED THEN
UPDATE SET
    hora_hasta = S.hora_hasta,
    habilitado = ISNULL(S.habilitado, 1)
    WHEN NOT MATCHED THEN
INSERT (nro_restaurante, nro_sucursal, hora_desde, hora_hasta, habilitado)
VALUES (@nro_restaurante, S.nro_sucursal, S.hora_desde, S.hora_hasta, ISNULL(S.habilitado,1));

UPDATE T
SET habilitado = 0
    FROM dbo.turnos_sucursales_restaurantes T
WHERE T.nro_restaurante = @nro_restaurante
  AND NOT EXISTS (
    SELECT 1 FROM @Turnos S
    WHERE S.nro_sucursal = T.nro_sucursal
  AND S.hora_desde   = T.hora_desde
    );

------------------------------------------------------------
-- 8) ZONAS_TURNOS: full replace
------------------------------------------------------------
DELETE FROM dbo.zonas_turnos_sucurales_restaurantes
WHERE nro_restaurante = @nro_restaurante;

INSERT INTO dbo.zonas_turnos_sucurales_restaurantes
(nro_restaurante, nro_sucursal, cod_zona, hora_desde, permite_menores)
SELECT
    @nro_restaurante,
    s.nroSucursal,
    z.codZona,
    z.horaDesde,
    ISNULL(z.permiteMenores, 1)
FROM OPENJSON(@json,'$.sucursales')
    WITH (nroSucursal INT, zonasTurnos NVARCHAR(MAX) AS JSON) s
    OUTER APPLY OPENJSON(s.zonasTurnos)
WITH (codZona INT, horaDesde TIME(0), permiteMenores BIT) z;

------------------------------------------------------------
-- 9) CATEGORÍAS (asegurar)
------------------------------------------------------------
DECLARE @CAT_ESTILOS INT, @CAT_ESPECIALIDADES INT, @CAT_TIPOS INT;

SELECT @CAT_ESTILOS = cod_categoria
FROM dbo.categorias_preferencias
WHERE nom_categoria = 'ESTILOS';
IF @CAT_ESTILOS IS NULL
BEGIN
SELECT @CAT_ESTILOS = ISNULL(MAX(cod_categoria),0)+1 FROM dbo.categorias_preferencias;
INSERT INTO dbo.categorias_preferencias (cod_categoria, nom_categoria)
VALUES (@CAT_ESTILOS, 'ESTILOS');
END

SELECT @CAT_ESPECIALIDADES = cod_categoria
FROM dbo.categorias_preferencias
WHERE nom_categoria = 'ESPECIALIDADES';
IF @CAT_ESPECIALIDADES IS NULL
BEGIN
SELECT @CAT_ESPECIALIDADES = ISNULL(MAX(cod_categoria),0)+1 FROM dbo.categorias_preferencias;
INSERT INTO dbo.categorias_preferencias (cod_categoria, nom_categoria)
VALUES (@CAT_ESPECIALIDADES, 'ESPECIALIDADES');
END

SELECT @CAT_TIPOS = cod_categoria
FROM dbo.categorias_preferencias
WHERE nom_categoria = 'TIPOS_COMIDAS';
IF @CAT_TIPOS IS NULL
BEGIN
SELECT @CAT_TIPOS = ISNULL(MAX(cod_categoria),0)+1 FROM dbo.categorias_preferencias;
INSERT INTO dbo.categorias_preferencias (cod_categoria, nom_categoria)
VALUES (@CAT_TIPOS, 'TIPOS_COMIDAS');
END

        ------------------------------------------------------------
        -- 10) PREFRAW (DECLARADA ACÁ, antes de usar)
        ------------------------------------------------------------
        DECLARE @PrefRaw TABLE(
            nro_sucursal INT NOT NULL,
            cod_categoria INT NOT NULL,
            nro_valor_dominio INT NOT NULL,
            nom_valor_dominio NVARCHAR(150) NOT NULL
        );

        ------------------------------------------------------------
        -- 11) Opción B por NOMBRE: staging por categoría
        ------------------------------------------------------------
        DECLARE @EstilosIn TABLE (nro_sucursal INT NOT NULL, nom_val NVARCHAR(150) NOT NULL);
        DECLARE @EspecialidadesIn TABLE (nro_sucursal INT NOT NULL, nom_val NVARCHAR(150) NOT NULL);
        DECLARE @TiposIn TABLE (nro_sucursal INT NOT NULL, nom_val NVARCHAR(150) NOT NULL);

        -- ESTILOS
INSERT INTO @EstilosIn (nro_sucursal, nom_val)
SELECT DISTINCT
    s.nroSucursal,
    LTRIM(RTRIM(e.nomEstilo))
FROM OPENJSON(@json,'$.sucursales')
    WITH (nroSucursal INT, estilos NVARCHAR(MAX) AS JSON) s
    OUTER APPLY OPENJSON(s.estilos)
WITH (nomEstilo NVARCHAR(150), habilitado BIT) e
WHERE ISNULL(e.habilitado,1)=1
  AND e.nomEstilo IS NOT NULL
  AND LTRIM(RTRIM(e.nomEstilo)) <> '';

-- ESPECIALIDADES
INSERT INTO @EspecialidadesIn (nro_sucursal, nom_val)
SELECT DISTINCT
    s.nroSucursal,
    LTRIM(RTRIM(e.nomRestriccion))
FROM OPENJSON(@json,'$.sucursales')
    WITH (nroSucursal INT, especialidades NVARCHAR(MAX) AS JSON) s
    OUTER APPLY OPENJSON(s.especialidades)
WITH (nomRestriccion NVARCHAR(150), habilitada BIT) e
WHERE ISNULL(e.habilitada,1)=1
  AND e.nomRestriccion IS NOT NULL
  AND LTRIM(RTRIM(e.nomRestriccion)) <> '';

-- TIPOS_COMIDAS
INSERT INTO @TiposIn (nro_sucursal, nom_val)
SELECT DISTINCT
    s.nroSucursal,
    LTRIM(RTRIM(t.nomTipoComida))
FROM OPENJSON(@json,'$.sucursales')
    WITH (nroSucursal INT, tiposComidas NVARCHAR(MAX) AS JSON) s
    OUTER APPLY OPENJSON(s.tiposComidas)
WITH (nomTipoComida NVARCHAR(150), habilitado BIT) t
WHERE ISNULL(t.habilitado,1)=1
  AND t.nomTipoComida IS NOT NULL
  AND LTRIM(RTRIM(t.nomTipoComida)) <> '';

------------------------------------------------------------
-- 11.1) Dominio + PrefRaw: ESTILOS
------------------------------------------------------------
DECLARE @MaxEst INT = (
            SELECT ISNULL(MAX(nro_valor_dominio),0)
            FROM dbo.dominio_categorias_preferencias
            WHERE cod_categoria = @CAT_ESTILOS
        );

        ;WITH MissingNames AS (
    SELECT DISTINCT I.nom_val
    FROM @EstilosIn I
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.dominio_categorias_preferencias D
        WHERE D.cod_categoria = @CAT_ESTILOS
          AND LTRIM(RTRIM(D.nom_valor_dominio)) = I.nom_val
    )
),
              ToInsert AS (
                  SELECT
                      @CAT_ESTILOS AS cod_categoria,
                      @MaxEst + ROW_NUMBER() OVER (ORDER BY nom_val) AS nro_valor_dominio,
                          nom_val AS nom_valor_dominio
                  FROM MissingNames
              )
         INSERT INTO dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio)
SELECT cod_categoria, nro_valor_dominio, nom_valor_dominio
FROM ToInsert;

INSERT INTO @PrefRaw (nro_sucursal, cod_categoria, nro_valor_dominio, nom_valor_dominio)
SELECT
    I.nro_sucursal,
    @CAT_ESTILOS,
    D.nro_valor_dominio,
    I.nom_val
FROM @EstilosIn I
         JOIN dbo.dominio_categorias_preferencias D
              ON D.cod_categoria = @CAT_ESTILOS
                  AND LTRIM(RTRIM(D.nom_valor_dominio)) = I.nom_val;

------------------------------------------------------------
-- 11.2) Dominio + PrefRaw: ESPECIALIDADES
------------------------------------------------------------
DECLARE @MaxEsp INT = (
            SELECT ISNULL(MAX(nro_valor_dominio),0)
            FROM dbo.dominio_categorias_preferencias
            WHERE cod_categoria = @CAT_ESPECIALIDADES
        );

        ;WITH MissingNames AS (
    SELECT DISTINCT I.nom_val
    FROM @EspecialidadesIn I
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.dominio_categorias_preferencias D
        WHERE D.cod_categoria = @CAT_ESPECIALIDADES
          AND LTRIM(RTRIM(D.nom_valor_dominio)) = I.nom_val
    )
),
              ToInsert AS (
                  SELECT
                      @CAT_ESPECIALIDADES AS cod_categoria,
                      @MaxEsp + ROW_NUMBER() OVER (ORDER BY nom_val) AS nro_valor_dominio,
                          nom_val AS nom_valor_dominio
                  FROM MissingNames
              )
         INSERT INTO dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio)
SELECT cod_categoria, nro_valor_dominio, nom_valor_dominio
FROM ToInsert;

INSERT INTO @PrefRaw (nro_sucursal, cod_categoria, nro_valor_dominio, nom_valor_dominio)
SELECT
    I.nro_sucursal,
    @CAT_ESPECIALIDADES,
    D.nro_valor_dominio,
    I.nom_val
FROM @EspecialidadesIn I
         JOIN dbo.dominio_categorias_preferencias D
              ON D.cod_categoria = @CAT_ESPECIALIDADES
                  AND LTRIM(RTRIM(D.nom_valor_dominio)) = I.nom_val;

------------------------------------------------------------
-- 11.3) Dominio + PrefRaw: TIPOS_COMIDAS
------------------------------------------------------------
DECLARE @MaxTipos INT = (
            SELECT ISNULL(MAX(nro_valor_dominio),0)
            FROM dbo.dominio_categorias_preferencias
            WHERE cod_categoria = @CAT_TIPOS
        );

        ;WITH MissingNames AS (
    SELECT DISTINCT I.nom_val
    FROM @TiposIn I
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.dominio_categorias_preferencias D
        WHERE D.cod_categoria = @CAT_TIPOS
          AND LTRIM(RTRIM(D.nom_valor_dominio)) = I.nom_val
    )
),
              ToInsert AS (
                  SELECT
                      @CAT_TIPOS AS cod_categoria,
                      @MaxTipos + ROW_NUMBER() OVER (ORDER BY nom_val) AS nro_valor_dominio,
                          nom_val AS nom_valor_dominio
                  FROM MissingNames
              )
         INSERT INTO dbo.dominio_categorias_preferencias (cod_categoria, nro_valor_dominio, nom_valor_dominio)
SELECT cod_categoria, nro_valor_dominio, nom_valor_dominio
FROM ToInsert;

INSERT INTO @PrefRaw (nro_sucursal, cod_categoria, nro_valor_dominio, nom_valor_dominio)
SELECT
    I.nro_sucursal,
    @CAT_TIPOS,
    D.nro_valor_dominio,
    I.nom_val
FROM @TiposIn I
         JOIN dbo.dominio_categorias_preferencias D
              ON D.cod_categoria = @CAT_TIPOS
                  AND LTRIM(RTRIM(D.nom_valor_dominio)) = I.nom_val;

------------------------------------------------------------
-- 12) UPSERT preferencias_restaurantes SIN romper FKs
------------------------------------------------------------
DECLARE @PrefBase INT = (
            SELECT ISNULL(MAX(nro_preferencia),0)
            FROM dbo.preferencias_restaurantes
            WHERE nro_restaurante = @nro_restaurante
        );

        ;WITH Missing AS (
    SELECT DISTINCT
        @nro_restaurante AS nro_restaurante,
        P.nro_sucursal,
        P.cod_categoria,
        P.nro_valor_dominio,
        ROW_NUMBER() OVER (
                    ORDER BY P.cod_categoria, P.nro_valor_dominio, P.nro_sucursal
                ) AS rn
    FROM @PrefRaw P
    WHERE NOT EXISTS (
        SELECT 1
        FROM dbo.preferencias_restaurantes PR
        WHERE PR.nro_restaurante   = @nro_restaurante
          AND PR.nro_sucursal      = P.nro_sucursal
          AND PR.cod_categoria     = P.cod_categoria
          AND PR.nro_valor_dominio = P.nro_valor_dominio
    )
)
         INSERT INTO dbo.preferencias_restaurantes
            (nro_restaurante, cod_categoria, nro_valor_dominio, nro_preferencia, observaciones, nro_sucursal)
SELECT
    nro_restaurante,
    cod_categoria,
    nro_valor_dominio,
    @PrefBase + rn,
    NULL,
    nro_sucursal
FROM Missing;

-- Borrar solo si NO viene y NO está usada por reservas
DELETE PR
        FROM dbo.preferencias_restaurantes PR
        WHERE PR.nro_restaurante = @nro_restaurante
          AND PR.cod_categoria IN (@CAT_ESTILOS, @CAT_ESPECIALIDADES, @CAT_TIPOS)
          AND NOT EXISTS (
              SELECT 1
              FROM @PrefRaw P
              WHERE P.nro_sucursal      = PR.nro_sucursal
                AND P.cod_categoria     = PR.cod_categoria
                AND P.nro_valor_dominio = PR.nro_valor_dominio
          )
          AND NOT EXISTS (
              SELECT 1
              FROM dbo.preferencias_reservas_restaurantes RR
              WHERE RR.nro_restaurante = PR.nro_restaurante
                AND RR.nro_preferencia = PR.nro_preferencia
          );

COMMIT;
END TRY
BEGIN CATCH
IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
END CATCH
END;
GO





/*

select * from restaurantes
select * from sucursales_restaurantes
SELECT * FROM dbo.zonas_sucursales_restaurantes;
SELECT * FROM dbo.turnos_sucursales_restaurantes;
SELECT * FROM dbo.zonas_turnos_sucurales_restaurantes;


SELECT * from dbo.categorias_preferencias
select * from dbo.dominio_categorias_preferencias
select * from dbo.preferencias_restaurantes



select * from dbo.contenidos_restaurantes
select * from dbo.reservas_restaurantes
*/




