## DOCKER Image with postgres 9.6, Java and Zenvisage
FROM postgres:9.6
MAINTAINER Matias Carrasco Kind <mgckind@gmail.com>
# Using postgres as base image, ideally DB should be running in a separate container
# postgres  images is based on debian
# add webupd8 repository to install Java
RUN \
    echo "===> add webupd8 repository..."  && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886  && \
    apt-get update  && \
    \
    \
    echo "===> install Java"  && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --force-yes oracle-java8-installer oracle-java8-set-default  && \
    \
    \
    echo "===> clean up..."  && \
    rm -rf /var/cache/oracle-jdk8-installer  && \
    apt-get clean  && \
    rm -rf /var/lib/apt/lists/* 
# Install maven, supervisor and vi
RUN  apt-get update && apt-get install -y maven supervisor vim git
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
RUN mkdir /home/postgres
# Add zenvisage (can also pull from master directly), zenvisage folder needs to be on docker building folder
#ADD zenvisage/ /home/postgres/zenvisage/ 
WORKDIR /home/postgres
RUN git clone https://github.com/zenvisage/zenvisage.git &&  (cd zenvisage/ && git checkout v2.0)
WORKDIR /home/postgres/zenvisage
RUN chown -R postgres:postgres /home/postgres
USER postgres
# run zenvisage build script
RUN sh build.sh
# Create startup script with postgres configuration and run the script
RUN echo "#!/bin/bash " >> startup.sh
RUN echo "set -e " >> startup.sh
RUN echo "nohup /usr/local/bin/docker-entrypoint.sh postgres &" >> startup.sh
RUN echo "sleep 6 " >> startup.sh
RUN echo "psql -c \"ALTER USER postgres WITH PASSWORD 'zenvisage';\" " >> startup.sh
RUN echo "psql -c \"ALTER USER postgres WITH SUPERUSER;\" " >> startup.sh
RUN echo "psql -c \"DROP schema public cascade; CREATE schema public;\" " >> startup.sh
RUN echo "psql -c \"CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);\" " >> startup.sh
RUN echo "psql -c \"CREATE TABLE zenvisage_metatable (tablename TEXT, attribute TEXT, type TEXT, axis TEXT, min FLOAT, max FLOAT);\" " >> startup.sh
RUN echo "psql -c \"CREATE TABLE zenvisage_dynamic_classes (tablename TEXT, attribute TEXT, ranges TEXT);\" " >> startup.sh
RUN echo "sh LMemRun.sh " >> startup.sh
# Run the startup script when creating container
CMD ["sh","/home/postgres/zenvisage/startup.sh"]
