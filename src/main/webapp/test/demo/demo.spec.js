var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Demo Test', function() {
  var originalTimeout;

  beforeEach(function() {
    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 1000000;
  });

  afterEach(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;
  });
  
  it('Check that score@rank n > score@rank m, where n<m.', function() {
    utils.initialize()
    checkDescendingResultScore();
  });    

  function checkDescendingResultScore(){
    // By default there is max 50 number of results, so we set 0<n<40 and create m by adding an integer 1<x<10
    var n = Math.floor(Math.random() * Math.floor(20));
    var x = Math.floor((Math.random() * ((19 + 1) - 1)) + 1);
    var m = n +x;
    // console.log(n,m)
    expect(n).toBeLessThan(m);
    element.all(by.css("#undraggable-result-"+n+">text")).get(0).getText().then(function(text){
      var rankN = utils.extractScoreFromResults(text);
      element.all(by.css("#undraggable-result-"+m+">text")).get(0).getText().then(function(text){
        var rankM = utils.extractScoreFromResults(text);
        // console.log(rankN["score"],rankM["score"])
        expect(rankM["score"]).toBeLessThanOrEqual(rankN["score"]);
      });
    });  
  }

  it("Smoothing Vary all Option + Sliders",function(){
    utils.initialize();
    var valLst  = ["movingaverage","exponentialmovingaverage","gaussian"]//"leossInterpolation",
    var sliderBar = element.all(by.css("#slider-range-max"));    
    
    for (var i = valLst.length - 1; i >= 0; i--) {
      utils.selectDropdownItem("#smoothing-form-control",valLst[i])
      for (var svar = 0 ; svar <=1; svar +=0.3){
        browser.executeScript("$('#slider-range-max').slider({value:"+svar +"}); $('#amount').val('"+svar +"') ; scope.callGetUserQueryResultsWithCallBack()");  
        browser.sleep(2000);
        // Check that at least the first result, representative, outlier is rendered
        expect(element(by.id("result-0")).isPresent()).toBe(true);
        expect(element(by.id("representative-result-0")).isPresent()).toBe(true);
        expect(element(by.id("outlier-result-0")).isPresent()).toBe(true);
      }
    }
  })

  
  it('Test Empty Filter', function() {
    utils.initialize()
    element(by.id('filter')).sendKeys("month<0");
    element(by.id('filter-submit')).click();
    browser.sleep(1000)
    // Check that error modal is present
    expect(element(by.id('errorModal')).isDisplayed()).toBe(true);
    // Click on show error
    element(by.css('#errorModalBody > p > button')).click();
    browser.sleep(500)
    // Check that error message is displayed
    expect(element(by.css('#errorModalText > pre')).getText()).toContain(
      "No resulting visualizations matches this filter. Please change the filter to relax the constraints.")
  }); 
});