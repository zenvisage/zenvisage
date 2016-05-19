# Overview
zenvisage is designed as a lightweight web-based client application. It performs two major functions: First, it pro-
vides the analyst an intuitive graphical interface for exploring trends and insights in data. Second, it takes the results of these queries from the back-end and encodes them into the most effective visualizations.

# Compilation

## Requirements

* Install [Apache Maven 3.0.5] (https://maven.apache.org/) 
* * sudo apt-get install maven 

* Install Java 1.8

* Install [eclipse J2EE] (http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars2) (Optional: for development) 

## Building Code and Deployment

*  git clone https://github.com/zenvisage/zenvisage.git
*  cd zenvisage
*  sh build.sh  
*  sh run.sh

# Architecture

## Back-end

The zenvisage front-end interacts with the back-end via a REST
protocol. The back-end is implemented in Java and uses embedded Jetty
for the web server. 


## Front-end
