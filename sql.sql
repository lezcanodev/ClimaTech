-- CREAMOS LA BASE DE DATOS
-- CREATE DATABASE climatech;
\c climatech;


--------------------------------
-- TABLA PARA ESTACIONES METEOROLÓGICAS (emisores UDP)
--------------------------------
CREATE TABLE IF NOT EXISTS estaciones_meteorologicas (
    id_estacion     VARCHAR(20) PRIMARY KEY,
    latitud         DECIMAL(9,6) NOT NULL,
    longitud        DECIMAL(9,6) NOT NULL,
    nombre          VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_ubicacion ON estaciones_meteorologicas (latitud, longitud);

-------------------------------- 
-- TABLA PARA GUARDAR LOS DATOS CLIMÁTICOS
--------------------------------
CREATE TABLE IF NOT EXISTS mediciones_climaticas (
    id                  SERIAL PRIMARY KEY,
    id_estacion         VARCHAR(20) NOT NULL REFERENCES estaciones_meteorologicas(id_estacion),
    fecha_hora_medicion TIMESTAMP NOT NULL,
    temperatura         DECIMAL(5,2) NOT NULL,
    sensacion_termica   DECIMAL(5,2),
    condicion           VARCHAR(80),
    probabilidad_lluvia SMALLINT
);

CREATE INDEX IF NOT EXISTS idx_estacion_fecha ON mediciones_climaticas (id_estacion, fecha_hora_medicion);

--------------------------------
-- TABLA PARA PRONÓSTICO EXTENDIDO (servicio TCP)
--------------------------------
CREATE TABLE IF NOT EXISTS pronostico_diario (
    id                  SERIAL PRIMARY KEY,
    id_estacion         VARCHAR(20) NOT NULL REFERENCES estaciones_meteorologicas(id_estacion),
    fecha               DATE NOT NULL,
    temp_maxima         DECIMAL(5,2) NOT NULL,
    temp_minima         DECIMAL(5,2) NOT NULL,
    condicion           VARCHAR(80),
    probabilidad_lluvia SMALLINT,
    UNIQUE (id_estacion, fecha)
);

CREATE INDEX IF NOT EXISTS idx_pronostico_estacion_fecha ON pronostico_diario (id_estacion, fecha);

--------------------------------
-- TABLA PARA SUSCRIPCIONES DE ALERTAS (servicio TCP)
--------------------------------
CREATE TABLE IF NOT EXISTS suscripciones_alertas (
    id_suscripcion      VARCHAR(20) PRIMARY KEY,
    sistema_suscriptor  VARCHAR(100) NOT NULL,
    latitud             DECIMAL(9,6) NOT NULL,
    longitud            DECIMAL(9,6) NOT NULL,
    radio_km            SMALLINT NOT NULL,
    tipos_alerta        TEXT NOT NULL,
    url_callback        VARCHAR(500) NOT NULL,
    estado              VARCHAR(20) NOT NULL,
    fecha_registro      TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_suscripcion_estado ON suscripciones_alertas (estado);