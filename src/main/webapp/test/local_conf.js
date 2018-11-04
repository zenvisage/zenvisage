exports.config = {
  framework: 'jasmine',
  seleniumAddress: 'http://localhost:4444/wd/hub',
  capabilities: {
    browserName: 'chrome'
  },
  restartBrowserBetweenTests:true,

  specs: ['basicCheck.spec.js','query.spec.js','dynamicClass.spec.js','dataSmoothing.spec.js']
}
