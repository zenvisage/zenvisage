describe('Zenvisage', function() {
  // it('should have a title', function() {
  //   browser.get('http://localhost:80/');
  //   expect(browser.getTitle()).toEqual('Zenvisage');
  // });
  it('Title Equality Test', function() {
    
    // implicit and page load timeouts
    browser.manage().timeouts().pageLoadTimeout(400000);
    browser.manage().timeouts().implicitlyWait(250000);

    browser.get('http://127.0.01:80/');
    expect(browser.getTitle()).toEqual('Zenvisage');
  });
  // // Testing a basic interaction-independent function (TODO: Trouble with importing package)
  // it('should succeed', function() {
  //   // var Helper = require("./js/helper.js");
  //   var arr = separateTwoArrays([[1,2,3],[2,2,3]])
  //   // expect(arr).toEqual([[[1,2][2,2]],[[1,3][2,3]]]);
  //   expect(arr).toEqual([[[1,2],[2,2]],[[1,3],[2,3]]]);
  //   // expect(arr).toEqual([1,2,3]);
  // });

  it('Input equation first rank should be Plymouth', function() {
    // implicit and page load timeouts
    browser.manage().timeouts().pageLoadTimeout(4000000);
    browser.manage().timeouts().implicitlyWait(2500000);

    browser.driver.manage().timeouts().implicitlyWait(50000);
    browser.get('http://localhost:80/');

    // Initialize the dataset with real_estate
    browser.executeScript("$('#dataset-form-control option').val('real_estate');var scope = angular.element('#dataset-form-control').scope();scope.onDatasetChange();")

    element(by.model('equation')).sendKeys("y=x^2");
    element(by.xpath('//button[. = "add"]')).click();

    element(by.css('text[count="1"]')).getText().then(function(text){
      expect(text).toEqual('city: Plymouth (0.383)');
    });
  });
  // Several TODOs : 
  // 1) Clean up required initialization to function call , called everytime
  // 2) Initialize with different dataset, check representative and outlier corresponds to the "correct visualizations"
  // 3) Write high level functions for checking equality for Rank 1, Rank 1 Cluster, Rank j Outlier, etc
  // 4) Check that scores are always between 
  // 5) Testing querying input modalities: 
  //    - Pattern Query 
  // 6) Smoothing: 
  //    - change algorithm --> vary smoothing constant

});



// var origFn = browser.driver.controlFlow().execute;
//
// browser.driver.controlFlow().execute = function() {
//   var args = arguments;
//
//   // queue 100ms wait
//   origFn.call(browser.driver.controlFlow(), function() {
//     return protractor.promise.delayed(100);
//   });
//
//   return origFn.apply(browser.driver.controlFlow(), args);
// };
