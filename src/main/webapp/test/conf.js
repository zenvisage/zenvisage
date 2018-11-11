exports.config = {
  framework: 'jasmine',
  // seleniumAddress: 'http://localhost:4444/wd/hub',
  specs: ['query.spec.js','basicCheck.spec.js'],
  capabilities: {'browserName': 'chrome', 'acceptInsecureCerts': true,
  chromeOptions: {
     args: [ "--headless,"--silent","--log-level=3"]
   }
},
	//capabilities: {'acceptInsecureCerts': 'true'}
  getPageTimeout: 30000,
  allScriptsTimeout: 30000
}
