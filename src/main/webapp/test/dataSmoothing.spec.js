var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Data Smoothing', function() {
  // Since the Vary all test takes longer than 60 seconds (original jasmine.DEFAULT_TIMEOUT_INTERVAL) to run, we extend this to 1000 second
  var originalTimeout;

  beforeEach(function() {
    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 1000000;
  });

  afterEach(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;
  });

  
  it("Vary all Option + Sliders",function(){
    utils.initialize();
    var valLst  = ["movingaverage","exponentialmovingaverage","gaussian"]//"leossInterpolation",
    var sliderBar = element.all(by.css("#slider-range-max"));    
    
    for (var i = valLst.length - 1; i >= 0; i--) {
      utils.selectDropdownItem("#smoothing-form-control",valLst[i])
      for (var svar = 0 ; svar <=1; svar +=0.1){
        browser.executeScript("$('#slider-range-max').slider({value:"+svar +"}); $('#amount').val('"+svar +"') ; scope.callGetUserQueryResultsWithCallBack()");  
        browser.sleep(3000);
        // Check that at least the first result, representative, outlier is rendered
        expect(element(by.id("result-0")).isPresent()).toBe(true);
        expect(element(by.id("representative-result-0")).isPresent()).toBe(true);
        expect(element(by.id("outlier-result-0")).isPresent()).toBe(true);
      }
    }
    // TODO: Not working drag and drop slider
    // browser.actions().dragAndDrop(sliderBar,{x:0.9,y:0}).perform(); 
    // "#slider-range-max > span"
    // browser.mouseDown(sliderBar).mouseMove({x:0.9,y:0}).mouseUp();
    // browser.actions()
    //   .mouseDown(sliderBar)
    //   .mouseMove({x: 10, y: 0}) 
    //   .mouseUp()
    //   .perform()
    // browser.actions().dragAndDrop(sliderBar,{x:100,y:0}).perform(); 
    // browser.sleep(10000);
  })
});
