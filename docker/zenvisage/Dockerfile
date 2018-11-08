## DOCKER Image 
FROM openjdk:10.0
LABEL maintainer="ABeltramo <beltramo.ale@gmail.com>, Matias Carrasco Kind <mgckind@gmail.com>"
# Install maven, supervisor and vi
RUN  apt-get update && apt-get install -y maven supervisor git

RUN mkdir /tmp/zenvisage
RUN rm -rf /tmp/zenvisage/*
RUN git clone https://github.com/zenvisage/zenvisage.git /tmp/zenvisage
WORKDIR /tmp/zenvisage/
# RUN git checkout v3.0
COPY config.properties /tmp/zenvisage/src/main/resources/config.properties
COPY PSQLDatabase.java /tmp/zenvisage/src/main/java/edu/uiuc/zenvisage/zqlcomplete/executor/PSQLDatabase.java
COPY SQLQueryExecutor.java /tmp/zenvisage/src/main/java/edu/uiuc/zenvisage/data/remotedb/SQLQueryExecutor.java

RUN sh build.sh

COPY run-slow.sh /tmp/zenvisage/run.sh

CMD ["sh", "run.sh"]
