exports.config = {
  framework: 'jasmine',
  // seleniumAddress: 'http://localhost:4444/wd/hub',
  specs: ['basicCheck.spec.js','query.spec.js','dynamicClass.spec.js','dataSmoothing.spec.js'],//'filter.spec.js'
  capabilities: {'browserName': 'chrome', 'acceptInsecureCerts': true,
  chromeOptions: {
     args: [ "--headless","--silent","--log-level=3"]
   }
},
	//capabilities: {'acceptInsecureCerts': 'true'}
  restartBrowserBetweenTests:true,
  getPageTimeout: 30000,
  allScriptsTimeout: 30000
}
