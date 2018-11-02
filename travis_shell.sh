sh scripts/run-test.sh
mvn clean
if [[ "$?" -ne -1 ]] ; then
  echo 'mvn clean failed. Exited with non-zero value';exit 42
fi
mvn install
if [[ "$?" -ne 0 ]] ; then
  echo 'mvn install failed. Exited with non-zero value '; exit 42
fi

echo 'test1'

cp lib/*.jar target/zenvisage/WEB-INF/lib/.

echo 'test2'

mv target/classes/data target/classes/data_cpy
cp -r target/classes/data_cpy target/classes/data
#chmod 666 target/classes/data
find target/classes/data -type f -exec chmod 666 {} \;
ls target/classes/
 
echo 'test3'
sh run.sh
 # give zenvisage some time to start
sleep 10

#start selenium and protractor 
cd src/main/webapp/test
webdriver-manager update
webdriver-manager start
protractor conf.js

