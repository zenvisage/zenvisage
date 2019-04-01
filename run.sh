#!bin/bash
cp lib/*.jar target/zenvisage/WEB-INF/lib/.
pkill -f ZvServer
cd target
java -cp "zenvisage/WEB-INF/lib/*:zenvisage-jar-with-dependencies.jar:classes/data" edu.uiuc.zenvisage.server.ZvServer

