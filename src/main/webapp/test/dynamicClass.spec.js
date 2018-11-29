var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Dynamic Class', function() {
  // Since the Vary all test takes longer than 60 seconds (original jasmine.DEFAULT_TIMEOUT_INTERVAL) to run, we extend this to 1000 second
  var originalTimeout;

  beforeEach(function() {
    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 1000000;
  });

  afterEach(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;
  });

  it("Testing Basic Two DCC creation",function(){
    utils.initialize();
    // Click + to open the DCC modal
    element(by.id("dynamicClassButton")).click();
    browser.sleep(500)
    element(by.css("#dynamic-class-row-1 > div.col-md-5")).click();
    var monthSelection = element(by.css("#dynamic-class-row-1 > div.col-md-5 > div > select > option:nth-child(2)"));
    expect(monthSelection.getText()).toEqual("month");
    monthSelection.click();
    element(by.css("#dynamic-class-row-1 > div.col-md-7 > input")).sendKeys("[min,6],[6,max]");
    // browser.sleep(50000);
    element(by.id("dynamic-class-submit-button")).click();
    // Load Dynamic Class
    element(by.id("load-dynamic-class-button")).click()

    var xArr = browser.executeScript(function(res){
       var arr  = userQueryDygraphsNew['result-0']['data']
       var xArr = arr.map(function(x){
          return x[Object.keys(x)[0]];
       });
       return res
    })
    expect(xArr).toBeLessThan(6);
  })
});
