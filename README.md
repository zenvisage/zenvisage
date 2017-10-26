# Zenvisage: An Interactive and Expressive Visual Analytics System 
Zenvisage is a visual data exploration system that can automatically identify and recommend visualizations that match desired user patterns. The user can specify at a high level what they are looking for either via interactions or via a query language (ZQL), and the system will perform the necessary computation to identify these visualizations.

### Version
The current version is 0.1.

### Features
Zenvisage enables users to effortlessly receive visualization recommendations for interesting trends, patterns, and insights from large datasets. Here are the key features of Zenvisage:

* Zenvisage users can directly _draw_ the trend-line they are looking for, and then rely on the system to find appropriate matches: for instance, a person browsing a dataset of material properties may be looking for those materials displaying a specific correlation between two given properties. Users can also _drag-and-drop_ trends onto the canvas and then subsequently modify the trend. Using this interface, users can specify the insights they are looking for, and expect Zenvisage to find matches, much like a "visualization search engine".

* Zenvisage supports a query language, called ZQL (pronounced "zee-quel"), short for Zenvisage Query Language, a flexible, powerful and intuitive mechanism to specify desired insights from visualizations. Using a small number of lines of ZQL, users can explore trends, patterns, and insights in any which way they desire.

* In addition to returning results for user-submitted queries, zenvisage runs a host of parallel queries to find the most typical and outlier trends for the subset of data the user is currently viewing and presents them as additional context for the user.

### Additional Readings
* Our project webpage is [here] [zenvisage-website]; regular updates will be posted at this webpage.
* Our VLDB'17 paper describing ZQL, our SmartFuse<sup>1</sup> ZQL optimizer, as well as a ZQL-centric user study is [here] [zenvisage-vldb].
* Our CIDR'17 paper describing the overall Zenvisage system, along with some target user scenarios is [here] [zenvisage-cidr].


### Required Software
* Java Platform (JDK) >= 8; once installed, update `JAVA_HOME` to your installed java folder.
* PostgreSQL >= 9.5;  many ways to install this, including an [app][postgres-installation] on Mac OSX.
* Apache Maven 3.0.5;  many ways to install this, including `brew install maven` on Mac OSX.

### Installation Instructions
The installation of Zenvisage is straightforward, assuming Postgres, Java, and Maven are installed. If you want to install using a Docker container, the instructions are [here][Docker-instructions].

* Clone the zenvisage repository. (Alternatively, you can download the source as a zip.)

     
        git clone https://github.com/zenvisage/zenvisage.git
     


* Configure Postgres. First, you need to install PostgresSQL successfully. (A tutorial on installing PostgresSQL on Mac OSX can be found [here][postgres-installation].) Zenvisage uses the default username -- `postgres`. Users need to create a password `zenvisage` for the user postgres. Thus, zenvisage uses the following username and password:     
        
          username: postgres
          password: zenvisage 
          
For making the above change, you could run the following commands:
            
            ALTER USER postgres WITH PASSWORD 'zenvisage';
            ALTER USER postgres WITH SUPERUSER;
              
* Update Database Schema
        
            DROP schema public cascade; CREATE schema public; CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT); CREATE TABLE zenvisage_metatable (tablename TEXT, attribute TEXT, type TEXT, axis TEXT, min FLOAT, max FLOAT, selectedX BOOLEAN, selectedY BOOLEAN, selectedZ BOOLEAN); CREATE TABLE zenvisage_dynamic_classes (tablename TEXT, attribute TEXT, ranges TEXT);CREATE TABLE dynamic_class_aggregations (Table_Name TEXT NOT NULL, Tag TEXT NOT NULL, Attributes TEXT NOT NULL, Ranges TEXT NOT NULL, Count INT NOT NULL);CREATE TABLE users (id TEXT, password TEXT);CREATE TABLE users_tables (users TEXT, tables TEXT);INSERT INTO users_tables (users, tables) VALUES ('public', 'cmu'), ('public', 'flights'),('public', 'real_estate'),('public', 'weather'),('public', 'real_estate_tutorial');

* Clean Postgres

            Postgres:
            psql -d postgres -U postgres
            \connect postgres;
            DROP schema public cascade;

* Data files location:
            Have you data folder under zenvisage folder, same level of src folder, name it data
            https://drive.google.com/drive/u/1/folders/0B3otFgGFeJnpVk96dEZqUnVaV2c

        
* In Terminal:

            git pull --rebase origin v2.0
            sudo rm -f -r target/
            sudo rm nohup.out
            sudo kill $(sudo lsof -t -i:8080)  
            
 * Build and deploy code. Inside the zenvisage folder,
 
            sudo sh build.sh
            sudo sh run.sh 

* Launch `http://localhost:8080/` (preferably in Chrome, if has error mostly because of uncleared cache, use incognito mode probably fix). 

### Dataset Upload Requirements

#### Dataset file

Currently, Zenvisage only accepts Comma-separated values (.csv) dataset file. The top row is attributes name and the following rows for data.

* Sample Dataset File

          location,month,dayofyear,year,temperature
          ABTIRANA,4,111,1997,55.4
          ABTIRANA,4,115,1997,56.8
          ABTIRANA,4,116,1997,61.5
          ABTIRANA,4,117,1997,60.8
          ABTIRANA,4,118,1997,57.2
          ABTIRANA,5,121,1997,66.2

#### Schema file

Schema file is a .txt file which specifies the way data are processed on backend and presented on graphs.

* Schema file format
          
          attribute's name:fundemental attribute's data type,indexed,x-axis,y-axis,z-axis,F,F,0,general attribute's data type

`attribute's name` - needs to be exactly the same as the attribute name in dataset file

`fundemental attribute's data type` - is the type of attribute in dataset (e.g string/int/float)

`indexed` -  currently detault value per design

`x-axis` - whether it would be shown on x-axis or not (e.g T/F)

`y-axis` - whether it would be shown on y-axis or not (e.g T/F)

`z-axis` - whether it would be shown on z-axis or not (e.g T/F)

`F` - currently detault value per design

`F` - currently detault value per design

`0` - currently detault value per design

`general attribute's data type` - is the type of attribute in dataset (e.g O/C/Q) (O for Ordinal, C for Categorical, Q for Qualitative)


* Sample Schema File

          location:string,indexed,F,F,T,F,F,0,C
          month:int,indexed,T,F,F,F,F,0,O
          dayofyear:int,indexed,T,F,F,F,F,0,O
          year:int,indexed,T,F,F,F,F,0,O
          temperature:float,indexed,F,T,F,F,F,0,O

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [prof]: http://web.engr.illinois.edu/~adityagp/#
   [zenvisage-website]: http://zenvisage.github.io/
   [zenvisage-vldb]: http://data-people.cs.illinois.edu/papers/zenvisage-vldb.pdf
   [zenvisage-cidr]: http://data-people.cs.illinois.edu/papers/zenvisage-cidr.pdf
   [postgressite]: https://www.postgresql.org/
   [postgres-installation]: https://chartio.com/resources/tutorials/how-to-start-postgresql-server-on-mac-os-x/
   [Docker-instructions]: https://github.com/zenvisage/zenvisage/wiki/Docker-Installation-Instruction-for-Mac
   <sup>1</sup>The smart-fuse optimization algorithms are not part of this release. Instead, we employ a simpler optimization scheme that works well for all but the most complex queries. 
   
### Data file uploading rules: 
   1. columns cannot be more than 1600; 
   2. column_name cannot have numbers in it; 
   3. no null values for fields;
   4. must be in .csv format;
