-- =============================================================
-- Script 2: Insertar dato y contar registros
-- =============================================================

\echo '>>> Conectando a la base de datos bastion...'
\c bastion

\echo '>>> Insertando un registro...'

INSERT INTO connections (name)
VALUES ('connections');

\echo '>>> Contando registros...'

SELECT COUNT(*) AS total_connections FROM connections;

\echo '>>> Mostrando datos...'

SELECT * FROM connections;