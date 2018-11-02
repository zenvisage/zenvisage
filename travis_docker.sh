sh scripts/run-test.sh
cd docker
docker-compose up &
 # give zenvisage some time to start
sleep 50

cd .. 

#start selenium and protractor 
cd src/main/webapp/test
npx webdriver-manager update --gecko=false
echo 'update finished'
webdriver-manager start --standalone &
protractor conf.js

