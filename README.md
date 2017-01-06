# Zenvisage: An Interactive and Expressive Visual Analytics System 
Zenvisage is a visual exploration system that can automatically identify and recommend interesting visualizations. The user can specify at a high level what they are looking for, and the system will do the rest.

### Version
The current version is 0.1.

### Features
Zenvisage enables users to effortlessly receive visualization recommendations for interesting trends, patterns, and insights from large datasets. Here are the key features of Zenvisage:

* Zenvisage users can directly draw the trend-line they are looking for, and then rely on the system to find appropriate matches: for instance, a person browsing a dataset of material properties may be looking for those materials displaying a specific correlation between two properties. Users can also drag-and-drop trends onto the canvas and then subsequently modify the trend. Using this interface, users can specify the insights they are looking for, and expect Zenvisage to find matches, much like a "visualization search engine".

* Zenvisage supports a query language, called ZQL (pronounced "zee-quel"), short for Zenvisage Query Language, a flexible and intuitive mechanism to specify desired insights from visualizations. Using a small number of ZQL lines, users can explore trends, patterns, and insights in any which way they desire.

* In addition to returning results for user-submitted queries, zenvisage runs a host of parallel queries to find the most typical and outlier trends for that subset of data the user is currently viewing and presents them as visualization recommendations.

### Additional Readings
* [Project webpage] [zenvisage-website]
* [VLDB'17 Paper] [zenvisage-vldb]
* [CIDR'17 Paper] [zenvisage-cidr]


### Required Software
* Java Platform (JDK) >= 8, UPDATE JAVA_HOME to your installed java folder.
* PostgreSQL >= 9.5
* Apache Maven 3.0.5, brew install maven

### Installation Instructions
* Clone the zenvisage repository (Alternatively, you can download the source as a zip.). 

     
        git clone https://github.com/zenvisage/zenvisage.git
     


* Configure Postgres.Users need to install PostgresSQL successfully. (A tutorial of installing PostgresSQL on Mac OSX can be found here.) Zenvisage uses the default username -- postgres. Users need to create a password “zenvisage” for the user postgres. Thus, zenvisage uses the following username and password:     
        
          username: postgres
          password: zenvisage 
          
 For changing or adding the password to username "postgres", you could run the following commands:
            
            sudo -u postgres psql
            \password
              
 
* Build and deploy code. Inside zenvisage folder,
    
        
          sh build.sh.   
        
* Run 
            
          sh run.sh
        
  
* Launch http://localhost:8080/ (preferably in chrome). 

License
----

MIT


[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [prof]: http://web.engr.illinois.edu/~adityagp/#
   [zenvisage-website]: http://zenvisage.github.io/
   [zenvisage-vldb]: http://data-people.cs.illinois.edu/papers/zenvisage-vldb.pdf
   [zenvisage-cidr]: http://data-people.cs.illinois.edu/papers/zenvisage-cidr.pdf
   [postgressite]: https://www.postgresql.org/
   [postgres-installation]: https://chartio.com/resources/tutorials/how-to-start-postgresql-server-on-mac-os-x/
   <sup>1</sup>The smart-fuse optimization algorithms are not part of this release.
