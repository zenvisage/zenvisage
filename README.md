# Overview
zenvisage is designed as a lightweight web-based client application. It provides the user an intuitive graphical interface for specifying trends and insights in data, automatically seaches for user-specified insights in data, and encodes the results into the most effective visualizations.

For more details, please look at our [Project Webpage] (http://zenvisage.github.io/)

# Compilation

## Requirements

* Install [Apache Maven 3.0.5] (https://maven.apache.org/) 
  * https://maven.apache.org/install.html
  * For Linux: sudo apt-get install maven
  * For Mac OS-X with Homebrew installed: brew install maven

* Install Java 1.8, 

  UPDATE JAVA_HOME to your installed java folder.

* Install [eclipse J2EE] (http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars2) (Optional: for development) 

* Install Postgresql database

* (https://github.com/zenvisage/zenvisage/blob/feature/QueryGraph/src/main/java/edu/uiuc/zenvisage/data/remotedb/readme.md)


## Building Code and Deployment

*  git clone https://github.com/zenvisage/zenvisage.git
*  cd zenvisage
*  sh build.sh 

## Install Postgres
(1) Download Postgres server on your local machine;

(2) Postgres server must contain a user with:

username  postgres
password: zenvisage

(3) Run clean up query and setup query:

DROP schema public cascade;
CREATE schema public;
CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);
CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);

## Deploy code
*  sh run.sh
*  http://localhost:8080/
*  Launch http://localhost:8080/chi-index.html

## Upload files
( Be aware that column names of csv&&txt table can not contain '-' 
(e.g.Foreclosures-Ratio, should be ForeclosuresRatio);
Upload (csv, txt) tuples (e.g. real_estate.csv, real_estate.txt) using the front-end file upload functionality;
The file upload back-end will auto creat csv table, insert metafilelocation table && metatable;

Now you can select the dataset from dropdown on front-end plotting and similarity search;







# Architecture

## Back-end

The zenvisage front-end interacts with the back-end via a REST
protocol. The back-end is implemented in Java and uses embedded Jetty
for the web server. 

The backe-end code is under src/main/java with the parent package edu.uiuc.zenvisage. 

* server: 
Contains the class for starting the webserver (embedded Jetty Server)

* api: 
Contains classes for REST Interface (registers request mappings).

* model: 
Contains the classes for key entitites in zenvisage

* service:
Contains the classes for analysis (e.g., similarity, representative)

* data:
Contains the classes for data storage and query execution

* zql:
Contains the classes for zql query execution (currently replicates some of the functionalities of above packages).



## Front-end
The front-end code is under src/main/webapp. 

* index.html. This is where all the static elements of the webpage are created. In addition, the dynamic elements are initialized in index.html, which are then populated through javscript. 

* dis/
This is the directory that contains the boostrap framework being used. 

* assets 
Directory for more Bootstrap things, and magicsuggest. 

* magicsugges/
The library used for making the comboboxes. 


* js Our own js files. 

* comboboxes.js: Our js to dynamically fill the comboboxes and also to keep track of them (for later submit usage).

* utils.js: helper for xdata.js

* xdata.js: The JS that helps link the front end to back end. Links up with the nodejs server in the getData() method. 

* vdb.js: File for angular.js framework needs. Currently no use of Angular in my code. 

* generate-vega-json.js: 
File contains some test charts from vega (testVega(), testScatter(), createScatter(), testBackend())
and also takes the backend data and displays charts (processBackEndData(), createBarGraph(), createLineGraph())


* vega-dynamic.html (not used): a test page for using the visualization toolkit, vega. 
schema2.json: A sample schema of the backend DB (not used currently). 
Other files in main directory are just for libraries to use. 


* vega-master (not used): Folder contains the code for the vega visualization library.
The examples directory could be useful for further vega work. 

