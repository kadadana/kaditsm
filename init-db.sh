#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- 1. Auth Service (User & DB)
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${AUTH_DB_USER:-auth_user}') THEN
            CREATE ROLE ${AUTH_DB_USER:-auth_user} WITH LOGIN PASSWORD '${AUTH_DB_PASSWORD:-auth_pass}';
        END IF;
    END
    \$\$;

    SELECT 'CREATE DATABASE ${AUTH_DB_NAME:-auth_db} OWNER ${AUTH_DB_USER:-auth_user}'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${AUTH_DB_NAME:-auth_db}')\gexec

    GRANT ALL PRIVILEGES ON DATABASE ${AUTH_DB_NAME:-auth_db} TO ${AUTH_DB_USER:-auth_user};

    -- 2. Tenant Service (User & DB)
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${TENANT_DB_USER:-tenant_user}') THEN
            CREATE ROLE ${TENANT_DB_USER:-tenant_user} WITH LOGIN PASSWORD '${TENANT_DB_PASSWORD:-tenant_pass}';
        END IF;
    END
    \$\$;

    SELECT 'CREATE DATABASE ${TENANT_DB_NAME:-tenant_db} OWNER ${TENANT_DB_USER:-tenant_user}'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${TENANT_DB_NAME:-tenant_db}')\gexec

    GRANT ALL PRIVILEGES ON DATABASE ${TENANT_DB_NAME:-tenant_db} TO ${TENANT_DB_USER:-tenant_user};

    -- 3. User Service (User & DB)
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${USER_DB_USER:-user_user}') THEN
            CREATE ROLE ${USER_DB_USER:-user_user} WITH LOGIN PASSWORD '${USER_DB_PASSWORD:-user_pass}';
        END IF;
    END
    \$\$;

    SELECT 'CREATE DATABASE ${USER_DB_NAME:-user_db} OWNER ${USER_DB_USER:-user_user}'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${USER_DB_NAME:-user_db}')\gexec

    GRANT ALL PRIVILEGES ON DATABASE ${USER_DB_NAME:-user_db} TO ${USER_DB_USER:-user_user};
EOSQL