#!bin/bash
# Script to run zenvisage with allocating 6GB maximum memory size for Java application (Run this script if you recieve HeapOutOfMemoryError)
pkill -f ZvServer
cd target
java  -Xmx6000M -cp "zenvisage/WEB-INF/lib/*:zenvisage-jar-with-dependencies.jar:classes/data" edu.uiuc.zenvisage.server.ZvServer
