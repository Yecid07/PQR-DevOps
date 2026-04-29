-- =============================================================
-- Script 3: Eliminar base de datos bastion
-- =============================================================

\echo '>>> Terminando conexiones activas...'

SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'bastion';

\echo '>>> Eliminando base de datos bastion...'

DROP DATABASE IF EXISTS bastion;

\echo '>>> Base de datos eliminada.'