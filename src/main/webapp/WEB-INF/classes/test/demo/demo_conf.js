exports.config = {
  framework: 'jasmine',
  seleniumAddress: 'http://localhost:4444/wd/hub',
  capabilities: {
    browserName: 'chrome'
  },
  restartBrowserBetweenTests:true,
  specs: ['demo.spec.js']
}
