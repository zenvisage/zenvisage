#sh scripts/run-test.sh
cd docker
docker-compose up &
 # give zenvisage some time to start
sleep 10

cd .. 

#start selenium and protractor 
cd src/main/webapp/test
npx webdriver-manager update
webdriver-manager start --standalone &
protractor conf.js

