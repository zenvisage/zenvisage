<!--This is a clean instruction for building a Postgres Server for zenvisage-->

1. Download Postgres server on your local machine;

2. Postgres server must contain a user with:

	username:postgres
	password:(not listed here, you know what it is for our server)

3. Run clean up query and setup query;
	drop schema public cascade;
	create schema public;
	CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);
	CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);

4. open http://localhost:8080/chi-index.html

5. upload (csv, txt) tuples using the servers file upload functionality;
  it will auto creat csv table, insert metafilelocation table && metatable;

6. now you can use the dataset for plotting and similarity search;

7. Be-aware that column names of csv table can not contain - such as:  Foreclosures-Ratio, should be ForeclosuresRatio;
