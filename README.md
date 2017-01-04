# Overview
zenvisage is designed as a lightweight web-based client application. It provides the user an intuitive graphical interface for specifying trends and insights in data, automatically seaches for user-specified insights in data, and encodes the results into the most effective visualizations.

For more details, please look at our [Project Webpage] (http://zenvisage.github.io/)

# Compilation

## Requirements

* Install [Apache Maven 3.0.5] (https://maven.apache.org/)
brew install maven

* Install Java 1.8
UPDATE JAVA_HOME to your installed java folder.

* Install Postgresql database
brew install postgres


## Install Postgres
(1) Download Postgres server on your local machine;

(2) Postgres server must contain a user with:

username:  postgres
password: zenvisage


* create a default db with current username
createdb

* create a specific username with no password
createuser <username> -w

* create a db for a user
createdb -O<username> -Eutf8 <dbname>

* log into the db
psql <dbname>

Method One:
Run clean up query and setup query:
\connect postgres;
DROP schema public cascade;
CREATE schema public;
CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);
CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);




## Building Code and Deployment

*  git clone https://github.com/zenvisage/zenvisage.git
*  cd zenvisage
*  sh build.sh

## Deploy code
*  sh run.sh
*  http://localhost:8080/
*  Launch http://localhost:8080/

## Upload files
( Be aware that column names of csv&&txt table can not contain '-'
(e.g.Foreclosures-Ratio, should be ForeclosuresRatio);
Upload (csv, txt) tuples (e.g. real_estate.csv, real_estate.txt) using the front-end file upload functionality;
The file upload back-end will auto creat csv table, insert metafilelocation table && metatable;

Now you can select the dataset from dropdown on front-end plotting and similarity search;


Method Two:(Need Revamp, don't use this for now) (Fast loading 3 databases without uploading schema and files 3 times and wait)

(1)
Using Command Line Tools with Postgres.app
Configure your $PATH
Postgres.app includes many command line tools. If you want to use them, you must configure the $PATH variable.

If you are using bash (default shell on OS X), add the following line to ~/.bash_profile:

source  ~/.bash_profile:

export PATH=$PATH:/Applications/Postgres.app/Contents/Versions/latest/bin

(2)
zevisange.sql is at zenvisage project root folder

First drop all schema in postgres terminal: DROP schema public cascade;

use command in regular terminal not in postgres terminal:

psql postgres < /path_to_zevnisage.sql/zenvisage.sql

(to dump database: use pg_dump postgres > /path_to_zevnisage.sql/zenvisage.sql)

