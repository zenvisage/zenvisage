echo "started travis_shell.sh"
# mv target/classes/data target/classes/data_cpy
# cp -r target/classes/data_cpy target/classes/data
#chmod 666 target/classes/data
echo "before"
ls -l target/classes/data
find target/classes/data -type f -exec chmod 666 {} \;
echo "after"
ls -l target/classes/data
 
bash run.sh &
 # give zenvisage some time to start
sleep 10

echo 'run JUnit test'
bash scripts/run-test.sh 
#start selenium and protractor 
 cd src/main/webapp/test
 webdriver-manager update
 webdriver-manager start &
 protractor conf.js

