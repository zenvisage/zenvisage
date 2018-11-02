exports.config = {
  framework: 'jasmine',
  // seleniumAddress: 'http://localhost:4444/wd/hub',
  specs: ['query.spec.js','basicCheck.spec.js','dynamicClass.spec.js']

multiCapabilities:[
{
'browserName': 'internet explorer',
},
{
'browserName' : 'chrome',
},
{
'browserName' : 'firefox',
},
]

}
