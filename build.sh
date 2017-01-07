#!bin/bash
mvn clean
mvn install
cp lib/*.jar target/zenvisage/WEB-INF/lib/.
