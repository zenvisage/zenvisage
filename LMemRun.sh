#!bin/bash
pkill -f ZvServer
cd target
java  -Xmx2000M -cp "zenvisage/WEB-INF/lib/*:zenvisage-jar-with-dependencies.jar:classes/data" edu.uiuc.zenvisage.server.ZvServer
