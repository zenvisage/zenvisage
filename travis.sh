mvn clean
if [[ "$?" -ne 0 ]] ; then
  echo 'mvn clean failed. Exited with non-zero value';exit 42
fi
mvn install
if [[ "$?" -ne 0 ]] ; then
  echo 'mvn install failed. Exited with non-zero value '; exit 42
fi
cp lib/*.jar target/zenvisage/WEB-INF/lib/.

sudo sh run.sh
sleep 10 # give zenvisage some time to start
cd src/main/webapp
webdriver-manager start
protractor conf.js
