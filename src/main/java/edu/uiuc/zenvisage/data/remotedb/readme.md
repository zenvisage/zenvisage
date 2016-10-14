<!--This is a clean instruction for building a Postgres Server for zenvisage-->

1. Download Postgres server on your local machine;

2. Postgres server must contain a user with:

username:postgres
password:(not listed here, you know what it is for our server)

3. Run clean up query and setup query:

DROP schema public cascade;
CREATE schema public;
CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);
CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);

4. open http://localhost:8080/chi-index.html

5. Be-aware that column names of csv&&txt table can not contain '-' 
(e.g.Foreclosures-Ratio, should be ForeclosuresRatio);
Upload (csv, txt) tuples (e.g. real_estate.csv, real_estate.txt) using the servers file upload functionality;
The file upload back-end will auto creat csv table, insert metafilelocation table && metatable;

6. now you can select the dataset from dropdown on front-end plotting and similarity search;

