exports.config = {
  framework: 'jasmine',
  // seleniumAddress: 'http://localhost:4444/wd/hub',
  specs: ['query.spec.js','basicCheck.spec.js','dynamicClass.spec.js'],
  capabilities: {'browserName': 'chrome', 'acceptInsecureCerts': true},
	//capabilities: {'acceptInsecureCerts': 'true'}
  getPageTimeout: 30000

}
