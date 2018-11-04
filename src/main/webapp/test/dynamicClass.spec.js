var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Dynamic Class', function() {
  it("Testing Basic Two DCC creation",function(){
    utils.initialize();
    // Click + to open the DCC modal
    element(by.id("classCreationButton")).click();
    browser.sleep(500)
    element(by.css("#class-row-1 > div.col-md-5 > div > select")).click();
    var monthSelection = element(by.css("#class-row-1 > div.col-md-5 > div > select > option:nth-child(2)"));
    expect(monthSelection.getText()).toEqual("month");
    monthSelection.click();
    element(by.css("#class-row-1 > div.col-md-7 > input")).sendKeys("[min,6],[6,max]");
    // browser.sleep(50000);
    element(by.id("class-creation-submit-button")).click();

    // Click to change Category axis dropdown (to select dynamic_class)
    var elem = element(by.id("zAxisSelection"))
    browser.actions().mouseMove(elem).click().perform();
    
    var DCSelection = element(by.css("#zAxisSelection > option:nth-child(1)"))
    expect(DCSelection.getText()).toEqual("dynamic_class");
    DCSelection.click();

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
