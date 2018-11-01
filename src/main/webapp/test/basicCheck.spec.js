var Utils = require('./testUtils.js');
var utils = new Utils();


describe('Basic Checks', function() {
  it('Title Equality Test', function() {
    utils.initialize()
    expect(browser.getTitle()).toEqual('Zenvisage');
  });  
 //  it("Data Smoothing",function(){
 //   utils.initialize()
  // var sliderBar = element.all(by.css("#slider-range-max"));
 //    browser.actions().dragAndDrop(sliderBar,{x:100,y:0}).perform(); 
 //    browser.sleep(10000);
 //    console.log(sliderBar.getAttribute('value'))
 //    // expect(sliderBar.getAttribute('value')).toEqual('7');
 //  });
   it("Switch to different datasets and ensuring basic checks satisfied",function(){
    utils.initialize();
    // console.log(element.all(by.css('#dataset-form-control option')).count());
    var allOptions = element.all(by.css('#dataset-form-control option'))
    var totalOptionsCount = allOptions.count()
    expect(allOptions.count()).toEqual(5)
    for (i = 1; i <= 5; i++) {
      // Click open the drop down menu
      element(by.id('dataset-form-control')).click();
      //Select the second item on the drop down menu
      element(by.css('#dataset-form-control > option:nth-child('+i+')')).click();
      // Click on the side to close the drop down menu
      browser.actions().mouseMove({x: 100, y: 0}).click().perform(); 
      // trigger on dataset change
      browser.executeScript("var scope = angular.element('#dataset-form-control').scope();scope.onDatasetChange();")
      // .then(function(){
      //   basicChecks()  
      // });
      // Perform all the basic checks after outlier is rendered (rendering order is result --> representative --> outliers)
      var outlier = element(by.id("outlier-result-0"))
      outlier.isPresent().then(function(){
        basicChecks()
      })
    }
  });
  ////////////////////////////////////////////////////////////
  //// Test Oracle as Self (Visual Examination Hard Coded)////
  ////////////////////////////////////////////////////////////

  // // Testing a basic interaction-independent function (TODO: Trouble with importing package)
  // it('should succeed', function() {
  //   // var Helper = require("./js/helper.js");
  //   var arr = separateTwoArrays([[1,2,3],[2,2,3]])
  //   // expect(arr).toEqual([[[1,2][2,2]],[[1,3][2,3]]]);
  //   expect(arr).toEqual([[[1,2],[2,2]],[[1,3],[2,3]]]);
  //   // expect(arr).toEqual([1,2,3]);
  // });

  /////////////////////////////////////////////////////
  ////Potentially tests to be ran every single time////
  /////////////////////////////////////////////////////
  it('Check last similarity above cutoff', function() {
    // TODO: Vary similarity cutoff, check that the last ranked item is larger or equal to the similarity cutoff
    expect(1).toEqual(1)
  });

  it('Check that score@rank n > score@rank m, where n<m.', function() {
    utils.initialize(); 
    checkDescendingResultScore();
  });    


  function basicChecks(){
    console.log("basic checked")
    checkDescendingResultScore();
  }
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


  // When set filter as city='Bristol', drag and drop first ranked item
  // Several TODOs : 
  // 0) Figure out how to do dataset changing before scope call? This doesn't seem to work: 
  //  $('#dataset-form-control option').val('real_estate');
  //  element(by.css('dataset-form-control option'))
  // 1) Why is the Cluster results for real_estate tutorial different in Selenium compared to the actual interface (Expected 'Barnegat Township (877 more like this)' to equal 'Catonsville (56 more like this)')
  // 2) Initialize with different dataset, check representative and outlier corresponds to the "correct visualizations"
  // 3) Write high level functions for checking equality for Rank 1, Rank 1 Cluster, Rank j Outlier, etc
  // 4) Check that scores are always between 0 and 1 
  // 5) Testing querying input modalities: 
  //    - Pattern Query 
  // 6) Smoothing: 
  //    - change algorithm --> vary smoothing constant
  // Done: 
  // 1) Clean up required initialization to function call , called everytime
});