-- =============================================================
-- Script 1: Crear base de datos y tabla
-- =============================================================

\echo '>>> Creando base de datos bastion...'

CREATE DATABASE bastion;

\echo '>>> Conectando a la base de datos bastion...'
\c bastion

\echo '>>> Creando tabla connections...'

CREATE TABLE connections (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100)
);

\echo '>>> Base de datos y tabla creadas.'
\dt