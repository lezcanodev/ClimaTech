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

CREATE INDEX idx_ubicacion ON estaciones_meteorologicas (latitud, longitud);

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

CREATE INDEX idx_estacion_fecha ON mediciones_climaticas (id_estacion, fecha_hora_medicion);