/*This is a clean instruction for building a Postgres Server for zenvisage*/

1. Postgres server must contain a user with:
	username:postgres
	password:(not listed here, you know what it is for our server)

2. Run clean up query and setup query;
drop table real_estate; 
drop table name cmu;
drop table name cmutesting;
drop table sales;

drop table zenvisage_metafilelocation;
drop table zenvisage_metatable;

CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);
CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);

3. upload csv,txt using the running servers file upload functionality;
  it will auto creat csv table, insert metafilelocation table && metatable;

4. now you can use the dataset for plotting and similarity search;
