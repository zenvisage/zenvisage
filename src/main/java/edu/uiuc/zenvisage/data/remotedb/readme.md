<!--This is a clean instruction for building a Postgres Server for zenvisage-->

(1) Download Postgres server on your local machine;

(2) Postgres server must contain a user with:

username:postgres
password:(not listed here, you know what it is for our server)


Method One:
(1) Run clean up query and setup query:

DROP schema public cascade;
CREATE schema public;
CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);
CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);

(2) open http://localhost:8080/chi-index.html

(3) Be aware that column names of csv&&txt table can not contain '-' 
(e.g.Foreclosures-Ratio, should be ForeclosuresRatio);
Upload (csv, txt) tuples (e.g. real_estate.csv, real_estate.txt) using the front-end file upload functionality;
The file upload back-end will auto creat csv table, insert metafilelocation table && metatable;

(4) Now you can select the dataset from dropdown on front-end plotting and similarity search;

Method Two:
Load zenvisage.sql to your postgres database with databasename: postgres


Method Two: (Fast loading 4 databases without uploading schema and files 4 times and wait)

(1)
Using Command Line Tools with Postgres.app
Configure your $PATH
Postgres.app includes many command line tools. If you want to use them, you must configure the $PATH variable.

If you are using bash (default shell on OS X), add the following line to ~/.bash_profile:

export PATH=$PATH:/Applications/Postgres.app/Contents/Versions/latest/bin

(2)
zevisange.sql is at zenvisage project root folder
use command:
psql postgres < /path_to_zevnisage.sql/zenvisage.sql

(to dump database: use pg_dump postgres > /path_to_zevnisage.sql/zenvisage.sql)



