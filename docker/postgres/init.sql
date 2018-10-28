ALTER USER postgres WITH SUPERUSER;
DROP schema public cascade; 
CREATE schema public; 
CREATE TABLE zenvisage_metatable (tablename TEXT, attribute TEXT, type TEXT, axis TEXT, min FLOAT, max FLOAT, selectedx BOOLEAN, selectedy BOOLEAN, selectedz BOOLEAN); 
CREATE TABLE zenvisage_dynamic_classes (tablename TEXT, attribute TEXT, ranges TEXT);
CREATE TABLE dynamic_class_aggregations (Table_Name TEXT NOT NULL, Tag TEXT NOT NULL, Attributes TEXT NOT NULL, Ranges TEXT NOT NULL, Count INT NOT NULL);
CREATE TABLE users (id TEXT, password TEXT);
CREATE TABLE users_tables (users TEXT, tables TEXT);
INSERT INTO users_tables (users, tables) VALUES ('public', 'cmu'), ('public', 'flights'),('public', 'real_estate'),('public', 'weather'),('public', 'real_estate_tutorial');