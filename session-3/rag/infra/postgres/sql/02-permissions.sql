-- 3. Grant Permissions
-- Ensure the application user has full access to the database and public schema
GRANT ALL PRIVILEGES ON DATABASE {{POSTGRES_DB}} TO {{POSTGRES_USER}};
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO {{POSTGRES_USER}};
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO {{POSTGRES_USER}};
